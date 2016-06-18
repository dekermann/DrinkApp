package softpatrol.drinkapp.model.dto;

import java.util.List;

import softpatrol.drinkapp.database.models.ingredient.Category;
import softpatrol.drinkapp.database.models.ingredient.Ingredient;
import softpatrol.drinkapp.database.models.recipe.Recipe;

/**
 * Created by root on 6/18/16.
 */
public class ResultViewItem {

    private Recipe recipe;
    private SearchResult result;

    private List<Ingredient> missingIngredients;
    private List<Category> missingCategories;

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public List<Ingredient> getMissingIngredients() {
        return missingIngredients;
    }

    public void setMissingIngredients(List<Ingredient> missingIngredients) {
        this.missingIngredients = missingIngredients;
    }

    public List<Category> getMissingCategories() {
        return missingCategories;
    }

    public void setMissingCategories(List<Category> missingCategories) {
        this.missingCategories = missingCategories;
    }

    public SearchResult getResult() {
        return result;
    }

    public void setResult(SearchResult result) {
        this.result = result;
    }
}
