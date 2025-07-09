package drip.competition.chatservice.component;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class JwtAuthUtil {
    public UUID getUserIdFromAuthentication(Authentication auth) {
        if (auth.getPrincipal() instanceof Jwt jwt) {
            String sub = jwt.getClaimAsString("sub");
            return UUID.fromString(sub);
        } else throw new IllegalStateException("Invalid JWT principal");
    }
}
