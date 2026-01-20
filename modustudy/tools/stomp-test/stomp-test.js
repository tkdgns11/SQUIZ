import { Client } from '@stomp/stompjs';
import WebSocket from 'ws';

const wsUrl = process.env.STOMP_URL || 'ws://localhost:8080/ws/websocket';
const roomId = process.env.ROOM_ID || 'room-1';
const displayName = process.env.DISPLAY_NAME || 'tester';

const client = new Client({
  brokerURL: wsUrl,
  reconnectDelay: 3000,
  heartbeatIncoming: 10000,
  heartbeatOutgoing: 10000,
  webSocketFactory: () => new WebSocket(wsUrl)
});

client.onConnect = () => {
  console.log('[stomp] connected');

  client.subscribe(`/topic/rooms/${roomId}/events`, (message) => {
    console.log('[topic events]', message.body);
  });

  client.subscribe(`/user/queue/rooms/${roomId}/history`, (message) => {
    console.log('[queue history]', message.body);
  });

  client.publish({
    destination: `/app/rooms/${roomId}/join`,
    body: JSON.stringify({ displayName })
  });

  setTimeout(() => {
    client.publish({
      destination: `/app/rooms/${roomId}/chat`,
      body: JSON.stringify({ sender: displayName, text: 'hello from stomp test' })
    });
  }, 500);

  setTimeout(() => {
    client.publish({
      destination: `/app/rooms/${roomId}/presenter`,
      body: JSON.stringify({ displayName, action: 'claim' })
    });
  }, 1000);

  setTimeout(() => {
    client.deactivate();
  }, 8000);
};

client.onStompError = (frame) => {
  console.error('[stomp error]', frame.headers['message']);
  console.error(frame.body);
};

client.onWebSocketError = (error) => {
  console.error('[ws error]', error);
};

client.activate();
