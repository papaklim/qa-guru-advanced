package guru.qa.niffler.api;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;

import java.util.List;
import java.util.Optional;

public interface SpendClient {

    SpendJson createSpend(SpendJson spend);

    SpendJson updateSpend(SpendJson spend);

    SpendJson getSpendById(Integer id);

    List<SpendJson> getAllSpends(String username, CurrencyValues filterCurrency, String from, String to);

    void deleteSpend(String userName, List<String> ids);

    CategoryJson createCategory(CategoryJson category);

    CategoryJson updateCategory(CategoryJson category);

    List<CategoryJson> getAllCategories(String username, Boolean excludeArchived);

    Optional<CategoryJson> findCategoryByNameAndUsername(String categoryName, String username);
}
