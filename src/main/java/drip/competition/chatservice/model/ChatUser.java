package drip.competition.chatservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ChatUserId.class)
public class ChatUser {
    @Id
    private UUID chatId;
    @Id
    private UUID userId;
    private boolean muted;
}

