package softpatrol.drinkapp.model.event;

/**
 * Created by root on 7/17/16.
 */
public class EventBottomBar {

    public int id;
    public boolean changeFragment;

    public EventBottomBar(int id, boolean changeFragment) {
        this.id = id;
        this.changeFragment = changeFragment;
    }
}
