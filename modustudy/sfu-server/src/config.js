const path = require('path');

const config = {
  port: Number(process.env.PORT || 4000),
  listenIp: process.env.LISTEN_IP || '0.0.0.0',
  announcedIp: process.env.ANNOUNCED_IP || process.env.SFU_ANNOUNCED_IP || '',
  corsOrigins: (process.env.CORS_ORIGINS || process.env.SFU_CORS_ORIGINS || '*')
    .split(',')
    .map((origin) => origin.trim())
    .filter((origin) => origin.length > 0),
  sslKeyPath: process.env.SFU_SSL_KEY_PATH
    || path.resolve(__dirname, '..', 'certs', 'key.pem'),
  sslCertPath: process.env.SFU_SSL_CERT_PATH
    || path.resolve(__dirname, '..', 'certs', 'cert.pem'),
  rtcMinPort: Number(process.env.RTC_MIN_PORT || 20000),
  rtcMaxPort: Number(process.env.RTC_MAX_PORT || 20100),
  mediaCodecs: [
    {
      kind: 'audio',
      mimeType: 'audio/opus',
      clockRate: 48000,
      channels: 2
    },
    {
      kind: 'video',
      mimeType: 'video/VP8',
      clockRate: 90000,
      parameters: {
        'x-google-start-bitrate': 1000
      }
    }
  ]
};

module.exports = config;
