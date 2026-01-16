# Recorder Server (Mock)

This is a minimal recorder service scaffold for local tests.
It does not capture real media yet. It can create empty files to simulate output.

## Run

```bash
npm install
npm start
```

## Environment

- `RECORDER_PORT` (default: 9100)
- `RECORDER_OUTPUT_DIR` (default: ./output)
- `RECORDER_SPRING_BASE_URL` (default: http://localhost:8080)
- `RECORDER_MOCK_FILES` (default: true)

## API

- POST `/recordings/start`
  - body: `{ "roomId": "...", "recordingId": 1 }`
- POST `/recordings/stop`
  - body: `{ "recordingId": 1 }`

If `RECORDER_MOCK_FILES` is true, the server writes empty files and
posts back to Spring `/api/recordings/complete`.
