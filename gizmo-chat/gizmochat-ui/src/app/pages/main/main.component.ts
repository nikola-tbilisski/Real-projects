import { AfterViewChecked, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ChatListComponent } from '../../components/chat-list/chat-list.component';
import { ChatResponse } from '../../services/models/chat-response';
import { ChatService } from '../../services/services/chat.service';
import { CommonModule } from '@angular/common';
import { KeycloakService } from '../../utils/keycloak/keycloak.service';
import { MessageService } from '../../services/services/message.service';
import { MessageResponse } from '../../services/models/message-response';
import { PickerComponent } from '@ctrl/ngx-emoji-mart';
import { FormsModule } from '@angular/forms';
import { EmojiData } from '@ctrl/ngx-emoji-mart/ngx-emoji';
import { MessageRequest } from '../../services/models/message-request';
import * as Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import { Notification } from './notification';

@Component({
  selector: 'app-main',
  standalone: true,
  imports: [
    ChatListComponent,
    CommonModule,
    PickerComponent,
    FormsModule
  ],
  templateUrl: './main.component.html',
  styleUrl: './main.component.scss'
})
export class MainComponent implements OnInit, OnDestroy, AfterViewChecked {
  chats: Array<ChatResponse> = [];
  selectedChat: ChatResponse = {};
  private readonly chatService: ChatService;
  private readonly keycloakService: KeycloakService;
  private readonly messageService: MessageService;
  chatMessages: MessageResponse[] = [];
  showEmojis: any = false;
  messageContent: any = '';
  socketClient: any = null;
  @ViewChild('scrollableDiv') scrollableDiv!: ElementRef<HTMLDivElement>;
  private notificationSubscription: any;

  constructor(chatService: ChatService, keycloakService: KeycloakService, messageService: MessageService) {
    this.chatService = chatService;
    this.keycloakService = keycloakService;
    this.messageService = messageService;
  }

  ngAfterViewChecked(): void {
    this.scrollBottom();
  }

  ngOnDestroy(): void {
    if (this.socketClient !== null) {
      this.socketClient.disconnect();
      this.notificationSubscription.unsubscribe();
      this.socketClient = null;
    }
  }

  ngOnInit(): void {
    this.initWebSocket();
    this.getAllChats();
  }

  private getAllChats(): void {
    this.chatService.getChatsByReceiver()
      .subscribe({
        next: result => {
          this.chats = result;
        }
      });
  }

  logout() {
    this.keycloakService.logout();
  }

  userProfile() {
    this.keycloakService.accountManagement();
  }

  chatSelected(chatResponse: ChatResponse) {
    this.selectedChat = chatResponse;
    this.getAllChatMessages(chatResponse.id as string);
    this.setMessagesToSeen();
    this.selectedChat.unreadCount = 0;
  }

  private getAllChatMessages(chatId: string) {
    console.log('🔄 Reloading messages for chat:', chatId);

    this.messageService.getMessages({
      'chat-id': chatId
    }).subscribe({
      next: (messages) => {
        console.log('📦 Received messages from backend:', messages.length, 'messages');

        // Debug: Check each message for media
        messages.forEach((msg, index) => {
          if (msg.type !== 'TEXT') {
            console.log(`📸 Message ${index}:`, {
              type: msg.type,
              content: msg.content,
              hasMedia: !!msg.media,
              mediaLength: msg.media?.length,
              mediaPreview: msg.media?.[0]?.substring(0, 50)
            });
          }
        });

        this.chatMessages = messages;
        console.log('✅ Chat messages updated, total:', this.chatMessages.length);
      },
      error: (err) => {
        console.error('❌ Error loading messages:', err);
      }
    })
  }

  private setMessagesToSeen() {
    this.messageService.setMessageToSeen({
      'chat-id': this.selectedChat.id as string
    }).subscribe({
      next: () => {
      }
    });
  }

  isSelfMessage(message: MessageResponse) {
    return message.senderId === this.keycloakService.userId;
  }

  uploadMedia(target: EventTarget | null) {
    const file = this.extractFileFromTarget(target);
    if (file !== null) {
      // Determine file type and MIME type dynamically
      const messageType = this.getMessageTypeFromFile(file);
      const mimeType = this.getMimeType(file);

      const reader = new FileReader();
      reader.onload = () => {
        if (reader.result) {
          // Extract base64 content (remove "data:mime/type;base64," prefix)
          const mediaLines: string = reader.result.toString().split(',')[1];

          this.messageService.uploadMedia({
            'chat-id': this.selectedChat.id as string,
            body: {
              file: file
            }
          }).subscribe({
            next: () => {
              // Create message with dynamic type and descriptive label
              const message: MessageResponse = {
                senderId: this.getSenderId(),
                receiverId: this.getReceiverId(),
                content: this.getDisplayLabel(messageType), // e.g., "Photo", "Video", "Audio", "Document"
                type: messageType, // Dynamically set: 'IMAGE' | 'VIDEO' | 'AUDIO' | 'DOCUMENT'
                state: 'SENT',
                media: [mediaLines],
                createdDate: new Date().toString()
              };
              this.chatMessages.push(message);
            }
          });
        }
      }
      reader.readAsDataURL(file);
    }
  }

  onSelectEmojis(emojiSelected: any) {
    const emoji: EmojiData = emojiSelected.emoji;
    this.messageContent += emoji.native;
  }

  keyDown(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      this.sendMessage();
    }
  }

  onClick() {
    this.setMessagesToSeen();
  }

  sendMessage() {
    if (this.messageContent) {
      const messageRequest: MessageRequest = {
        chatId: this.selectedChat.id,
        senderId: this.getSenderId(),
        receiverId: this.getReceiverId(),
        content: this.messageContent,
        type: 'TEXT'
      };
      this.messageService.saveMessage({
        body: messageRequest
      }).subscribe({
        next: () => {
          const message: MessageResponse = {
            senderId: this.getSenderId(),
            receiverId: this.getReceiverId(),
            content: this.messageContent,
            type: 'TEXT',
            state: 'SENT',
            createdDate: new Date().toString()
          };
          this.selectedChat.lastMessage = this.messageContent;
          this.chatMessages.push(message);
          this.messageContent = '';
          this.showEmojis = false;
        }
      });
    }
  }

  private getSenderId() {
    if (this.selectedChat.senderId === this.keycloakService.userId) {
      return this.selectedChat.senderId;
    }
    return this.selectedChat.receiverId as string;
  }

  private getReceiverId() {
    if (this.selectedChat.senderId === this.keycloakService.userId) {
      return this.selectedChat.receiverId as string;
    }
    return this.selectedChat.senderId as string;
  }

  private initWebSocket() {
    if (this.keycloakService.keycloak.tokenParsed?.sub) {
      let ws = new SockJS('http://localhost:8080/ws');
      this.socketClient = Stomp.over(ws);
      const subUrl = `/user/${this.keycloakService.keycloak.tokenParsed?.sub}/chat`;
      this.socketClient.connect({ 'Authorization': 'Bearer ' + this.keycloakService.keycloak.token },
        () => {
          this.notificationSubscription = this.socketClient.subscribe(subUrl,
            (message: any) => {
              const notification: Notification = JSON.parse(message.body);

              this.handleNotification(notification);
            },
            () => console.error('Error while connecting websocket'));
        }
      );
    }
  }

  private handleNotification(notification: Notification) {
    if (!notification) return;
    if (this.selectedChat && this.selectedChat.id === notification.chatId) {
      switch (notification.type) {
        case 'MESSAGE':
        case 'IMAGE':
        case 'VIDEO':
        case 'AUDIO':
        case 'DOCUMENT': {
          // DEBUG: Log notification data to verify what backend is sending
          console.log('📨 Received media notification:', {
            type: notification.type,
            messageType: notification.messageType,
            content: notification.content,
            hasMedia: !!notification.media,
            mediaLength: notification.media?.length,
            mediaPreview: notification.media?.[0]?.substring(0, 50) // First 50 chars
          });

          // SOLUTION: If media is not provided in notification, reload all messages
          // This is a simple fix - backend doesn't send base64 data via WebSocket
          if (!notification.media || notification.media.length === 0) {
            console.log('⚠️ Media data not in notification, reloading messages...');

            // Reload all messages to get the one with media
            this.getAllChatMessages(this.selectedChat.id as string);

            // Update last message preview
            this.selectedChat.lastMessage = notification.content || this.getDisplayLabel(notification.messageType || 'DOCUMENT');

            break; // Exit early, messages will reload
          }

          // If media IS included (unlikely), create message normally
          const message: MessageResponse = {
            senderId: notification.senderId,
            receiverId: notification.receiverId,
            content: notification.content,
            type: notification.messageType,
            media: notification.media,
            createdDate: new Date().toString()
          };

          // DEBUG: Verify message object before adding to chat
          console.log('💬 Creating message object:', {
            type: message.type,
            hasMedia: !!message.media,
            mediaLength: message.media?.length
          });

          // Update last message preview in chat list
          if (notification.type === 'MESSAGE') {
            this.selectedChat.lastMessage = notification.content;
          } else {
            // For all media types, show descriptive label
            this.selectedChat.lastMessage = notification.content || this.getDisplayLabel(notification.messageType || 'DOCUMENT');
          }

          this.chatMessages.push(message);
          break;
        }

        case 'SEEN':
          this.chatMessages.forEach(m => m.state = 'SEEN');
          break;
      }
    } else {
      const destChat = this.chats.find(c => c.id === notification.chatId);
      if (destChat && notification.type !== 'SEEN') {
        // Update last message for unselected chats
        if (notification.type === 'MESSAGE') {
          destChat.lastMessage = notification.content;
        } else {
          destChat.lastMessage = notification.content || this.getDisplayLabel(notification.messageType || 'DOCUMENT');
        }
        destChat.lastMessageTime = new Date().toString();
        destChat.unreadCount! += 1;
      } else if (notification.type === 'MESSAGE') {
        const newChat: ChatResponse = {
          id: notification.chatId,
          senderId: notification.senderId,
          receiverId: notification.receiverId,
          lastMessage: notification.content,
          name: notification.chatName,
          unreadCount: 1,
          lastMessageTime: new Date().toString()
        };
        this.chats.unshift(newChat);
      }
    }
  }

  private extractFileFromTarget(target: EventTarget | null): File | null {
    const htmlInputTarget = target as HTMLInputElement;
    if (target === null || htmlInputTarget.files === null) {
      return null;
    }
    return htmlInputTarget.files[0];
  }

  /**
   * Extract file extension from filename
   * Example: "photo.jpg" -> "jpg"
   */
  private getFileExtension(filename: string): string {
    return filename.split('.').pop()?.toLowerCase() || '';
  }

  /**
   * Determine message type based on file extension
   * Maps file extensions to appropriate message types (IMAGE, VIDEO, AUDIO, DOCUMENT)
   */
  private getMessageTypeFromFile(file: File): 'IMAGE' | 'VIDEO' | 'AUDIO' | 'DOCUMENT' {
    const extension = this.getFileExtension(file.name);

    const imageExtensions = ['jpg', 'jpeg', 'png', 'svg'];
    const videoExtensions = ['mp4', 'mov'];
    const audioExtensions = ['mp3'];
    const documentExtensions = ['pdf'];

    if (imageExtensions.includes(extension)) return 'IMAGE';
    if (videoExtensions.includes(extension)) return 'VIDEO';
    if (audioExtensions.includes(extension)) return 'AUDIO';
    if (documentExtensions.includes(extension)) return 'DOCUMENT';

    return 'DOCUMENT'; // default fallback
  }

  /**
   * Get the correct MIME type for a file based on its extension
   * This is crucial for proper rendering in data URLs
   */
  private getMimeType(file: File): string {
    const extension = this.getFileExtension(file.name);

    const mimeTypeMap: { [key: string]: string } = {
      'jpg': 'image/jpeg',
      'jpeg': 'image/jpeg',
      'png': 'image/png',
      'svg': 'image/svg+xml',
      'mp4': 'video/mp4',
      'mov': 'video/quicktime',
      'mp3': 'audio/mpeg',
      'pdf': 'application/pdf'
    };

    return mimeTypeMap[extension] || 'application/octet-stream';
  }

  /**
   * Get user-friendly display label for message type
   * Used in chat list preview instead of generic "Attachment"
   */
  private getDisplayLabel(messageType: string): string {
    const labels: { [key: string]: string } = {
      'IMAGE': 'Photo',
      'VIDEO': 'Video',
      'AUDIO': 'Audio',
      'DOCUMENT': 'Document'
    };
    return labels[messageType] || 'Attachment';
  }

  /**
   * Generate proper data URL with correct MIME type for displaying media
   * This method is called from the template to render different file types
   */
  getMediaDataUrl(message: MessageResponse): string {
    if (!message.media || message.media.length === 0) {
      return '';
    }

    let mimeType: string;
    switch (message.type) {
      case 'IMAGE':
        mimeType = 'image/jpeg'; // default for images, works for most
        break;
      case 'VIDEO':
        mimeType = 'video/mp4';
        break;
      case 'AUDIO':
        mimeType = 'audio/mpeg';
        break;
      case 'DOCUMENT':
        mimeType = 'application/pdf';
        break;
      default:
        mimeType = 'application/octet-stream';
    }

    return `data:${mimeType};base64,${message.media}`;
  }

  private scrollBottom() {
    if (this.scrollableDiv) {
      const div = this.scrollableDiv.nativeElement;
      div.scrollTop = div.scrollHeight;
    }
  }
}
