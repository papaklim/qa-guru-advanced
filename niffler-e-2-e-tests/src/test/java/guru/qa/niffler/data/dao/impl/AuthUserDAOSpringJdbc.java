package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthUserDAO;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class AuthUserDAOSpringJdbc implements AuthUserDAO {
    private final DataSource dataSource;

    public AuthUserDAOSpringJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public AuthUserEntity create(AuthUserEntity authUser) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                        "VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

                ps.setString(1, authUser.getUsername());
                ps.setString(2, authUser.getPassword());
                ps.setBoolean(3, authUser.getEnabled());
                ps.setBoolean(4, authUser.getAccountNonExpired());
                ps.setBoolean(5, authUser.getAccountNonLocked());
                ps.setBoolean(6, authUser.getCredentialsNonExpired());
                return ps;
            },
            keyHolder);
        final UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
        authUser.setId(generatedKey);
        if (authUser.getAuthorities() != null && !authUser.getAuthorities().isEmpty()) {
            for (AuthorityEntity authority : authUser.getAuthorities()) {
                KeyHolder authKeyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(con -> {
                    PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO \"authority\" (user_id, authority) VALUES (?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setObject(1, generatedKey);
                    ps.setString(2, authority.getAuthority().name());
                    return ps;
                }, authKeyHolder);

                final UUID authorityId = (UUID) authKeyHolder.getKeys().get("id");
                authority.setId(authorityId);
                authority.setUser(generatedKey);
            }
        }
        return authUser;
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return Optional.ofNullable(
            jdbcTemplate.queryForObject("SELECT * FROM \"user\" WHERE id = ?",
                AuthUserEntityRowMapper.INSTANCE,
                id
            )
        );
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return Optional.ofNullable(
            jdbcTemplate.queryForObject("SELECT * FROM \"user\" WHERE username = ?",
                AuthUserEntityRowMapper.INSTANCE,
                username
            )
        );
    }

    @Override
    public void delete(AuthUserEntity user) {

    }
}


