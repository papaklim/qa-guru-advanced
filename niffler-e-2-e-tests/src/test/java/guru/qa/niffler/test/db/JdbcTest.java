package guru.qa.niffler.test.db;

import com.github.javafaker.Faker;
import guru.qa.niffler.model.auth.AuthUserJson;
import guru.qa.niffler.model.auth.Authority;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersDbClient;
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
    private final UsersDbClient usersDbClient = new UsersDbClient();
    private CategoryJson createdCategory;
    private SpendJson createdSpend;
    private UserJson createdAuthUser;
    private UserJson createdUserdataUser;
    private final Faker faker = new Faker();

//    @AfterEach
//    void cleanup() {
//        if (createdSpend != null) {
//            spendDbClient.deleteSpend(List.of(createdSpend.id()), createdSpend.username());
//        }
//        if (createdCategory != null) {
//            spendDbClient.deleteCategory(createdCategory);
//        }
//        if (createdAuthUser != null) {
//            UsersDbClient.deleteAuthUser(createdAuthUser);
//        }
//        if (createdUserdataUser != null) {
//            UsersDbClient.deleteUserdataUser(createdUserdataUser);
//        }
//    }

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
        SpendJson spend = new SpendJson(
            null,
            new Date(System.currentTimeMillis()),
            new CategoryJson(
                null,
                "category-" + UUID.randomUUID().toString().substring(0, 8),
                "admin",
                false
            ),
            CurrencyValues.RUB,
            7.77,
            "Desc",
            null
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

//    @Test
//    void successXaTransactionTest() {
//        String username = faker.name().username();
//        String firstname = faker.name().firstName();
//        String surname = faker.name().lastName();
//        String fullname = firstname + " " + surname;
//
//        AuthUserJson authUser = new AuthUserJson(
//            null,
//            username,
//            "password",
//            true,
//            true,
//            true,
//            true,
//            List.of(Authority.read, Authority.write)
//        );
//
//        UserJson user = new UserJson(
//            null,
//            username,
//            firstname,
//            surname,
//            fullname,
//            CurrencyValues.RUB,
//            null,
//            null,
//            null
//        );
//
//        createdAuthUser = usersDbClient.createUser(authUser, user);
//        createdUserdataUser = user;
//
//        assertNotNull(createdAuthUser.id());
//
//        // Проверяем, что в обеих базах данные действительно записались
//        Optional<AuthUserJson> authUserInDb = usersDbClient.findAuthUserByUsername(username);
//        Optional<UserJson> userdataUserInDb = usersDbClient.findUdUserByUsername(username);
//
//        assertTrue(authUserInDb.isPresent());
//        assertTrue(userdataUserInDb.isPresent());
//
//        assertEquals(username, authUserInDb.get().username());
//        assertEquals(username, userdataUserInDb.get().username());
//    }
//
//    @Test
//    void rollbackXaTransactionTest() {
//        String username = "xa-user-rollback-" + UUID.randomUUID().toString().substring(0, 8);
//
//        // Пароль null приведет к ошибке в auth БД
//        AuthUserJson authUser = new AuthUserJson(
//            null,
//            username,
//            null,
//            true,
//            true,
//            true,
//            true,
//            List.of(Authority.read, Authority.write)
//        );
//
//        UserJson user = new UserJson(
//            null,
//            username,
//            "Firstname",
//            "Surname",
//            "Full Name",
//            CurrencyValues.RUB,
//            null,
//            null,
//            null
//        );
//
//        try {
//            usersDbClient.createUser(authUser, user);
//        } catch (Exception e) {
//            // Ожидаем ошибку транзакции
//            System.out.println("Transaction failed as expected: " + e.getMessage());
//        }
//
//        // Проверяем, что пользователя нет ни в одной из баз
//        Optional<AuthUserJson> authUserInDb = usersDbClient.findAuthUserByUsername(username);
//        Optional<UserJson> userdataUserInDb = usersDbClient.findUdUserByUsername(username);
//
//        assertFalse(authUserInDb.isPresent());
//        assertFalse(userdataUserInDb.isPresent());
//    }

    @Test
    void successSpringJdbcTransactionTest() {
        String username = faker.name().username();
        String firstname = faker.name().firstName();
        String surname = faker.name().lastName();
        String fullname = firstname + " " + surname;

        AuthUserJson authUser = new AuthUserJson(
            null,
            username,
            "password",
            true,
            true,
            true,
            true,
            List.of(Authority.read, Authority.write)
        );

        UserJson user = new UserJson(
            null,
            username,
            firstname,
            surname,
            fullname,
            CurrencyValues.RUB,
            null,
            null,
            null
        );

        createdAuthUser = usersDbClient.createUserSpringJdbc(authUser, user);
        createdUserdataUser = user;

        assertNotNull(createdAuthUser.id());

        // Проверяем, что в обеих базах данные действительно записались
        Optional<AuthUserJson> authUserInDb = usersDbClient.findAuthUserByUsernameSpringJdbc(username);
        Optional<UserJson> userdataUserInDb = usersDbClient.findUdUserByUsernameSpringJdbc(username);

        assertTrue(authUserInDb.isPresent());
        assertTrue(userdataUserInDb.isPresent());

        assertEquals(username, authUserInDb.get().username());
        assertEquals(username, userdataUserInDb.get().username());
    }
}
