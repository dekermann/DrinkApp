package softpatrol.drinkapp.activities.fragments.result;

import softpatrol.drinkapp.database.models.recipe.Recipe;
import softpatrol.drinkapp.model.dto.SearchResult;

/**
 * Created by root on 6/18/16.
 */
public class ResultViewItem {

    private Recipe recipe;
    private SearchResult result;


    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public SearchResult getResult() {
        return result;
    }

    public void setResult(SearchResult result) {
        this.result = result;
    }
}
