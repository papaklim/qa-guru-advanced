package guru.qa.niffler.api;

import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.spend.SpendJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendClient {

    SpendJson createSpend(SpendJson spend);

    SpendJson updateSpend(SpendJson spend);

    Optional<SpendJson> getSpendByIdAndUserName(UUID id, String username);

    List<SpendJson> getAllSpends(String username, CurrencyValues filterCurrency, String from, String to);

    void deleteSpend(List<UUID> ids, String userName);

    CategoryJson createCategory(CategoryJson category);

    CategoryJson updateCategory(CategoryJson category);

    List<CategoryJson> getAllCategories(String username, Boolean excludeArchived);

    Optional<CategoryJson> findCategoryByNameAndUsername(String categoryName, String username);
}
