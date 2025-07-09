package drip.competition.chatservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class OutgoingMessageDto {
    private UUID id;
    private UUID author;
    private String content;
    private Instant createdAt;
}
