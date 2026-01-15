const config = {
  port: Number(process.env.PORT || 4000),
  listenIp: process.env.LISTEN_IP || '0.0.0.0',
  announcedIp: process.env.ANNOUNCED_IP || '127.0.0.1',
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
