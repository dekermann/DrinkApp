package softpatrol.drinkapp.database.models.ingredient;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * David was here on 2016-06-08!
 * Copy this code for instant regret...
 */
public class MetaData {

    public static final String VENDOR = "vendor";

    private String vendor;

    public MetaData(JSONObject jsonObject) {
        try {
            vendor = jsonObject.getString(VENDOR);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public MetaData(String dataString) {
        String[] data = dataString.split("&&");
        vendor = data[0];
    }

    public String getVendor() {
        return vendor;
    }

    @Override
    public String toString() {
        return vendor + "";
    }
}
