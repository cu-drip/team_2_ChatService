package drip.competition.chatservice.repositories;

import drip.competition.chatservice.model.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByChatIdOrderByCreatedAtAsc(UUID chatId, Pageable pageable);
    List<Message> findByChatIdAndCreatedAtAfterOrderByCreatedAtAsc(UUID chatId, Instant createdAtAfter, Pageable pageable);
}
