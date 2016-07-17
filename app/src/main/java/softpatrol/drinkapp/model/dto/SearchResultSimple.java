package softpatrol.drinkapp.model.dto;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 7/15/16.
 */
public class SearchResultSimple {

    private int recipeId;
    private int totalMisses;
    private int categoryMisses;
    private int ingredMisses;

    public int getCategoryMisses() {
        return categoryMisses;
    }

    public void setCategoryMisses(int categoryMisses) {
        this.categoryMisses = categoryMisses;
    }

    public int getIngredMisses() {
        return ingredMisses;
    }

    public void setIngredMisses(int ingredMisses) {
        this.ingredMisses = ingredMisses;
    }

    public int getTotalMisses() {
        return totalMisses;
    }

    public void setTotalMisses(int totalMisses) {
        this.totalMisses = totalMisses;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public static SearchResultSimple deserialize(JSONObject obj) throws JSONException {
        SearchResultSimple sr = new SearchResultSimple();

        sr.setRecipeId(obj.getInt("recipe_id"));

        sr.setIngredMisses(obj.getInt("ingred_count_misses"));
        sr.setTotalMisses(obj.getInt("total_misses"));
        sr.setCategoryMisses(obj.getInt("cat_count_misses"));
        return sr;
    }
}
