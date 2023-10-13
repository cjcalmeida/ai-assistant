import { MessageInput } from '@hilla/react-components/MessageInput.js';
import { MessageList, MessageListItem } from '@hilla/react-components/MessageList.js';
import router from 'Frontend/routes.js';
import { useState } from 'react';
import { RouterProvider } from 'react-router-dom';
import { ChatService } from './generated/endpoints';

export default function App() {
  return <RouterProvider router={router} />;
}
