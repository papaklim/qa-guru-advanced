package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.spend.SpendEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendDAO {
    SpendEntity create(SpendEntity spend);

    Optional<SpendEntity> findSpendByIdAndUserName(UUID id, String username);

    List<SpendEntity> findAllByUsername(String username) throws SQLException;

    void deleteSpend(List<UUID> spends, String username);
}
