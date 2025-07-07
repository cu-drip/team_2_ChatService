package drip.competition.chatservice.repositories;

import drip.competition.chatservice.model.ChatUser;
import drip.competition.chatservice.model.ChatUserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatUserRepository extends JpaRepository<ChatUser, ChatUserId> {
    List<ChatUser> findByChatId(UUID chatId);
    List<ChatUser> findByUserId(UUID userId);
}
