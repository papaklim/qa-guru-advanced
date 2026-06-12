package guru.qa.niffler.data.entity.auth;

import guru.qa.niffler.model.auth.Authority;
import guru.qa.niffler.model.auth.AuthorityJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class AuthorityEntity implements Serializable {
    private UUID id;
    private UUID user;
    private Authority authority;

    public static AuthorityEntity fromJson(AuthorityJson json) {
        AuthorityEntity ae = new AuthorityEntity();
        ae.setId(json.id());
        ae.setUser(json.userId());
        ae.setAuthority(json.authority());
        return ae;
    }
}
