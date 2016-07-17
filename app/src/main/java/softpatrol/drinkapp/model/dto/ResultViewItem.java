package softpatrol.drinkapp.model.dto;

import softpatrol.drinkapp.database.models.recipe.Recipe;

/**
 * Created by root on 6/18/16.
 */
public class ResultViewItem {

    private Recipe recipe;
    private SearchResultSimple result;


    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public SearchResultSimple getResult() {
        return result;
    }

    public void setResult(SearchResultSimple result) {
        this.result = result;
    }
}
