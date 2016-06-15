package softpatrol.drinkapp.database.models.stash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * David was here on 2016-06-08!
 * Copy this code for instant regret...
 */
public class Stash {

    public static final String SERVER_ID = "id";
    public static final String NAME = "name";
    public static final String PICTURE_ID = "pictureId";
    public static final String OWNER_ID = "ownerId";
    public static final String INGREDIENTS = "ingredients";
    public static final String CREATED_AT = "createdAt";
    public static final String ACCESS_STATE = "accessState";
    public static final String LATEST_MODIFICATION = "latestModified";
    public static final String INGREDIENT_SERVER_ID = "id";

    private long id;
    private long serverId;
    private String name;
    private String pictureId;
    private String ownerId;
    private ArrayList<Long> ingredientIds = new ArrayList<>();
    private ArrayList<Long> recipeIds = new ArrayList<>();
    private String accessState;
    private long createdAt;
    private long latestModification;

    public Stash(JSONObject jsonObject) {
        try {
            this.serverId = jsonObject.getLong(SERVER_ID);
            this.name = jsonObject.getString(NAME);
            this.pictureId = "picture X";
            //this.pictureId = jsonObject.getString(PICTURE_ID);
            this.ownerId = "owner X";
            //this.ownerId = jsonObject.getString(OWNER_ID);
            JSONArray ingredients = jsonObject.getJSONArray(INGREDIENTS);
            for(int i = 0;i<ingredients.length();i++) {
                ingredientIds.add(ingredients.getJSONObject(i).getLong(INGREDIENT_SERVER_ID));
            }
            this.recipeIds.add(1L);
            this.accessState = "access X";
            //this.accessState = jsonObject.getString(ACCESS_STATE);
            this.createdAt = jsonObject.getLong(CREATED_AT);
            this.latestModification = jsonObject.getLong(LATEST_MODIFICATION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Stash() {}

    @Override
    public String toString() {
        String ret = "---Stash " + id + "---\n";
        ret += "Server ID: " + serverId + '\n';
        ret += "Name: " + name + '\n';
        ret += "Picture ID: " + pictureId + '\n';
        ret += "Owner ID: " + ownerId + '\n';
        for(Long l : ingredientIds) ret += "\t" + l.toString() + '\n';
        for(Long l : recipeIds) ret += "\t" + l.toString() + '\n';
        ret += "Access State: " + accessState + '\n';
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

    public String getPictureId() {
        return pictureId;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public ArrayList<Long> getIngredientsIds() {
        return ingredientIds;
    }

    public void setIngredientsIds(ArrayList<Long> ingredientIds) {
        this.ingredientIds = ingredientIds;
    }

    public void addIngredientId(Long ingredient) {
        for(Long l : this.ingredientIds) if(l.equals(ingredient)) return;
        this.ingredientIds.add(ingredient);
    }

    public ArrayList<Long> getResultingDrinks() {
        return recipeIds;
    }

    public void setResultingDrinks(ArrayList<Long> recipeIds) {
        this.recipeIds = recipeIds;
    }

    public void addRecipeId(Long recipeId) {
        for(Long l : this.recipeIds) if(l.equals(recipeId)) return;
        this.recipeIds.add(recipeId);
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public String getAccessState() {
        return accessState;
    }

    public void setAccessState(String accessState) {
        this.accessState = accessState;
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
