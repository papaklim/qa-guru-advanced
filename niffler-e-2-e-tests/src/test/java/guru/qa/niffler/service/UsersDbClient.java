package guru.qa.niffler.service;

import guru.qa.niffler.api.UsersClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.UdUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UdUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.auth.AuthUserJson;
import guru.qa.niffler.model.auth.Authority;
import guru.qa.niffler.model.userdata.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
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
                new AuthUserDaoJdbc(connection).create(authUserEntity);
                return null;
            }, CFG.authJdbcUrl()),
            // Создание пользователя в userdata БД
            new Databases.XaFunction<>(connection -> {
                UserEntity created = new UdUserDaoJdbc(connection).create(UserEntity.fromJson(user));
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
        new AuthUserDaoSpringJdbc(Databases.dataSource(CFG.authJdbcUrl())).create(authUserEntity);

        AuthorityEntity[] authorities = Arrays.stream(Authority.values()).map(
            e -> {
                AuthorityEntity ae = new AuthorityEntity();
                ae.setUserId(authUserEntity.getId());
                ae.setAuthority(e);
                return ae;
            }
        ).toArray(AuthorityEntity[]::new);

        new AuthAuthorityDaoJdbc(Databases.dataSource(CFG.authJdbcUrl())).create(authorities);

        // Создание пользователя в userdata БД
        UserEntity created = new UdUserDaoSpringJdbc(Databases.dataSource(CFG.userDataJdbcUrl()))
            .create(UserEntity.fromJson(user));
        return UserJson.fromEntity(created, null);
    }

    public Optional<AuthUserJson> findAuthUserById(UUID id) {
        return transaction(connection -> {
            return new AuthUserDaoJdbc(connection).findById(id).map(AuthUserJson::fromEntity);
        }, CFG.authJdbcUrl());
    }

    public Optional<AuthUserJson> findAuthUserByUsername(String username) {
        return transaction(connection -> {
            return new AuthUserDaoJdbc(connection).findByUsername(username).map(AuthUserJson::fromEntity);
        }, CFG.authJdbcUrl());
    }

    public Optional<AuthUserJson> findAuthUserByUsernameSpringJdbc(String username) {
        return new AuthUserDaoSpringJdbc(Databases.dataSource(CFG.authJdbcUrl())).
            findByUsername(username).map(AuthUserJson::fromEntity);
    }

    public void deleteAuthUser(AuthUserJson authUser) {
        transaction(connection -> {
            new AuthUserDaoJdbc(connection).delete(AuthUserEntity.fromJson(authUser));
        }, CFG.authJdbcUrl());
    }

    public Optional<UserJson> findUserdataUserById(UUID id) {
        return transaction(connection -> {
            return new UdUserDaoJdbc(connection).findById(id).map(entity -> UserJson.fromEntity(entity, null));
        }, CFG.userDataJdbcUrl());
    }

    public Optional<UserJson> findUdUserByUsername(String username) {
        return transaction(connection -> {
            return new UdUserDaoJdbc(connection).findByUsername(username).map(entity -> UserJson.fromEntity(entity, null));
        }, CFG.userDataJdbcUrl());
    }

    public Optional<UserJson> findUdUserByUsernameSpringJdbc(String username) {
        return new UdUserDaoSpringJdbc(Databases.dataSource(CFG.userDataJdbcUrl()))
            .findByUsername(username).map(entity -> UserJson.fromEntity(entity, null));
    }

    public void deleteUserdataUser(UserJson user) {
        transaction(connection -> {
            new UdUserDaoJdbc(connection).delete(UserEntity.fromJson(user));
        }, CFG.userDataJdbcUrl());
    }
}
