package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendDbClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JdbcTest {

    private final SpendDbClient spendDbClient = new SpendDbClient();
    private CategoryJson createdCategory;
    private SpendJson createdSpend;

    @AfterEach
    void cleanup() {
        if (createdSpend != null) {
            spendDbClient.deleteSpend(List.of(createdSpend.id()), createdSpend.username());
        }
        if (createdCategory != null) {
            spendDbClient.deleteCategory(createdCategory);
        }
    }

    @Test
    void createCategoryTest() {
        CategoryJson category = new CategoryJson(
            null,
            "category-" + UUID.randomUUID().toString().substring(0, 8),
            "admin",
            false
        );

        createdCategory = spendDbClient.createCategory(category);

        // 1. Проверяем, что ID сгенерирован базой данных
        assertNotNull(createdCategory.id());

        // 2. Проверяем корректность вставленных полей
        assertEquals(category.name(), createdCategory.name());
        assertEquals(category.username(), createdCategory.username());
        assertFalse(createdCategory.archived());

        // 3. Проверяем, что сущность можно найти в БД по ID
        Optional<CategoryJson> found = spendDbClient.findCategoryById(createdCategory.id());
        assertTrue(found.isPresent());
        assertEquals(createdCategory.name(), found.get().name());
    }

    @Test
    void createSpendTest() {
        CategoryJson category = new CategoryJson(
            null,
            "category-" + UUID.randomUUID().toString().substring(0, 8),
            "admin",
            false
        );

        createdCategory = spendDbClient.createCategory(category);

        SpendJson spend = new SpendJson(
            null,
            new Date(System.currentTimeMillis()),
            createdCategory,
            CurrencyValues.RUB,
            7.77,
            "Desc",
            "admin"
        );

        createdSpend = spendDbClient.createSpend(spend);

        // 1. Проверяем, что ID сгенерирован базой данных
        assertNotNull(createdSpend.id());

        // 2. Проверяем корректность вставленных полей
        assertEquals(spend.username(), createdSpend.username());
        assertEquals(spend.spendDate(), createdSpend.spendDate());

        // 3. Проверяем, что сущность можно найти в БД по ID
        Optional<SpendJson> found = spendDbClient.getSpendByIdAndUserName(createdSpend.id(), createdSpend.username());

        assertTrue(found.isPresent());
        assertEquals(createdSpend.category(), found.get().category());
    }
}
