package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class SpendingTest {

    private final static String DUMMY_PASSWORD = "12345";
    private final static String EXISTED_USERNAME = "admin";
    private static final Config CFG = Config.getInstance();

    @Spending(
            username = EXISTED_USERNAME,
            category = "Дефолтная категория",
            amount = 66.6,
            currency = CurrencyValues.RUB,
            description = "Дефолтный декскрипшен"
    )
    @Test
    void spendingDescriptionShouldBeEditedByTableAction(SpendJson spending) {
        final String newDescription = "Обновленный дескрипшен";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(EXISTED_USERNAME, DUMMY_PASSWORD)
                .editSpending(spending.description())
                .setNewSpendingDescription(newDescription)
                .save()
                .checkThatTableContains(newDescription);
    }
}
