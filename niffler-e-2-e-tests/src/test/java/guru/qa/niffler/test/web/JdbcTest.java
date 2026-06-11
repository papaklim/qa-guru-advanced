package guru.qa.niffler.test.web;

import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendDbClient;

public class JdbcTest {

    @Test
    void jdbcTest() {
        SpendDbClient spendDbClient = new SpendDbClient();

        spendDbClient.createSpend(
            new SpendJson(
                null,
                new Date(), new CategoryJson(
                    null,
                    "dao-test-category" + UUID.randomUUID().toString().substring(0, 8),
                    "admin",
                    false),
                CurrencyValues.RUB,
                6.66,
                "desc",
                "admin")
        );
    }

}
