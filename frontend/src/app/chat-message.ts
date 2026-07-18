export interface ChatMessage {
  id: string;
  clientMessageId: string;
  roomId: string;
  author: string;
  content: string;
  sentAt: string;
}

export interface OutgoingChatMessage {
  clientMessageId: string;
  author: string;
  content: string;
}

export type ConnectionStatus = 'disconnected' | 'connecting' | 'connected';
