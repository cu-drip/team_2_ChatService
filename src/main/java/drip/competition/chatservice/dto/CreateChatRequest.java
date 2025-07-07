package drip.competition.chatservice.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateChatRequest {
    private String name;
    private List<UUID> users;
}
