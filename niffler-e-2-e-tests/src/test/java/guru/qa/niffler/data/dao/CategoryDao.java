package guru.qa.niffler.data.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import guru.qa.niffler.data.entity.spend.CategoryEntity;

public interface CategoryDAO {
    CategoryEntity create(CategoryEntity category);

    Optional<CategoryEntity> findCategoryById(UUID id);

    Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName);

    List<CategoryEntity> findAllByUsername(String username);

    void deleteCategory(CategoryEntity category);

}
