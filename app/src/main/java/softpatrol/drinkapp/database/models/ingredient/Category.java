package softpatrol.drinkapp.database.models.ingredient;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * David was here on 2016-06-08!
 * Copy this code for instant regret...
 */
public class Category {

    public static final String SERVER_ID = "id";
    public static final String NAME = "name";
    public static final String CREATED_AT = "createdAt";
    public static final String LATEST_MODIFICATION = "latestModified";

    private long serverId;
    private String name;
    private long createdAt;
    private long latestModification;

    public Category(JSONObject jsonObject) {
        try {
            serverId = jsonObject.getLong(SERVER_ID);
            name = jsonObject.getString(NAME);
            createdAt = jsonObject.getLong(CREATED_AT);
            latestModification = jsonObject.getLong(LATEST_MODIFICATION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public Category(String dataString) {
        String[] data = dataString.split("&&");
        serverId = Long.parseLong(data[0]);
        name = data[1];
        createdAt = Long.parseLong(data[2]);
        latestModification = Long.parseLong(data[3]);
    }
    //Fake
    public Category() {
        serverId = 1;
        name = "Rum rum rum";
        createdAt = 123;
        latestModification = 1234;
    }

    public long getServerId() {
        return serverId;
    }

    public String getName() {
        return name;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getLatestModification() {
        return latestModification;
    }

    @Override
    public String toString() {
        return serverId + "&&" + name + "&&" + createdAt + "&&" + latestModification;
    }
}
