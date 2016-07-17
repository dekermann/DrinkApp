package softpatrol.drinkapp.model.event;

import java.util.List;

import softpatrol.drinkapp.model.dto.SearchResultSimple;

/**
 * Created by root on 6/18/16.
 */
public class EventRecipeSearchComplete {

    public final List<SearchResultSimple> results;

    public EventRecipeSearchComplete(List<SearchResultSimple> results) {
        this.results = results;
    }
}
