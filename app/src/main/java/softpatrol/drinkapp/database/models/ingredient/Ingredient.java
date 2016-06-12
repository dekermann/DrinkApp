package softpatrol.drinkapp.database.models.ingredient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * David was here on 2016-06-08!
 * Copy this code for instant regret...
 */
public class Ingredient {

    public static final String SERVER_ID = "id";
    public static final String NAME = "name";
    public static final String CATEGORIES = "categorySet";
    public static final String META_DATA = "metaData";
    public static final String CREATED_AT = "createdAt";
    public static final String LATEST_MODIFICATION = "latestModified";

    private long id;
    private long serverId;
    private String name;
    private MetaData metaData;
    private ArrayList<Category> categories = new ArrayList<>();
    private long createdAt;
    private long latestModification;

    public Ingredient(JSONObject jsonObject) {
        try {
            this.serverId = jsonObject.getLong(SERVER_ID);
            this.name = jsonObject.getString(NAME);
            metaData = new MetaData("vendor X");
            //metaData = new MetaData(jsonObject.getJSONObject(META_DATA));
            JSONArray categories = jsonObject.getJSONArray(CATEGORIES);
            for(int i = 0;i<categories.length();i++) {
                Category c = new Category(categories.getJSONObject(i));
                this.categories.add(c);
            }
            this.createdAt = jsonObject.getLong(CREATED_AT);
            this.latestModification = jsonObject.getLong(LATEST_MODIFICATION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Ingredient() {

    }

    @Override
    public String toString() {
        String ret = "---Ingredient " + id + "---\n";
        ret += "Server ID: " + serverId + '\n';
        ret += "Name: " + name + '\n';
        ret += "MetaData: " + metaData.toString() + '\n';
        for(Category c : categories) ret += "\t" + c.toString() + '\n';
        ret += "Created At: " + createdAt + '\n';
        ret += "Latest Modification: " + latestModification + '\n';

        return ret;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getLatestModification() {
        return latestModification;
    }

    public void setLatestModification(long latestModification) {
        this.latestModification = latestModification;
    }
}
