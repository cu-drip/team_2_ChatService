package drip.competition.chatservice.repositories;

import drip.competition.chatservice.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {
    List<Chat> getChatsByOwner(UUID owner);
}
