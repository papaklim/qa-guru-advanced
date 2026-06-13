package guru.qa.niffler.api;

import guru.qa.niffler.model.auth.AuthUserJson;
import guru.qa.niffler.model.userdata.UserJson;

public interface UsersClient {
    public UserJson createUser(AuthUserJson authUser, UserJson user);

    public UserJson createUserSpringJdbc(AuthUserJson authUser, UserJson user);
}
