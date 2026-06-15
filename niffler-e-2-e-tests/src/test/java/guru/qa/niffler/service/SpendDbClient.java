package guru.qa.niffler.service;

import guru.qa.niffler.api.SpendClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.spend.SpendJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDbClient implements SpendClient {
    private static final Config CFG = Config.getInstance();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(CFG.spendJdbcUrl());

    private final CategoryDaoJdbc categoryDaoJdbc = new CategoryDaoJdbc();
    private final SpendDaoJdbc spendDaoJdbc = new SpendDaoJdbc();

    public SpendJson createSpend(SpendJson spend) {
        return jdbcTxTemplate.execute(() -> {
                SpendEntity spendEntity = SpendEntity.fromJson(spend);
                if (spendEntity.getCategory().getId() == null) {
                    CategoryEntity categoryEntity = categoryDaoJdbc.create(spendEntity.getCategory());
                    spendEntity.setCategory(categoryEntity);
                }
                return SpendJson.fromEntity(spendDaoJdbc.create(spendEntity));
            }
        );
    }

    @Override
    public SpendJson updateSpend(SpendJson spend) {
        return null;
    }

    @Override
    public Optional<SpendJson> getSpendByIdAndUserName(UUID id, String username) {
        return jdbcTxTemplate.execute(() -> spendDaoJdbc.findSpendByIdAndUserName(id, username)
            .map(SpendJson::fromEntity));
    }

    @Override
    public List<SpendJson> getAllSpends(String username, CurrencyValues filterCurrency, String from, String to) {
        throw new UnsupportedOperationException("Not implemented :(");
    }

    @Override
    public void deleteSpend(List<UUID> ids, String userName) {
        jdbcTxTemplate.execute(() -> {
                spendDaoJdbc.deleteSpend(ids, userName);
                return null;
            }
        );
    }


    public List<SpendJson> findAllSpendsByUsername(String username) {
        return jdbcTxTemplate.execute(() -> {
                return spendDaoJdbc.findAllByUsername(username)
                    .stream()
                    .map(SpendJson::fromEntity)
                    .toList();
            }
        );
    }


    public CategoryJson createCategory(CategoryJson category) {
        return jdbcTxTemplate.execute(() -> {
                CategoryEntity categoryEntity = categoryDaoJdbc.create(CategoryEntity.fromJson(category));
                return CategoryJson.fromEntity(categoryEntity);
            }
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
        return jdbcTxTemplate.execute(() -> findCategoryByUsernameAndCategoryName(username, categoryName));
    }

    public Optional<CategoryJson> findCategoryById(UUID id) {
        return jdbcTxTemplate.execute(() -> categoryDaoJdbc.findCategoryById(id).map(CategoryJson::fromEntity));
    }

    public Optional<CategoryJson> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return jdbcTxTemplate.execute(() -> categoryDaoJdbc.findCategoryByUsernameAndCategoryName(username, categoryName).map(CategoryJson::fromEntity));
    }

    public List<CategoryJson> findAllCategoriesByUsername(String username) {
        return jdbcTxTemplate.execute(() -> categoryDaoJdbc.findAllByUsername(username).stream().map(CategoryJson::fromEntity).toList());
    }

    public void deleteCategory(CategoryJson category) {
        jdbcTxTemplate.execute(() -> {
            categoryDaoJdbc.deleteCategory(CategoryEntity.fromJson(category));
            return null;
        });
    }
}
