const path = require('path');

const config = {
  port: Number(process.env.RECORDER_PORT || 9100),
  outputDir: process.env.RECORDER_OUTPUT_DIR || path.resolve(__dirname, '..', 'output'),
  springBaseUrl: process.env.RECORDER_SPRING_BASE_URL || 'http://localhost:8080',
  mockFiles: process.env.RECORDER_MOCK_FILES !== 'false'
};

module.exports = config;
