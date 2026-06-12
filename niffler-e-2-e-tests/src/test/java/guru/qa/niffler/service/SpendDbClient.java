package guru.qa.niffler.service;

import guru.qa.niffler.api.SpendClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDAOJdbc;
import guru.qa.niffler.data.dao.impl.SpendDAOJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.spend.SpendJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.transaction;

public class SpendDbClient implements SpendClient {
    private static final Config CFG = Config.getInstance();

    public SpendJson createSpend(SpendJson spend) {
        return transaction(connection -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spend);
            if (spendEntity.getCategory().getId() == null) {
                CategoryEntity categoryEntity = new CategoryDAOJdbc(connection).create(spendEntity.getCategory());
                spendEntity.setCategory(categoryEntity);
            }
            return SpendJson.fromEntity(new SpendDAOJdbc(connection).create(spendEntity));
        }, CFG.spendJdbcUrl());
    }

    @Override
    public SpendJson updateSpend(SpendJson spend) {
        return null;
    }

    @Override
    public Optional<SpendJson> getSpendByIdAndUserName(UUID id, String username) {
        return transaction(connection -> {
                return new SpendDAOJdbc(connection).findSpendByIdAndUserName(id, username)
                    .map(SpendJson::fromEntity);
            }, CFG.spendJdbcUrl()
        );
    }

    @Override
    public List<SpendJson> getAllSpends(String username, CurrencyValues filterCurrency, String from, String to) {
        throw new UnsupportedOperationException("Not implemented :(");
    }

    @Override
    public void deleteSpend(List<UUID> ids, String userName) {
        transaction(connection -> {
                new SpendDAOJdbc(connection).deleteSpend(ids, userName);
            }, CFG.spendJdbcUrl()
        );
    }

    public List<SpendJson> findAllSpendsByUsername(String username) {
        return transaction(connection -> {
                return new SpendDAOJdbc(connection).findAllByUsername(username)
                    .stream()
                    .map(SpendJson::fromEntity)
                    .toList();
            }, CFG.spendJdbcUrl()
        );
    }


    public CategoryJson createCategory(CategoryJson category) {
        return transaction(connection -> {
                CategoryEntity categoryEntity = new CategoryDAOJdbc(connection).create(CategoryEntity.fromJson(category));
                return CategoryJson.fromEntity(categoryEntity);
            }, CFG.spendJdbcUrl()
        );
    }

    @Override
    public CategoryJson updateCategory(CategoryJson category) {
        throw new UnsupportedOperationException("Not implemented :(");
    }

    @Override
    public List<CategoryJson> getAllCategories(String username, Boolean excludeArchived) {
        throw new UnsupportedOperationException("Not implemented :(");
    }

    @Override
    public Optional<CategoryJson> findCategoryByNameAndUsername(String categoryName, String username) {
        return transaction(connection -> {
            return findCategoryByUsernameAndCategoryName(username, categoryName);
        }, CFG.spendJdbcUrl());
    }

    public Optional<CategoryJson> findCategoryById(UUID id) {
        return transaction(connection -> {
            return new CategoryDAOJdbc(connection).findCategoryById(id).map(CategoryJson::fromEntity);
        }, CFG.spendJdbcUrl());
    }

    public Optional<CategoryJson> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return transaction(connection -> {
            return new CategoryDAOJdbc(connection).findCategoryByUsernameAndCategoryName(username, categoryName).map(CategoryJson::fromEntity);
        }, CFG.spendJdbcUrl());
    }

    public List<CategoryJson> findAllCategoriesByUsername(String username) {
        return transaction(connection -> {
            return new CategoryDAOJdbc(connection).findAllByUsername(username).stream().map(CategoryJson::fromEntity).toList();
        }, CFG.spendJdbcUrl());
    }

    public void deleteCategory(CategoryJson category) {
        transaction(connection -> {
            new CategoryDAOJdbc(connection).deleteCategory(CategoryEntity.fromJson(category));
        }, CFG.spendJdbcUrl());
    }
}
