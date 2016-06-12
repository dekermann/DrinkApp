package softpatrol.drinkapp.database.models.recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * David was here on 2016-06-08!
 * Copy this code for instant regret...
 */
public class Recipe {

    public static final String SERVER_ID = "id";
    public static final String NAME = "name";
    public static final String BODY = "body";
    public static final String PICTURE_ID = "pictureId";
    public static final String PARTS_INGREDIENTS = "partsIngreds";
    //public static final String PARTS_INGREDIENTS = "partIngredients";
    public static final String PARTS_CATEGORIES = "partsCats";
    //public static final String PARTS_CATEGORIES = "partCategories";
    public static final String CREATED_AT = "createdAt";
    public static final String LATEST_MODIFICATION = "latestModified";

    private long id;
    private long serverId;
    private String name;
    private String body;
    private String pictureId;
    private ArrayList<PartIngredient> partIngredients = new ArrayList<>();
    private ArrayList<PartCategory> partCategories = new ArrayList<>();
    private long createdAt;
    private long latestModification;

    public Recipe(JSONObject jsonObject) {
        try {
            this.serverId = jsonObject.getLong(SERVER_ID);
            this.name = jsonObject.getString(NAME);
            this.body = jsonObject.getString(BODY);
            this.pictureId = "picture X";
            //this.pictureId = jsonObject.getString(PICTURE_ID);
            JSONArray partIngredients = jsonObject.getJSONArray(PARTS_INGREDIENTS);
            for(int i = 0;i<partIngredients.length();i++) {
                PartIngredient p = new PartIngredient(partIngredients.getJSONObject(i));
                this.partIngredients.add(p);
            }
            JSONArray parts = jsonObject.getJSONArray(PARTS_CATEGORIES);
            for(int i = 0;i<parts.length();i++) {
                PartCategory p = new PartCategory(parts.getJSONObject(i));
                this.partCategories.add(p);
            }
            this.createdAt = jsonObject.getLong(CREATED_AT);
            this.latestModification = jsonObject.getLong(LATEST_MODIFICATION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Recipe() {}

    @Override
    public String toString() {
        String ret = "---Recipe " + id + "---\n";
        ret += "Server ID: " + serverId + '\n';
        ret += "Name: " + name + '\n';
        ret += "Description: " + body + '\n';
        ret += "Picture ID: " + pictureId + '\n';
        for(PartCategory p : partCategories) ret += "\t" + p.toString() + '\n';
        for(PartIngredient p : partIngredients) ret += "\t" + p.toString() + '\n';
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPictureId() {
        return pictureId;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }

    public ArrayList<PartIngredient> getPartIngredients() {
        return partIngredients;
    }

    public void setPartIngredients(ArrayList<PartIngredient> partIngredients) {
        this.partIngredients = partIngredients;
    }

    public ArrayList<PartCategory> getPartCategories() {
        return partCategories;
    }

    public void setPartCategories(ArrayList<PartCategory> partCategories) {
        this.partCategories = partCategories;
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
