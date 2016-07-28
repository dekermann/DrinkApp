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
        MATCH_PERCENT
    }

    public List<Category> categoriesInSearch;
    public FilterSortBy sortBy;
    public int maxMissing;

    public FilterSettings() {
        categoriesInSearch = new ArrayList<>();
        sortBy = FilterSortBy.MATCH_PERCENT;
        maxMissing = Integer.MAX_VALUE;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof LayoutInflater.Filter) {
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