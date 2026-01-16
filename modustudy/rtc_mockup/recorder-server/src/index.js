const express = require('express');
const fs = require('fs');
const path = require('path');
const config = require('./config');

const app = express();
app.use(express.json());

const sessions = new Map();

function ensureDir(dir) {
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
  }
}

function createMockFiles(recordingId) {
  ensureDir(config.outputDir);
  const videoPath = path.join(config.outputDir, `${recordingId}.mp4`);
  const audioPath = path.join(config.outputDir, `${recordingId}.wav`);
  if (!fs.existsSync(videoPath)) {
    fs.writeFileSync(videoPath, '');
  }
  if (!fs.existsSync(audioPath)) {
    fs.writeFileSync(audioPath, '');
  }
  return { videoPath, audioPath };
}

async function notifyComplete(recordingId, videoUrl, audioUrl) {
  if (typeof fetch !== 'function') {
    return;
  }
  const payload = { recordingId, videoUrl, audioUrl };
  await fetch(`${config.springBaseUrl}/api/recordings/complete`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
}

app.post('/recordings/start', (req, res) => {
  const { roomId, recordingId } = req.body || {};
  sessions.set(recordingId, { roomId, startedAt: Date.now() });
  res.json({ status: 'RECORDING' });
});

app.post('/recordings/stop', async (req, res) => {
  const { recordingId } = req.body || {};
  const session = sessions.get(recordingId);
  if (!session) {
    res.status(404).json({ error: 'recording not found' });
    return;
  }
  sessions.delete(recordingId);
  let videoUrl = null;
  let audioUrl = null;
  if (config.mockFiles) {
    const files = createMockFiles(recordingId);
    videoUrl = files.videoPath;
    audioUrl = files.audioPath;
    try {
      await notifyComplete(recordingId, videoUrl, audioUrl);
    } catch (err) {
      // ignore callback errors in mock mode
    }
  }
  res.json({ status: 'UPLOADING', videoUrl, audioUrl });
});

app.get('/health', (_req, res) => {
  res.json({ status: 'ok' });
});

app.listen(config.port, () => {
  // eslint-disable-next-line no-console
  console.log(`Recorder server listening on ${config.port}`);
});
