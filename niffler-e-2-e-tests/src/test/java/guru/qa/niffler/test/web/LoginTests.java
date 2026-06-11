package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class LoginTests {
    private static final Config CFG = Config.getInstance();
    private final static String DUMMY_PASSWORD = "12345";
    private final static String EXISTED_USERNAME = "admin";
    private final static String INVALID_USER_DATA_ERROR_MESSAGE = "Неверные учетные данные пользователя";

    @Test
    void mainPageShouldBeDisplayedAfterSuccessLogin() {
        LoginPage loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
        MainPage mainPage = loginPage.login(EXISTED_USERNAME, DUMMY_PASSWORD);
        mainPage.checkThatPageLoaded();
    }

    @Test
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        LoginPage loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
        loginPage.loginWithError(EXISTED_USERNAME, "WRONG_PASSWORD");
        loginPage.checkErrorMessage(INVALID_USER_DATA_ERROR_MESSAGE);
    }
}
