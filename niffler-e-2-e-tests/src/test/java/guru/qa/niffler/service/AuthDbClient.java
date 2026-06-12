package guru.qa.niffler.service;

import guru.qa.niffler.api.AuthClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.impl.AuthUserDAOJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDAOJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.auth.AuthUserJson;
import guru.qa.niffler.model.userdata.UserJson;

import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.transaction;
import static guru.qa.niffler.data.Databases.xaTransaction;

public class AuthDbClient implements AuthClient {
    private static final Config CFG = Config.getInstance();

    @Override
    public AuthUserJson createAuthUser(AuthUserJson authUser, UserJson user) {
        return xaTransaction(
            // Создание пользователя в userdata БД
            new Databases.XaFunction<>(connection -> {
                new UserdataUserDAOJdbc(connection).createUser(UserEntity.fromJson(user));
                return null;
            }, CFG.userDataJdbcUrl()
            ),
            // Создание пользователя в auth БД
            new Databases.XaFunction<>(connection -> {
                AuthUserEntity created = new AuthUserDAOJdbc(connection).create(AuthUserEntity.fromJson(authUser));
                return AuthUserJson.fromEntity(created);
            }, CFG.authJdbcUrl()
            ));
    }

    public Optional<AuthUserJson> findAuthUserById(UUID id) {
        return transaction(connection -> {
            return new AuthUserDAOJdbc(connection).findById(id).map(AuthUserJson::fromEntity);
        }, CFG.authJdbcUrl());
    }

    public Optional<AuthUserJson> findAuthUserByUsername(String username) {
        return transaction(connection -> {
            return new AuthUserDAOJdbc(connection).findByUsername(username).map(AuthUserJson::fromEntity);
        }, CFG.authJdbcUrl());
    }

    public void deleteAuthUser(AuthUserJson authUser) {
        transaction(connection -> {
            new AuthUserDAOJdbc(connection).delete(AuthUserEntity.fromJson(authUser));
        }, CFG.authJdbcUrl());
    }

    public Optional<UserJson> findUserdataUserById(UUID id) {
        return transaction(connection -> {
            return new UserdataUserDAOJdbc(connection).findById(id).map(entity -> UserJson.fromEntity(entity, null));
        }, CFG.userDataJdbcUrl());
    }

    public Optional<UserJson> findUserdataUserByUsername(String username) {
        return transaction(connection -> {
            return new UserdataUserDAOJdbc(connection).findByUsername(username).map(entity -> UserJson.fromEntity(entity, null));
        }, CFG.userDataJdbcUrl());
    }

    public void deleteUserdataUser(UserJson user) {
        transaction(connection -> {
            new UserdataUserDAOJdbc(connection).delete(UserEntity.fromJson(user));
        }, CFG.userDataJdbcUrl());
    }
}
