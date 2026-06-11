package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.DisabledByIssue;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.RegisterPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class RegistrationTests {

    private static final Config CFG = Config.getInstance();
    private final static String DUMMY_PASSWORD = "12345";
    private final static String EXISTED_USERNAME = "admin";
    private final static String ALLOWED_PASSWORD_ERROR_MESSAGE = "Allowed password length should be from 3 to 12 characters";
    private final static String EXISTED_USER_ERROR_MESSAGE = "Username `%s` already exists";
    private final static String NOT_EQUAL_PASSWORDS_ERROR_MESSAGE = "Passwords should be equal";

    @DisabledByIssue("1")
    @Test
    void shouldRegisterNewUser() {
        LoginPage loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
        RegisterPage registerPage = loginPage.registerNewUser();
        String USERNAME = "test_user_".concat(String.valueOf(System.currentTimeMillis()));
        registerPage.submitRegistration(USERNAME, DUMMY_PASSWORD, DUMMY_PASSWORD);
        MainPage mainPage = loginPage.login(USERNAME, DUMMY_PASSWORD);
        mainPage.checkThatPageLoaded();
    }

    @Test
    void shouldNotRegisterUserWithExistingUsername() {
        LoginPage loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
        RegisterPage registerPage = loginPage.registerNewUser();
        registerPage.submitRegistrationWithError(EXISTED_USERNAME, DUMMY_PASSWORD, DUMMY_PASSWORD);
        registerPage.checkErrorMessage(EXISTED_USER_ERROR_MESSAGE.formatted(EXISTED_USERNAME));
    }

    @Test
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        LoginPage loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
        RegisterPage registerPage = loginPage.registerNewUser();
        registerPage.submitRegistrationWithError(EXISTED_USERNAME, DUMMY_PASSWORD, DUMMY_PASSWORD.substring(0, 3));
        registerPage.checkErrorMessage(NOT_EQUAL_PASSWORDS_ERROR_MESSAGE);
    }

    @Test
    void shouldShowErrorIfPasswordWithLengthLessThan3() {
        LoginPage loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
        RegisterPage registerPage = loginPage.registerNewUser();
        registerPage.submitRegistrationWithError(
            EXISTED_USERNAME, DUMMY_PASSWORD.substring(0, 2),
            DUMMY_PASSWORD.substring(0, 2));
        registerPage.checkErrorMessage(ALLOWED_PASSWORD_ERROR_MESSAGE);
    }

    @Test
    void shouldShowErrorIfPasswordWithLengthMoreThan12() {
        LoginPage loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
        RegisterPage registerPage = loginPage.registerNewUser();
        registerPage.submitRegistrationWithError(EXISTED_USERNAME, DUMMY_PASSWORD.repeat(3), DUMMY_PASSWORD.repeat(3));
        registerPage.checkErrorMessage(ALLOWED_PASSWORD_ERROR_MESSAGE);
    }
}
