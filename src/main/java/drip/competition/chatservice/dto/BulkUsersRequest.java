package drip.competition.chatservice.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class BulkUsersRequest {
    private List<UUID> users;
}
