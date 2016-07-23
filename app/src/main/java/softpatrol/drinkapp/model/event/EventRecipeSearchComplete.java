package softpatrol.drinkapp.model.event;

import java.util.List;

import softpatrol.drinkapp.model.dto.SearchResult;

/**
 * Created by root on 6/18/16.
 */
public class EventRecipeSearchComplete {

    public final List<SearchResult> results;

    public EventRecipeSearchComplete(List<SearchResult> results) {
        this.results = results;
    }
}
