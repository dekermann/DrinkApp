package softpatrol.drinkapp.model.event;

/**
 * Created by rasmus on 8/5/16.
 */
public class EventScanFragment {

    public static final int FRAGMENT_ENTERED = 0;
    public static final int FRAGMENT_EXITED = 1;

    public int event;

    public EventScanFragment(int event) {
        this.event = event;
    }
}
