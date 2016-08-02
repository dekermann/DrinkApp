package softpatrol.drinkapp.activities.fragments.result;

import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

import softpatrol.drinkapp.database.models.ingredient.Category;

/**
 * Created by rasmus on 7/23/16.
 */
public class FilterSettings {

    public enum FilterSortBy {
        MAX_MATCH,MIN_MATCH
    }

    public ArrayList<Category> categoriesInSearch;
    public FilterSortBy sortBy;
    public int maxMissing;

    public FilterSettings() {
        reset();
    }

    public void reset() {
        categoriesInSearch = new ArrayList<>();
        sortBy = FilterSortBy.MAX_MATCH;
        maxMissing = Integer.MAX_VALUE;
    }

    public FilterSettings clone() {
        FilterSettings fs = new FilterSettings();
        fs.sortBy = sortBy;
        fs.maxMissing = maxMissing;
        fs.categoriesInSearch = (ArrayList<Category>) categoriesInSearch.clone();
        return fs;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FilterSettings) {
            FilterSettings fs = (FilterSettings) o;

            if (maxMissing != fs.maxMissing) {
                return false;
            }

            if (!categoriesInSearch.equals(fs.categoriesInSearch)) {
                return false;
            }

            return sortBy == fs.sortBy;
        }

        return false;
    }
}