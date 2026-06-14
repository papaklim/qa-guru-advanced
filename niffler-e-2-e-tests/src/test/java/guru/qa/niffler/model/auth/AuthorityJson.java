package guru.qa.niffler.model.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.util.UUID;

public record AuthorityJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("userId")
    UUID userId,
    @JsonProperty("authority")
    Authority authority)
{
    public static AuthorityJson fromEntity(AuthorityEntity entity) {
        return new AuthorityJson(
            entity.getId(),
            entity.getUserId(),
            entity.getAuthority());
    }
}
