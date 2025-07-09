package drip.competition.chatservice.controller;

import drip.competition.chatservice.component.JwtAuthUtil;
import drip.competition.chatservice.dto.BulkUsersRequest;
import drip.competition.chatservice.dto.CreateChatRequest;
import drip.competition.chatservice.dto.PatchUserStatus;
import drip.competition.chatservice.model.Chat;
import drip.competition.chatservice.model.Message;
import drip.competition.chatservice.service.ChatService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class ChatController {
    private final ChatService chatService;
    private final JwtAuthUtil jwtUtil;

    @GetMapping
    public List<Chat> listChats(Authentication auth) {
        return chatService.listChats(currentUserId(auth));
    }

    @PostMapping
    public ResponseEntity<Chat> createChat(@RequestBody CreateChatRequest request, Authentication auth) {
        Chat chat = chatService.createChat(request, currentUserId(auth));
        return ResponseEntity.status(HttpStatus.CREATED).body(chat);
    }

    @GetMapping("/{id}/users")
    public ResponseEntity<List<UUID>> getUsers(Authentication auth, @PathVariable UUID id) {
        if (!chatService.isUserInChat(id, currentUserId(auth)))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(chatService.getChatUsers(id));
    }

    @PostMapping("/{id}/users")
    public ResponseEntity<Void> addUsers(Authentication auth, @PathVariable UUID id, @RequestBody BulkUsersRequest users) {
        if (!chatService.isUserChatOwner(id, currentUserId(auth)))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        chatService.addUsers(id, users.getUsers());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/users/{userId}")
    public ResponseEntity<Void> addUser(Authentication auth, @PathVariable UUID id, @PathVariable UUID userId) {
        if (!chatService.isUserChatOwner(id, currentUserId(auth)))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        chatService.addUser(id, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/users/{userId}")
    public ResponseEntity<Void> removeUser(Authentication auth, @PathVariable UUID id, @PathVariable UUID userId) {
        if (!chatService.isUserChatOwner(id, currentUserId(auth)))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        chatService.removeUser(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/users/{userId}")
    public ResponseEntity<Void> patchUserStatus(Authentication auth, @PathVariable UUID id, @PathVariable UUID userId, @RequestBody PatchUserStatus status) {
        if (!chatService.isUserChatOwner(id, currentUserId(auth)))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        if (chatService.patchUserStatus(id, userId, status.isMuted()))
            return ResponseEntity.noContent().build();
        else return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<List<Message>> getMessages(Authentication auth, @PathVariable UUID id,
                                     @RequestParam(required = false) Integer limit,
                                     @RequestParam(required = false) UUID after) {
        if (!chatService.isUserInChat(id, currentUserId(auth)))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(chatService.getMessages(id, limit, after));
    }

    private UUID currentUserId(Authentication auth) {
        return jwtUtil.getUserIdFromAuthentication(auth);
    }
}

