package drip.competition.chatservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    private UUID id;

    private UUID author;
    private String content;

    private Instant createdAt;

    private UUID chatId;
}

