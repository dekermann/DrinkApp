package softpatrol.drinkapp.model.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 6/18/16.
 */
public class SearchResult  implements Comparable{

    private int recipieId;

    private List<Integer> ingredientMatches;
    private List<Integer> ingredientNoMatches;
    private List<Integer> categoryMatches;
    private List<Integer> categoryNoMatches;

    public SearchResult() {
        ingredientMatches = new ArrayList<>();
        ingredientNoMatches = new ArrayList<>();
        categoryMatches = new ArrayList<>();
        categoryNoMatches = new ArrayList<>();
    }


    public int getRecipieId() {
        return recipieId;
    }

    public void setRecipieId(int recipieId) {
        this.recipieId = recipieId;
    }

    public List<Integer> getIngredientMatches() {
        return ingredientMatches;
    }

    public void setIngredientMatches(List<Integer> ingredientMatches) {
        this.ingredientMatches = ingredientMatches;
    }

    public List<Integer> getIngredientNoMatches() {
        return ingredientNoMatches;
    }

    public void setIngredientNoMatches(List<Integer> ingredientNoMatches) {
        this.ingredientNoMatches = ingredientNoMatches;
    }

    public List<Integer> getCategoryMatches() {
        return categoryMatches;
    }

    public void setCategoryMatches(List<Integer> categoryMatches) {
        this.categoryMatches = categoryMatches;
    }

    public List<Integer> getCategoryNoMatches() {
        return categoryNoMatches;
    }

    public void setCategoryNoMatches(List<Integer> categoryNoMatches) {
        this.categoryNoMatches = categoryNoMatches;
    }

    public float matchPercent() {
        float mathes = getIngredientMatches().size();
        float total = getIngredientMatches().size() + getCategoryNoMatches().size() + getIngredientNoMatches().size() + getCategoryMatches().size();
        return mathes/total;
    }

    @Override
    public int compareTo(Object compare) {
        SearchResult compareage=((SearchResult)compare);

        float myPercent = matchPercent();
        float comparePercent = compareage.matchPercent();

        if (myPercent > comparePercent) {
            return 1;
        } else if (myPercent == comparePercent) {
            return 0;
        } else {
            return -1;
        }
    }
}
