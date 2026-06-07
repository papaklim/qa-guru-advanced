package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage {
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement passwordSubmitInput = $("#passwordSubmit");
    private final SelenideElement submitBtn = $("#register-button");
    private final SelenideElement errorMessage = $(".form__error");
    private final SelenideElement signInBtn = $(".form_sign-in");


    public RegisterPage setUsername(String username) {
        usernameInput.val(username);
        return this;
    }

    public RegisterPage setPassword(String password){
        passwordInput.val(password);
        return this;
    }
    public RegisterPage setPasswordSubmit(String password){
        passwordSubmitInput.val(password);
        return this;
    }

    public LoginPage submitRegistration(String username, String password, String confirmPassword) {
        usernameInput.val(username);
        passwordInput.val(password);
        passwordSubmitInput.val(confirmPassword);
        submitBtn.click();
        signInBtn.click();
        return new LoginPage();
    }

    public RegisterPage submitRegistrationWithError(String username, String password, String confirmPassword) {
        usernameInput.val(username);
        passwordInput.val(password);
        passwordSubmitInput.val(confirmPassword);
        submitBtn.click();
        return this;
    }

    public RegisterPage checkErrorMessage(String errorMessageText){
        errorMessage.shouldHave(text(errorMessageText));
        return this;
    }
}
