package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.spend.CurrencyValues;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDaoJdbc implements SpendDao {

    private final Connection connection;

    public SpendDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public SpendEntity create(SpendEntity spend) {
        try (PreparedStatement ps = connection.prepareStatement(
            "INSERT INTO spend (username, spend_date, currency, amount, description, category_id)"
                + "VALUES (?, ?, ?, ?, ?, ?)",
            Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, spend.getUsername());
            ps.setDate(2, spend.getSpendDate());
            ps.setString(3, spend.getCurrency().name());
            ps.setDouble(4, spend.getAmount());
            ps.setString(5, spend.getDescription());
            ps.setObject(6, spend.getCategory().getId());

            ps.executeUpdate();

            final UUID generatedKey;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else throw new SQLException("Can't find id in ResultSet");
            }
            spend.setId(generatedKey);
            return spend;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<SpendEntity> findSpendByIdAndUserName(UUID id, String username) {
        try (PreparedStatement ps = connection.prepareStatement(
            "SELECT * FROM spend WHERE id = ? AND username = ?")) {
            ps.setObject(1, id);
            ps.setObject(2, username);

            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    SpendEntity se = new SpendEntity();
                    se.setId(rs.getObject("id", UUID.class));
                    se.setUsername(rs.getString("username"));
                    se.setSpendDate(rs.getDate("spend_date"));
                    se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    se.setAmount(rs.getDouble("amount"));
                    se.setDescription(rs.getString("description"));
                    se.setCategory(
                        new CategoryDaoJdbc(connection).findCategoryById(rs.getObject("category_id", UUID.class)).get());
                    return Optional.of(se);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SpendEntity> findAllByUsername(String username) {
        String query = """
                SELECT s.id AS s_id, s.username AS s_username, s.spend_date, s.currency, s.amount, s.description,
                       c.id AS c_id, c.name AS c_name, c.username AS c_username, c.archived AS c_archived
                FROM spend s
                JOIN category c ON s.category_id = c.id
                WHERE s.username = ?
            """;
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                List<SpendEntity> spends = new ArrayList<>();
                while (rs.next()) {
                    CategoryEntity ce = new CategoryEntity();
                    ce.setId(rs.getObject("c_id", UUID.class));
                    ce.setUsername(rs.getString("c_username"));
                    ce.setName(rs.getString("c_name"));
                    ce.setArchived(rs.getBoolean("c_archived"));

                    SpendEntity se = new SpendEntity();
                    se.setId(rs.getObject("s_id", UUID.class));
                    se.setUsername(rs.getString("s_username"));
                    se.setSpendDate(rs.getDate("spend_date"));
                    se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    se.setAmount(rs.getDouble("amount"));
                    se.setDescription(rs.getString("description"));
                    se.setCategory(ce);

                    spends.add(se);
                }
                return spends;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteSpend(List<UUID> spends, String username) {
        try (PreparedStatement ps = connection.prepareStatement(
            "DELETE FROM spend WHERE id = ? AND username = ?")) {
            for (UUID spend : spends) {
                ps.setObject(1, spend);
                ps.setObject(2, username);
                ps.executeUpdate();
            }

        } catch (
            SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
