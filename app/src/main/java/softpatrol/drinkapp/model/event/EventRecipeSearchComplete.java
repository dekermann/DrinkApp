package softpatrol.drinkapp.model.event;

import java.util.List;

import softpatrol.drinkapp.model.dto.SearchResult;
import softpatrol.drinkapp.model.dto.SearchResult2;

/**
 * Created by root on 6/18/16.
 */
public class EventRecipeSearchComplete {

    public final List<SearchResult2> results;

    public EventRecipeSearchComplete(List<SearchResult2> results) {
        this.results = results;
    }
}
