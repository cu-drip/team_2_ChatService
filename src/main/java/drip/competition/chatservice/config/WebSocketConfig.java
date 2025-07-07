package drip.competition.chatservice.config;

import drip.competition.chatservice.component.JwtHandshakeInterceptor;
import drip.competition.chatservice.handler.ChatWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final ChatWebSocketHandler handler;
    private final JwtHandshakeInterceptor handshakeInterceptor;

    public WebSocketConfig(ChatWebSocketHandler handler, JwtHandshakeInterceptor handshakeInterceptor) {
        this.handler = handler;
        this.handshakeInterceptor = handshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/chats/{id}")
            .setAllowedOrigins("*")
            .addInterceptors(handshakeInterceptor);
    }
}

