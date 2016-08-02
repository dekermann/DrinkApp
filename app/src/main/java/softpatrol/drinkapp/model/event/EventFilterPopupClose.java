package softpatrol.drinkapp.model.event;

import softpatrol.drinkapp.activities.fragments.result.FilterSettings;

/**
 * Created by root on 8/2/16.
 */
public class EventFilterPopupClose {

    public FilterSettings fs;

    public EventFilterPopupClose(FilterSettings fs) {
        this.fs = fs;
    }
}
