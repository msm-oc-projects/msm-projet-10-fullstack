import { Client } from "@stomp/stompjs";

const roomId = "demo";
const clientMessageId = crypto.randomUUID();
const timeoutMs = 10000;

const client = new Client({
  brokerURL: "ws://localhost:8080/ws",
  reconnectDelay: 0,
});

try {
  const received = await new Promise((resolve, reject) => {
    const timeout = setTimeout(
      () => reject(new Error("Smoke test timed out.")),
      timeoutMs,
    );

    client.onConnect = () => {
      client.subscribe(`/topic/chat/${roomId}`, (frame) => {
        const message = JSON.parse(frame.body);
        if (message.clientMessageId === clientMessageId) {
          clearTimeout(timeout);
          resolve(message);
        }
      });

      client.publish({
        destination: `/app/chat/${roomId}/send`,
        body: JSON.stringify({
          clientMessageId,
          author: "Smoke Test",
          content: `Smoke test ${clientMessageId}`,
        }),
      });
    };

    client.onStompError = (frame) => {
      clearTimeout(timeout);
      reject(new Error(frame.headers.message ?? "STOMP error"));
    };

    client.onWebSocketError = () => {
      clearTimeout(timeout);
      reject(new Error("WebSocket connection failed."));
    };

    client.activate();
  });

  const response = await fetch(
    `http://localhost:8080/api/v1/chat/rooms/${roomId}/messages?limit=100`,
  );
  const history = await response.json();
  const persisted = history.some(
    (entry) => entry.clientMessageId === received.clientMessageId,
  );

  if (!persisted) {
    throw new Error("Message was broadcast but not found in history.");
  }

  console.log("Smoke test passed: message persisted and broadcast.");
} finally {
  await client.deactivate();
}
