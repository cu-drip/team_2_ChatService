package drip.competition.chatservice.component;

import drip.competition.chatservice.repositories.ChatRepository;
import drip.competition.chatservice.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.UUID;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    private final JwtTokenProvider jwtTokenProvider;
    private final ChatRepository chatRepo;
    private final ChatService chatService;

    @Autowired
    public JwtHandshakeInterceptor(JwtTokenProvider jwtTokenProvider, ChatRepository chatRepo, ChatService chatService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.chatRepo = chatRepo;
        this.chatService = chatService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String token;
        try {
            String query = request.getURI().getQuery();
            int tokenIdx = query.indexOf("token=");
            token = query.substring(tokenIdx + 7);
            int endIdx = token.indexOf("&");
            token = token.substring(0, endIdx);
        } catch (Exception e) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        UUID userId;
        try {
            Jwt jwt = jwtTokenProvider.jwtDecoder.decode(token);
            attributes.put("jwt", jwt);

            userId = UUID.fromString(jwt.getClaimAsString("sub"));
            attributes.put("userId", userId);
        } catch (Exception e) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        String path = request.getURI().getPath();
        String[] segments = path.split("/");
        UUID chatId = UUID.fromString(segments[segments.length - 1]);
        attributes.put("chatId", chatId);
        if (chatRepo.getChatById(chatId) == null)
            return false;
        if (!chatService.isUserInChat(chatId, userId))
            return false;

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}
