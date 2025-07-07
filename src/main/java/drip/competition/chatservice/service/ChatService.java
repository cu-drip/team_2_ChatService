package drip.competition.chatservice.service;

import drip.competition.chatservice.dto.CreateChatRequest;
import drip.competition.chatservice.model.Chat;
import drip.competition.chatservice.model.ChatUser;
import drip.competition.chatservice.model.ChatUserId;
import drip.competition.chatservice.model.Message;
import drip.competition.chatservice.repositories.ChatRepository;
import drip.competition.chatservice.repositories.ChatUserRepository;
import drip.competition.chatservice.repositories.MessageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepo;
    private final ChatUserRepository chatUserRepo;
    private final MessageRepository messageRepo;

    public List<Chat> listChats(UUID userId) {
        List<ChatUser> entries = chatUserRepo.findByUserId(userId);
        List<UUID> chatIds = entries.stream()
            .map(ChatUser::getChatId)
            .toList();
        return chatRepo.findAllById(chatIds);
    }

    public boolean isUserInChat(UUID chatId, UUID userId) {
        List<ChatUser> entries = chatUserRepo.findByUserId(userId);
        List<UUID> chatIds = entries.stream()
            .map(ChatUser::getChatId)
            .toList();
        return chatIds.contains(chatId);
    }

    public boolean isUserChatOwner(UUID chatId, UUID userId) {
        List<Chat> chats = chatRepo.getChatsByOwner(userId);
        List<UUID> chatIds = chats.stream()
            .map(Chat::getId)
            .toList();
        return chatIds.contains(chatId);
    }

    public Chat createChat(CreateChatRequest request, UUID ownerId) {
        UUID chatId = UUID.randomUUID();
        Chat chat = new Chat(chatId, request.getName(), ownerId);
        chatRepo.save(chat);

        List<ChatUser> users = request.getUsers().stream()
            .map(u -> new ChatUser(chatId, u, false))
            .collect(Collectors.toList());
        chatUserRepo.saveAll(users);

        return chat;
    }

    public List<UUID> getChatUsers(UUID chatId) {
        return chatUserRepo.findByChatId(chatId).stream()
            .map(ChatUser::getUserId)
            .collect(Collectors.toList());
    }

    public void addUsers(UUID chatId, List<UUID> users) {
        List<ChatUser> chatUsers = users.stream()
            .map(u -> new ChatUser(chatId, u, false))
            .collect(Collectors.toList());
        chatUserRepo.saveAll(chatUsers);
    }

    public void addUser(UUID chatId, UUID userId) {
        chatUserRepo.save(new ChatUser(chatId, userId, false));
    }

    public void removeUser(UUID chatId, UUID userId) {
        chatUserRepo.deleteById(new ChatUserId(chatId, userId));
    }

    public boolean isUserMuted(UUID chatId, UUID userId) {
        AtomicBoolean found = new AtomicBoolean(false);
        chatUserRepo.findById(new ChatUserId(chatId, userId)).ifPresent((user) -> {
            found.set(user.isMuted());
        });
        return found.get();
    }

    public boolean patchUserStatus(UUID chatId, UUID userId, boolean muted) {
        AtomicBoolean found = new AtomicBoolean(false);
        chatUserRepo.findById(new ChatUserId(chatId, userId)).ifPresent((user) -> {
            user.setMuted(muted);
            chatUserRepo.save(user);
            found.set(true);
        });
        return found.get();
    }

    public List<Message> getMessages(UUID chatId, Integer limit, UUID after) {
        int effectiveLimit = (limit != null && limit > 0) ? limit : 100;
        List<Message> result;

        if (after != null) {
            Optional<Message> anchor = messageRepo.findById(after);
            if (anchor.isEmpty() || !anchor.get().getChatId().equals(chatId))
                throw new EntityNotFoundException("Invalid 'after' message ID");
            Instant ts = anchor.get().getCreatedAt();
            result = messageRepo.findByChatIdAndCreatedAtAfterOrderByCreatedAtAsc(chatId, ts, PageRequest.of(0, effectiveLimit));
        } else {
            result = messageRepo.findByChatIdOrderByCreatedAtAsc(chatId, PageRequest.of(0, effectiveLimit));
        }

        return result;
    }
}

