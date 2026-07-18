import { HttpClient } from '@angular/common/http';
import { DestroyRef, Injectable, computed, inject, signal } from '@angular/core';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import {
  ChatMessage,
  ConnectionStatus,
  OutgoingChatMessage,
} from './chat-message';

@Injectable({ providedIn: 'root' })
export class ChatService {
  private readonly http = inject(HttpClient);
  private readonly destroyRef = inject(DestroyRef);
  private readonly messagesState = signal<ChatMessage[]>([]);
  private readonly statusState = signal<ConnectionStatus>('disconnected');
  private readonly errorState = signal('');
  private client?: Client;
  private subscriptions: StompSubscription[] = [];

  readonly messages = this.messagesState.asReadonly();
  readonly status = this.statusState.asReadonly();
  readonly error = this.errorState.asReadonly();
  readonly isConnected = computed(() => this.statusState() === 'connected');

  constructor() {
    this.destroyRef.onDestroy(() => this.disconnect());
  }

  join(roomId: string): void {
    this.disconnect();
    this.messagesState.set([]);
    this.errorState.set('');
    this.statusState.set('connecting');
    this.loadHistory(roomId);

    this.client = new Client({
      brokerURL: this.webSocketUrl(),
      reconnectDelay: 3000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      onConnect: () => {
        this.statusState.set('connected');
        this.errorState.set('');
        this.subscriptions = [
          this.client!.subscribe(`/topic/chat/${roomId}`, (frame) =>
            this.receive(frame),
          ),
          this.client!.subscribe(`/topic/chat/${roomId}/errors`, (frame) =>
            this.receiveError(frame),
          ),
        ];
      },
      onDisconnect: () => this.statusState.set('disconnected'),
      onWebSocketClose: () => {
        if (this.client?.active) {
          this.statusState.set('connecting');
          this.errorState.set('Connexion perdue. Nouvelle tentative en cours.');
        } else {
          this.statusState.set('disconnected');
        }
      },
      onStompError: () => {
        this.errorState.set('Le serveur de tchat a refusé la connexion.');
      },
    });

    this.client.activate();
  }

  send(roomId: string, message: OutgoingChatMessage): void {
    if (!this.client?.connected) {
      this.errorState.set("Le message n'a pas été envoyé : le tchat est déconnecté.");
      return;
    }

    this.errorState.set('');
    this.client.publish({
      destination: `/app/chat/${roomId}/send`,
      body: JSON.stringify(message),
    });
  }

  disconnect(): void {
    this.subscriptions.forEach((subscription) => subscription.unsubscribe());
    this.subscriptions = [];
    if (this.client) {
      void this.client.deactivate();
      this.client = undefined;
    }
    this.statusState.set('disconnected');
  }

  clearError(): void {
    this.errorState.set('');
  }

  private loadHistory(roomId: string): void {
    this.http
      .get<ChatMessage[]>(`/api/v1/chat/rooms/${roomId}/messages`, {
        params: { limit: 50 },
      })
      .subscribe({
        next: (messages) => this.mergeMessages(messages),
        error: () =>
          this.errorState.set(
            "L'historique n'est pas disponible. Le tchat peut néanmoins se reconnecter.",
          ),
      });
  }

  private receive(frame: IMessage): void {
    const message = JSON.parse(frame.body) as ChatMessage;
    this.mergeMessages([message]);
  }

  private receiveError(frame: IMessage): void {
    const payload = JSON.parse(frame.body) as { message?: string };
    this.errorState.set(payload.message ?? "Le message n'a pas été accepté.");
  }

  private mergeMessages(incoming: ChatMessage[]): void {
    const byId = new Map(
      this.messagesState().map((message) => [message.id, message]),
    );
    incoming.forEach((message) => byId.set(message.id, message));
    this.messagesState.set(
      [...byId.values()].sort((left, right) =>
        left.sentAt.localeCompare(right.sentAt),
      ),
    );
  }

  private webSocketUrl(): string {
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    return `${protocol}//${window.location.host}/ws`;
  }
}
