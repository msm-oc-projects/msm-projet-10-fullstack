import { DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ChatService } from './chat.service';

@Component({
  selector: 'app-root',
  imports: [DatePipe, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  private readonly chat = inject(ChatService);
  protected readonly roomId = 'demo';
  protected readonly messages = this.chat.messages;
  protected readonly status = this.chat.status;
  protected readonly error = this.chat.error;
  protected readonly isConnected = this.chat.isConnected;
  protected readonly author = signal('');
  protected readonly content = signal('');
  protected readonly joined = signal(false);
  protected readonly announcement = signal('');
  protected readonly remainingCharacters = computed(
    () => 500 - this.content().length,
  );

  protected join(): void {
    const normalizedAuthor = this.author().trim();
    if (normalizedAuthor.length < 2 || normalizedAuthor.length > 40) {
      this.chat.clearError();
      this.announcement.set('Le pseudonyme doit contenir entre 2 et 40 caractères.');
      return;
    }

    this.author.set(normalizedAuthor);
    this.joined.set(true);
    this.announcement.set('Connexion au salon de démonstration.');
    this.chat.join(this.roomId);
  }

  protected send(): void {
    const normalizedContent = this.content().trim();
    if (!normalizedContent || normalizedContent.length > 500) {
      this.announcement.set('Le message doit contenir entre 1 et 500 caractères.');
      return;
    }

    this.chat.send(this.roomId, {
      clientMessageId: crypto.randomUUID(),
      author: this.author(),
      content: normalizedContent,
    });
    this.content.set('');
    this.announcement.set('Message envoyé.');
  }

  protected leave(): void {
    this.chat.disconnect();
    this.joined.set(false);
    this.announcement.set('Vous avez quitté le salon.');
  }

  protected updateAuthor(value: string): void {
    this.author.set(value);
  }

  protected updateContent(value: string): void {
    this.content.set(value);
  }
}
