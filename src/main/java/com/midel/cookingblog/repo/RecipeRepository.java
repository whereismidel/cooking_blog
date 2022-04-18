package com.midel.cookingblog.repo;

import com.midel.cookingblog.models.Recipe;
import org.springframework.data.repository.CrudRepository;

public interface RecipeRepository extends CrudRepository<Recipe, Long> {
}
