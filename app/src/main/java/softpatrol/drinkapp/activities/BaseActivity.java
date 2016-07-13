package softpatrol.drinkapp.activities;


import android.app.NotificationManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

/**
 * David was here on 2016-06-08!
 * Copy this code for instant regret...
 */
public class BaseActivity extends AppCompatActivity {
    private static boolean active = false;
    public static String ACTIVITY_NAME = "Unknown Activity";

    @Override
    protected void onResume() {
        super.onResume();
        active = true;
        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancelAll();
    }
    @Override
    protected void onPause() {
        super.onPause();
        active = false;
    }
    public static boolean isActive() { return active; }
}
