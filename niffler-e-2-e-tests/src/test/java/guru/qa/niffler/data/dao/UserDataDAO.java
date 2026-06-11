package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.spend.user.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserDataDAO {
    UserEntity createUser(UserEntity user);

    Optional<UserEntity> findById(UUID id);

    Optional<UserEntity> findByUsername(String username);

    void delete(UserEntity user);
}
