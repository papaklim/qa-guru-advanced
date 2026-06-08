package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;

public class ProfilePage {
    private final SelenideElement inputCategoryField = $("#category");
    private final SelenideElement editCategoryInput = $("input[placeholder='Edit category']");
    private final ElementsCollection categoryChips = $$("span.MuiChip-label");
    private final SelenideElement archiveCategoryModalBtn = $x("//button[text()='Archive']");
    private final SelenideElement showArchivedCheckbox = $("[type=checkbox]");
    private final String editCategoryButtonSelector = "button[aria-label='Edit category']";
    private final String archiveCategoryButtonSelector = "button[aria-label='Archive category']";

    public ProfilePage addNewCategory(String categoryName) {
        inputCategoryField.setValue(categoryName);
        inputCategoryField.pressEnter();
        return this;
    }

    public ProfilePage editCategory(String currentCategoryName, String newCategoryName) {
        categoryChips.find(Condition.text(currentCategoryName))
                .parent()
                .parent()
                .find(editCategoryButtonSelector)
                .click();
        editCategoryInput.setValue(newCategoryName);
        editCategoryInput.pressEnter();
        return this;
    }

    public ProfilePage archiveCategory(String categoryName) {
        categoryChips.find(Condition.text(categoryName))
                .parent()
                .parent()
                .find(archiveCategoryButtonSelector)
                .click();
        archiveCategoryModalBtn
                .click();
        return this;
    }

    public ProfilePage checkThatCategoryExistsByName(String categoryName) {
        categoryChips.find(Condition.text(categoryName))
                .shouldBe(Condition.visible);
        return this;
    }

    public ProfilePage checkThatCategoryArchivedByName(String categoryName) {
        showArchivedCheckbox.click();
        categoryChips.find(Condition.text(categoryName))
                .shouldBe(Condition.visible);
        return this;
    }
}
