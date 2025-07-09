package drip.competition.chatservice.service;

import drip.competition.chatservice.dto.OutgoingMessageDto;
import drip.competition.chatservice.model.Message;
import drip.competition.chatservice.repositories.ChatRepository;
import drip.competition.chatservice.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    public OutgoingMessageDto saveMessage(UUID chatId, UUID authorId, String content) {
        Message message = new Message(
            UUID.randomUUID(),
            authorId,
            content,
            Instant.now(),
            chatId
        );
        message = messageRepository.save(message);

        return OutgoingMessageDto.builder()
            .id(message.getId())
            .author(message.getAuthor())
            .content(message.getContent())
            .createdAt(message.getCreatedAt())
            .build();
    }
}
