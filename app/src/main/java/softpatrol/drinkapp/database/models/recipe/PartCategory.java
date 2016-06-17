package softpatrol.drinkapp.database.models.recipe;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * David was here on 2016-06-08!
 * Copy this code for instant regret...
 */
public class PartCategory {

    public static final String CATEGORY = "category";
    public static final String CATEGORY_NAME = "name";
    public static final String QUANTITY_AMOUNT = "quantity";
    public static final String UNIT = "unit";
    public static final String UNIT_NAME = "name";

    private String categoryName;
    private int quantity;
    private String unitName;

    public PartCategory(JSONObject jsonObject) {
        try {
            categoryName = jsonObject.getJSONObject(CATEGORY).getString(CATEGORY_NAME);
            quantity = jsonObject.getInt(QUANTITY_AMOUNT);
            unitName = jsonObject.getJSONObject(UNIT).getString(UNIT_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public PartCategory(String dataString) {
        String[] data = dataString.split("&&");

        categoryName = data[0];
        quantity = Integer.parseInt(data[1]);
        unitName = data[2];
    }

    @Override
    public String toString() {
        return categoryName + "&&" + quantity + "&&" + unitName;
    }
}
