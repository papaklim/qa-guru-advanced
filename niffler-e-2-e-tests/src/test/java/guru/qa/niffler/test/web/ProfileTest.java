package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class ProfileTest {

    private final static String DUMMY_PASSWORD = "12345";
    private final static String EXISTED_USERNAME = "admin";
    private final static String CATEGORY_NAME = "test_category_".concat(String.valueOf(System.currentTimeMillis()));
    private static final Config CFG = Config.getInstance();

    @Test
    void createCategoryTest() {
        LoginPage loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
        MainPage mainPage = loginPage.login(EXISTED_USERNAME, DUMMY_PASSWORD);
        mainPage.checkThatPageLoaded();
        ProfilePage profilePage = mainPage.goToProfile();
        profilePage.addNewCategory(CATEGORY_NAME);
        profilePage.checkThatCategoryExistsByName(CATEGORY_NAME);
    }

    @Category(
            username = EXISTED_USERNAME,
            archived = false)
    @Test
    void updateExistedCategoryNameTest(CategoryJson categoryData) {
        var currentCategoryName = categoryData.name();
        LoginPage loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
        MainPage mainPage = loginPage.login(EXISTED_USERNAME, DUMMY_PASSWORD);
        mainPage.checkThatPageLoaded();
        ProfilePage profilePage = mainPage.goToProfile();
        profilePage.checkThatCategoryExistsByName(currentCategoryName);
        profilePage.editCategory(currentCategoryName, "UPDATED_".concat(currentCategoryName));
        profilePage.checkThatCategoryExistsByName("UPDATED_".concat(currentCategoryName));
    }

    @Category(
            username = EXISTED_USERNAME,
            archived = false)
    @Test
    void archiveExistedCategoryByNameTest(CategoryJson categoryData) {
        LoginPage loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
        MainPage mainPage = loginPage.login(EXISTED_USERNAME, DUMMY_PASSWORD);
        mainPage.checkThatPageLoaded();
        ProfilePage profilePage = mainPage.goToProfile();
        profilePage.checkThatCategoryExistsByName(categoryData.name());
        profilePage.archiveCategory(categoryData.name());
        profilePage.checkThatCategoryArchivedByName(categoryData.name());
    }
}
