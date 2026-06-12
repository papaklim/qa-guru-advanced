package guru.qa.niffler.api;

import guru.qa.niffler.model.auth.AuthUserJson;
import guru.qa.niffler.model.userdata.UserJson;

public interface AuthClient {
    public AuthUserJson createAuthUser(AuthUserJson json, UserJson user);
}
