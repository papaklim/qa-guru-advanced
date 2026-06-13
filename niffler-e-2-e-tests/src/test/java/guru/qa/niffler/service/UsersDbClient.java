package guru.qa.niffler.service;

import guru.qa.niffler.api.UsersClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.impl.AuthUserDAOJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDAOSpringJdbc;
import guru.qa.niffler.data.dao.impl.UdUserDAOJdbc;
import guru.qa.niffler.data.dao.impl.UdUserDAOSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.auth.AuthUserJson;
import guru.qa.niffler.model.userdata.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.transaction;
import static guru.qa.niffler.data.Databases.xaTransaction;

public class UsersDbClient implements UsersClient {
    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Override
    public UserJson createUser(AuthUserJson authUser, UserJson user) {
        return xaTransaction(
            // Создание пользователя в auth БД
            new Databases.XaFunction<>(connection -> {
                AuthUserEntity authUserEntity = AuthUserEntity.fromJson(authUser);
                if (authUserEntity.getPassword() != null) {
                    authUserEntity.setPassword(pe.encode(authUserEntity.getPassword()));
                }
                new AuthUserDAOJdbc(connection).create(authUserEntity);
                return null;
            }, CFG.authJdbcUrl()),
            // Создание пользователя в userdata БД
            new Databases.XaFunction<>(connection -> {
                UserEntity created = new UdUserDAOJdbc(connection).create(UserEntity.fromJson(user));
                return UserJson.fromEntity(created, null);
            }, CFG.userDataJdbcUrl()
            )
        );
    }

    @Override
    public UserJson createUserSpringJdbc(AuthUserJson authUser, UserJson user) {
        // Создание пользователя в auth БД
        AuthUserEntity authUserEntity = AuthUserEntity.fromJson(authUser);
        if (authUserEntity.getPassword() != null) {
            authUserEntity.setPassword(pe.encode(authUserEntity.getPassword()));
        }
        new AuthUserDAOSpringJdbc(Databases.dataSource(CFG.authJdbcUrl())).create(authUserEntity);

        // Создание пользователя в userdata БД
        UserEntity created = new UdUserDAOSpringJdbc(Databases.dataSource(CFG.userDataJdbcUrl())).create(UserEntity.fromJson(user));
        return UserJson.fromEntity(created, null);
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

    public Optional<AuthUserJson> findAuthUserByUsernameSpringJdbc(String username) {
        return new AuthUserDAOSpringJdbc(Databases.dataSource(CFG.authJdbcUrl())).
            findByUsername(username).map(AuthUserJson::fromEntity);
    }

    public void deleteAuthUser(AuthUserJson authUser) {
        transaction(connection -> {
            new AuthUserDAOJdbc(connection).delete(AuthUserEntity.fromJson(authUser));
        }, CFG.authJdbcUrl());
    }

    public Optional<UserJson> findUserdataUserById(UUID id) {
        return transaction(connection -> {
            return new UdUserDAOJdbc(connection).findById(id).map(entity -> UserJson.fromEntity(entity, null));
        }, CFG.userDataJdbcUrl());
    }

    public Optional<UserJson> findUdUserByUsername(String username) {
        return transaction(connection -> {
            return new UdUserDAOJdbc(connection).findByUsername(username).map(entity -> UserJson.fromEntity(entity, null));
        }, CFG.userDataJdbcUrl());
    }

    public Optional<UserJson> findUdUserByUsernameSpringJdbc(String username) {
        return new UdUserDAOSpringJdbc(Databases.dataSource(CFG.userDataJdbcUrl()))
            .findByUsername(username).map(entity -> UserJson.fromEntity(entity, null));
    }

    public void deleteUserdataUser(UserJson user) {
        transaction(connection -> {
            new UdUserDAOJdbc(connection).delete(UserEntity.fromJson(user));
        }, CFG.userDataJdbcUrl());
    }
}
