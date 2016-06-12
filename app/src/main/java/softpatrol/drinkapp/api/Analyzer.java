package softpatrol.drinkapp.api;

import android.content.Context;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class Analyzer {

    protected Context caller;

    public Analyzer(Context context) {
        caller = context;
    }

    public abstract void analyzeData(JSONObject result) throws Exception;

    public void analyzeError(JSONObject error) {
        try {
            String message = error.getString("message");
            Toast toast = Toast.makeText(caller, message, Toast.LENGTH_SHORT);
            toast.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}