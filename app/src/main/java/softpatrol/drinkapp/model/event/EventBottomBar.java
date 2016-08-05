package softpatrol.drinkapp.model.event;

import softpatrol.drinkapp.layout.components.BottomBarItem;

/**
 * Created by root on 7/17/16.
 */
public class EventBottomBar {

    public BottomBarItem item;
    public boolean changeFragment;

    public EventBottomBar(BottomBarItem item,boolean changeFragment) {
        this.item = item;
        this.changeFragment = changeFragment;
    }
}
