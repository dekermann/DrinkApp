package softpatrol.drinkapp.model.event;

/**
 * Created by root on 7/17/16.
 */
public class BottomBarEvent {

    public int id;
    public boolean changeFragment;

    public BottomBarEvent(int id,boolean changeFragment) {
        this.id = id;
        this.changeFragment = changeFragment;
    }
}
