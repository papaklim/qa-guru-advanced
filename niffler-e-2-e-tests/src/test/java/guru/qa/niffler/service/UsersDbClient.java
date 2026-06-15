package guru.qa.niffler.service;

import guru.qa.niffler.api.UsersClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.UdUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.auth.AuthUserJson;
import guru.qa.niffler.model.auth.Authority;
import guru.qa.niffler.model.userdata.UserJson;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.Optional;

public class UsersDbClient implements UsersClient {
    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();
    private final UdUserDao udUserDao = new UdUserDaoSpringJdbc();

    private final TransactionTemplate transactionTemplate = new TransactionTemplate(
        new JdbcTransactionManager(
            DataSources.dataSource(CFG.authJdbcUrl())
        )
    );

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
        CFG.authJdbcUrl(),
        CFG.userDataJdbcUrl()
    );

//    @Override
//    public UserJson createUser(AuthUserJson authUser, UserJson user) {
//        return xaTransaction(
//            // Создание пользователя в auth БД
//            new Databases.XaFunction<>(connection -> {
//                AuthUserEntity authUserEntity = AuthUserEntity.fromJson(authUser);
//                if (authUserEntity.getPassword() != null) {
//                    authUserEntity.setPassword(pe.encode(authUserEntity.getPassword()));
//                }
//                authUserDao.create(authUserEntity);
//                return null;
//            }, CFG.authJdbcUrl()),
//            // Создание пользователя в userdata БД
//            new Databases.XaFunction<>(connection -> {
//                UserEntity created = udUserDao.create(UserEntity.fromJson(user));
//                return UserJson.fromEntity(created, null);
//            }, CFG.userDataJdbcUrl()
//            )
//        );
//    }

    @Override
    public UserJson createUserSpringJdbc(AuthUserJson authUser, UserJson user) {
        return xaTransactionTemplate.execute(() -> {
                // Создание пользователя в auth БД
                AuthUserEntity authUserEntity = AuthUserEntity.fromJson(authUser);
                if (authUserEntity.getPassword() != null) {
                    authUserEntity.setPassword(pe.encode(authUserEntity.getPassword()));
                }
                authUserDao.create(authUserEntity);

                AuthorityEntity[] authorities = Arrays.stream(Authority.values()).map(
                    e -> {
                        AuthorityEntity ae = new AuthorityEntity();
                        ae.setUserId(authUserEntity.getId());
                        ae.setAuthority(e);
                        return ae;
                    }
                ).toArray(AuthorityEntity[]::new);

                authAuthorityDao.create(authorities);

                // Создание пользователя в userdata БД
                UserEntity created = udUserDao.create(UserEntity.fromJson(user));
                return UserJson.fromEntity(created, null);
            }
        );
    }

//    public Optional<AuthUserJson> findAuthUserById(UUID id) {
//        return transaction(connection -> {
//            return authUserDao.findById(id).map(AuthUserJson::fromEntity);
//        }, CFG.authJdbcUrl());
//    }
//
//    public Optional<AuthUserJson> findAuthUserByUsername(String username) {
//        return transaction(connection -> {
//            return authUserDao.findByUsername(username).map(AuthUserJson::fromEntity);
//        }, CFG.authJdbcUrl());
//    }
//
    public Optional<AuthUserJson> findAuthUserByUsernameSpringJdbc(String username) {
        return new AuthUserDaoSpringJdbc().
            findByUsername(username).map(AuthUserJson::fromEntity);
    }
//
//    public void deleteAuthUser(AuthUserJson authUser) {
//        transaction(connection -> {
//            new AuthUserDaoJdbc().delete(AuthUserEntity.fromJson(authUser));
//        }, CFG.authJdbcUrl());
//    }
//
//    public Optional<UserJson> findUserdataUserById(UUID id) {
//        return transaction(connection -> {
//            return new UdUserDaoJdbc().findById(id).map(entity -> UserJson.fromEntity(entity, null));
//        }, CFG.userDataJdbcUrl());
//    }
//
//    public Optional<UserJson> findUdUserByUsername(String username) {
//        return transaction(connection -> {
//            return new UdUserDaoJdbc().findByUsername(username).map(entity -> UserJson.fromEntity(entity, null));
//        }, CFG.userDataJdbcUrl());
//    }
//
    public Optional<UserJson> findUdUserByUsernameSpringJdbc(String username) {
        return new UdUserDaoSpringJdbc()
            .findByUsername(username).map(entity -> UserJson.fromEntity(entity, null));
    }
//
//    public void deleteUserdataUser(UserJson user) {
//        transaction(connection -> {
//            udUserDao.delete(UserEntity.fromJson(user));
//        }, CFG.userDataJdbcUrl());
//    }
}
