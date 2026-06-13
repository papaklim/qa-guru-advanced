package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthUserEntityRowMapper implements RowMapper<AuthUserEntity> {

    public static final AuthUserEntityRowMapper INSTANCE = new AuthUserEntityRowMapper();

    private AuthUserEntityRowMapper() {
    }

    @Override
    public AuthUserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        AuthUserEntity userAuth = new AuthUserEntity();
        userAuth.setId(rs.getObject("id", UUID.class));
        userAuth.setUsername(rs.getString("username"));
        userAuth.setPassword(rs.getString("password"));
        userAuth.setEnabled(rs.getBoolean("enabled"));
        userAuth.setAccountNonExpired(rs.getBoolean("account_non_expired"));
        userAuth.setAccountNonLocked(rs.getBoolean("account_non_locked"));
        userAuth.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
        return userAuth;
    }
}
