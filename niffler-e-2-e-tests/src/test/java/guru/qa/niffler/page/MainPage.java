package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage {
    private final ElementsCollection tableRows = $$("#spendings tr");
    private final SelenideElement statBlock = $("#stat");
    private final SelenideElement spendingBlock = $("#spendings");
    private final SelenideElement profileBtn = $("img[class^='MuiAvatar-img']");
    private final ElementsCollection profileMenuItems = $$("ul[role=menu] > li[role=menuitem]");

    public ProfilePage goToProfile() {
        profileBtn.click();
        profileMenuItems.get(0).click();
        return new ProfilePage();
    }

    public EditSpendingPage editSpending(String description) {
        tableRows.find(text(description)).$$("td").get(5).click();
        return new EditSpendingPage();
    }

    public MainPage checkThatTableContains(String description) {
        tableRows.find(text(description)).should(visible);
        return this;
    }

    public MainPage checkThatPageLoaded() {
        statBlock.shouldBe(visible);
        spendingBlock.shouldBe(visible);
        return this;
    }
}
