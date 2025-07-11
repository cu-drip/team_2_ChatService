package drip.competition.chatservice.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import drip.competition.chatservice.dto.IncomingMessageDto;
import drip.competition.chatservice.dto.OutgoingMessageDto;
import drip.competition.chatservice.service.ChatService;
import drip.competition.chatservice.service.ChatSessionRegistry;
import drip.competition.chatservice.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final MessageService messageService;
    private final ChatService chatService;
    private final ChatSessionRegistry sessionRegistry;
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        UUID chatId = (UUID) session.getAttributes().get("chatId");
        sessionRegistry.register(chatId, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        UUID chatId = (UUID) session.getAttributes().get("chatId");
        sessionRegistry.unregister(chatId, session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        UUID chatId = (UUID) session.getAttributes().get("chatId");
        UUID userId = (UUID) session.getAttributes().get("userId");

        if (!chatService.isUserInChat(chatId, userId) || chatService.isUserMuted(chatId, userId)) return;

        IncomingMessageDto incoming = objectMapper.readValue(message.getPayload(), IncomingMessageDto.class);
        OutgoingMessageDto saved = messageService.saveMessage(chatId, userId, incoming.getContent());
        sessionRegistry.broadcast(chatId, saved);
    }
}
