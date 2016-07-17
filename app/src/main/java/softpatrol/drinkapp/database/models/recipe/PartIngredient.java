package softpatrol.drinkapp.database.models.recipe;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * David was here on 2016-06-08!
 * Copy this code for instant regret...
 */
public class PartIngredient {

    public static final String INGREDIENT = "ingredient";
    public static final String INGREDIENT_SERVER_ID = "id";
    public static final String QUANTITY_AMOUNT = "quantity";
    public static final String UNIT = "unit";
    public static final String UNIT_NAME = "name";

    private long ingredientServerId;
    private int quantity;
    private String unitName;

    public PartIngredient(JSONObject jsonObject) {
        try {
            ingredientServerId = jsonObject.getJSONObject(INGREDIENT).getLong(INGREDIENT_SERVER_ID);
            quantity = jsonObject.getInt(QUANTITY_AMOUNT);
            unitName = jsonObject.getJSONObject(UNIT).getString(UNIT_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public PartIngredient(String dataString) {
        String[] data = dataString.split("&&");
        ingredientServerId = Long.parseLong(data[0]);
        quantity = Integer.parseInt(data[1]);
        unitName = data[2];
    }

    public long getIngredientId() {
        return ingredientServerId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getUnitName() {
        return unitName;
    }

    @Override
    public String toString() {
        return ingredientServerId + "&&" + quantity + "&&" + unitName;
    }


}
