package softpatrol.drinkapp.util;

import android.util.Log;

import softpatrol.drinkapp.activities.BaseActivity;

/**
 * David was here on 2016-06-08!
 * Copy this code for instant regret...
 */
public class Debug {
    private static final boolean debugEnabled = true;

    public static void debugMessage(BaseActivity activity, String message){
        if(debugEnabled) Log.d("DBG", activity.ACTIVITY_NAME + "Activity>>> " + message);
    }
}
