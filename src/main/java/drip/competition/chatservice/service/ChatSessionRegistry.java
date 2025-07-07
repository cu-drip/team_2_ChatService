package drip.competition.chatservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import drip.competition.chatservice.dto.OutgoingMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ChatSessionRegistry {
    private final Map<UUID, Set<WebSocketSession>> chatSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public void register(UUID chatId, WebSocketSession session) {
        chatSessions.computeIfAbsent(chatId, k -> Collections.synchronizedSet(new HashSet<>())).add(session);
    }

    public void unregister(UUID chatId, WebSocketSession session) {
        Set<WebSocketSession> sessions = chatSessions.get(chatId);
        if (sessions != null) {
            sessions.remove(session);
        }
    }

    public void broadcast(UUID chatId, OutgoingMessageDto message) {
        Set<WebSocketSession> sessions = chatSessions.get(chatId);
        if (sessions == null) return;

        try {
            String json = objectMapper.writeValueAsString(message);
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(json));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
