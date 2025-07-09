package drip.competition.chatservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf((conf) -> {
                try {
                    conf.disable()
                        .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/ws/**").permitAll()
                            .anyRequest().authenticated()
                        )
                        .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(NimbusJwtDecoder.withSecretKey(
                            new SecretKeySpec("123124315tfadfasfdsafdsafsdafdsafsda".getBytes(), "HmacSHA256")
                        ).build())));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        return http.build();
    }
}

