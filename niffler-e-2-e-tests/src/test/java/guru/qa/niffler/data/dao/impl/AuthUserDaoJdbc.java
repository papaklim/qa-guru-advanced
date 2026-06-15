package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthUserDaoJdbc implements AuthUserDao {
    private static final Config CFG = Config.getInstance();

    @Override
    public AuthUserEntity create(AuthUserEntity authUser) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
            "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                "VALUES (?, ?, ?, ?, ?, ?)",
            Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, authUser.getUsername());
            ps.setString(2, authUser.getPassword());
            ps.setBoolean(3, authUser.getEnabled());
            ps.setBoolean(4, authUser.getAccountNonExpired());
            ps.setBoolean(5, authUser.getAccountNonLocked());
            ps.setBoolean(6, authUser.getCredentialsNonExpired());

            ps.executeUpdate();

            final UUID generatedKey;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else throw new SQLException("Can't find id in ResultSet");
            }
            authUser.setId(generatedKey);

            return authUser;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
            "SELECT * FROM \"user\" WHERE id = ?")) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    AuthUserEntity u = new AuthUserEntity();
                    u.setId(rs.getObject("id", UUID.class));
                    u.setUsername(rs.getString("username"));
                    u.setPassword(rs.getString("password"));
                    u.setEnabled(rs.getBoolean("enabled"));
                    u.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    u.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    u.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));

                    u.setAuthorities(this.findAuthoritiesByUserId(u.getId()));

                    return Optional.of(u);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        try (PreparedStatement ps =  holder(CFG.authJdbcUrl()).connection().prepareStatement(
            "SELECT * FROM \"user\" WHERE username = ?")) {
            ps.setString(1, username);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    AuthUserEntity u = new AuthUserEntity();
                    u.setId(rs.getObject("id", UUID.class));
                    u.setUsername(rs.getString("username"));
                    u.setPassword(rs.getString("password"));
                    u.setEnabled(rs.getBoolean("enabled"));
                    u.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    u.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    u.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));

                    u.setAuthorities(this.findAuthoritiesByUserId(u.getId()));

                    return Optional.of(u);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(AuthUserEntity user) {
        try {
            try (PreparedStatement ps =  holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "DELETE FROM \"authority\" WHERE user_id = ?")) {
                ps.setObject(1, user.getId());
                ps.executeUpdate();
            }
            try (PreparedStatement ps =  holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "DELETE FROM \"user\" WHERE id = ?")) {
                ps.setObject(1, user.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<AuthorityEntity> findAuthoritiesByUserId(UUID userId) {
        try (PreparedStatement ps =  holder(CFG.authJdbcUrl()).connection().prepareStatement(
            "SELECT * FROM \"authority\" WHERE user_id = ?")) {
            ps.setObject(1, userId);
            ps.execute();

            List<AuthorityEntity> authorities = new ArrayList<>();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setId(rs.getObject("id", UUID.class));
                    ae.setUserId(rs.getObject("user_id", UUID.class));
                    ae.setAuthority(guru.qa.niffler.model.auth.Authority.valueOf(rs.getString("authority")));
                    authorities.add(ae);
                }
            }
            return authorities;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
