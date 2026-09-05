package gympie.springboot.a_simple_login.dto;

import java.time.Instant;

public record HerbNoteResponse(Long id, Long herbId, String herbName, String title, String body,
                               String ownerUsername, Instant createdAt) {
}
