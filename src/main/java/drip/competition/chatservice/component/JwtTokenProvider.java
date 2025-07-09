package drip.competition.chatservice.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class JwtTokenProvider {
    public final JwtDecoder jwtDecoder;

    public JwtTokenProvider(@Value("${jwt.secret-file}") String secretFilePath) {
        try {
            byte[] secretBytes = Files.readAllBytes(Path.of(secretFilePath));
            jwtDecoder = NimbusJwtDecoder.withSecretKey(
                new SecretKeySpec(secretBytes, "HmacSHA256")
            ).build();
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать секретный ключ для JWT", e);
        }
    }
}
