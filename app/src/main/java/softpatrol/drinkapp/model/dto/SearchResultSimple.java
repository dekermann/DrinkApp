package softpatrol.drinkapp.model.dto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by root on 7/15/16.
 */
public class SearchResultSimple {

    private int recipeId;
    private int totalMisses;
    private Set<Integer> categoryHits;
    private Set<Integer> ingredMisses;

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

        sr.setRecipeId(obj.getInt("recipeId"));
        sr.setTotalMisses(obj.getInt("totalMisses"));

        // Set ingredient misses
        HashSet<Integer> ingredMisses = new HashSet<>();
        JSONArray ingredArr = obj.getJSONArray("ingredientMisses");

        for (int i = 0; i < ingredArr.length();i++) {
            ingredMisses.add(ingredArr.getInt(i));
        }
        sr.setIngredMisses(ingredMisses);

        // Set category misses
        HashSet<Integer> categoryHits = new HashSet<>();
        JSONArray categoryArr = obj.getJSONArray("categoryHits");

        for (int i = 0; i < categoryArr.length();i++) {
            categoryHits.add(ingredArr.getInt(i));
        }
        sr.setCategoryHits(categoryHits);

        return sr;
    }

    public Set<Integer> getCategoryHits() {
        return categoryHits;
    }

    public void setCategoryHits(Set<Integer> categoryHits) {
        this.categoryHits = categoryHits;
    }

    public Set<Integer> getIngredMisses() {
        return ingredMisses;
    }

    public void setIngredMisses(Set<Integer> ingredMisses) {
        this.ingredMisses = ingredMisses;
    }
}
