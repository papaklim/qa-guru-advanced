package guru.qa.niffler.data.dao;

import java.util.Optional;
import java.util.UUID;

import guru.qa.niffler.data.entity.spend.CategoryEntity;

public interface CategoryDao {
    CategoryEntity create(CategoryEntity category);

    Optional<CategoryEntity> findCategoryById(UUID id);
}
