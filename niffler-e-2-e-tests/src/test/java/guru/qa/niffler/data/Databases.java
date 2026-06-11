package guru.qa.niffler.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;

public class Databases {
    private Databases() {
    }

    private static Map<String, DataSource> datasources = new ConcurrentHashMap<>();

    private static DataSource dataSource(String jdbcUrl) {
        return datasources.computeIfAbsent(
            jdbcUrl, key -> {
                PGSimpleDataSource ds = new PGSimpleDataSource();
                ds.setUser("postgres");
                ds.setPassword("secret");
                ds.setURL(key);
                return ds;
            });
    }

    public static Connection connection(String jbdcUrl) throws SQLException {
        return dataSource(jbdcUrl).getConnection();
    }

}
