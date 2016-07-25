package softpatrol.drinkapp.model.dto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by root on 7/15/16.
 */
public class SearchResult {

    private String name;
    private Long recipeId;
    private int totalMisses;
    private Set<Long> categoryHits;
    private Set<Long> ingredMisses;

    public int getTotalMisses() {
        return totalMisses;
    }

    public void setTotalMisses(int totalMisses) {
        this.totalMisses = totalMisses;
    }

    public Long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }

    public static SearchResult deserialize(JSONObject obj) throws JSONException {
        SearchResult sr = new SearchResult();

        sr.setRecipeId(obj.getLong("recipeId"));
        sr.setTotalMisses(obj.getInt("totalMisses"));
        sr.setName(obj.getString("name"));

        // Set ingredient misses
        HashSet<Long> ingredMisses = new HashSet<>();
        JSONArray ingredArr = obj.getJSONArray("ingredientMisses");

        for (int i = 0; i < ingredArr.length();i++) {
            ingredMisses.add(ingredArr.getLong(i));
        }
        sr.setIngredMisses(ingredMisses);

        // Set category misses
        HashSet<Long> categoryHits = new HashSet<>();
        JSONArray categoryArr = obj.getJSONArray("categoryHits");

        for (int i = 0; i < categoryArr.length();i++) {
            categoryHits.add(ingredArr.getLong(i));
        }
        sr.setCategoryHits(categoryHits);

        return sr;
    }

    public Set<Long> getCategoryHits() {
        return categoryHits;
    }

    public void setCategoryHits(Set<Long> categoryHits) {
        this.categoryHits = categoryHits;
    }

    public Set<Long> getIngredMisses() {
        return ingredMisses;
    }

    public void setIngredMisses(Set<Long> ingredMisses) {
        this.ingredMisses = ingredMisses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
