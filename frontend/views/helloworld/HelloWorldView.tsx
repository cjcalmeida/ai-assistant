import { Button } from '@hilla/react-components/Button.js';
import { MessageInput } from '@hilla/react-components/MessageInput.js';
import { MessageList, MessageListItem } from '@hilla/react-components/MessageList.js';
import { ChatService } from 'Frontend/generated/endpoints.js';
import { useState } from 'react';

export default function HelloWorldView() {
  const [messages, setMessages] = useState<MessageListItem[]>([]);

  function addMessage(message: MessageListItem) {
    setMessages((messages) => [...messages, message]);
  }

  function appendToLastMessage(chunk: string) {
    setMessages((messages) => {
      const lastMessage = messages[messages.length - 1];
      lastMessage.text += chunk;
      return [...messages.slice(0, -1), lastMessage];
    });
  }

  async function sendMessage(message: string) {
    addMessage({
      text: message,
      userName: 'You',
    });

    let first = true;

    ChatService.chat(message).onNext((chunk) => {
      if (first && chunk) {
        addMessage({
          text: chunk,
          userName: 'Assistant',
        });
        first = false;
      } else {
        appendToLastMessage(chunk);
      }
    });
  }

  return (
    <div className="p-m flex flex-col h-full box-border">
      <MessageList className="flex-grow" items={messages} />
      <MessageInput onSubmit={(e) => sendMessage(e.detail.value)} />
    </div>
  );
}
