package guru.qa.niffler.service;

import guru.qa.niffler.api.SpendClient;
import guru.qa.niffler.data.dao.CategoryDAO;
import guru.qa.niffler.data.dao.SpendDAO;
import guru.qa.niffler.data.dao.impl.CategoryDAOJdbc;
import guru.qa.niffler.data.dao.impl.SpendDAOJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDbClient implements SpendClient {
    private final SpendDAO spendDao = new SpendDAOJdbc();
    private final CategoryDAO categoryDao = new CategoryDAOJdbc();

    public SpendJson createSpend(SpendJson spend) {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        if (spendEntity.getCategory().getId() == null) {
            CategoryEntity categoryEntity = categoryDao.findCategoryByUsernameAndCategoryName(
                spendEntity.getCategory().getUsername(),
                spendEntity.getCategory().getName()
            ).orElseGet(() -> categoryDao.create(spendEntity.getCategory()));
            spendEntity.setCategory(categoryEntity);
        }
        return SpendJson.fromEntity(spendDao.create(spendEntity));
    }

    @Override
    public SpendJson updateSpend(SpendJson spend) {
        return null;
    }

    @Override
    public Optional<SpendJson> getSpendByIdAndUserName(UUID id, String username) {
        return spendDao.findSpendByIdAndUserName(id, username).map(SpendJson::fromEntity);
    }

    @Override
    public List<SpendJson> getAllSpends(String username, CurrencyValues filterCurrency, String from, String to) {
        return List.of();
    }

    @Override
    public void deleteSpend(List<UUID> ids, String userName) {
    spendDao.deleteSpend(ids, userName);
    }

    public List<SpendJson> findAllSpendsByUsername(String username) {
        return spendDao.findAllByUsername(username).stream().map(SpendJson::fromEntity).toList();
    }

    public CategoryJson createCategory(CategoryJson category) {
        CategoryEntity categoryEntity = categoryDao.create(CategoryEntity.fromJson(category));
        return CategoryJson.fromEntity(categoryEntity);
    }

    @Override
    public CategoryJson updateCategory(CategoryJson category) {
        return null;
    }

    @Override
    public List<CategoryJson> getAllCategories(String username, Boolean excludeArchived) {
        return List.of();
    }

    @Override
    public Optional<CategoryJson> findCategoryByNameAndUsername(String categoryName, String username) {
        return Optional.empty();
    }

    public Optional<CategoryJson> findCategoryById(UUID id) {
        return categoryDao.findCategoryById(id).map(CategoryJson::fromEntity);
    }

    public Optional<CategoryJson> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return categoryDao.findCategoryByUsernameAndCategoryName(username, categoryName).map(CategoryJson::fromEntity);
    }

    public List<CategoryJson> findAllCategoriesByUsername(String username) {
        return categoryDao.findAllByUsername(username).stream().map(CategoryJson::fromEntity).toList();
    }

    public void deleteCategory(CategoryJson category) {
        categoryDao.deleteCategory(CategoryEntity.fromJson(category));
    }
}
