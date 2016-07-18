package softpatrol.drinkapp.model.dto;

import softpatrol.drinkapp.database.models.recipe.PartCategory;
import softpatrol.drinkapp.database.models.recipe.PartIngredient;

/**
 * Created by root on 7/18/16.
 */
public class PartWrapper {


    public final static int MISSING = 1;
    public final static int HAVE_IT = 2;
    public final static int ADDED_TO_CART = 3;

    public final static int INGREDIENT = 0;
    public final static int CATEGORY = 1;

    public String name;
    public String unit;
    public float quantity;
    public int type;
    public int status;

    public PartWrapper(String name,String unit,float quantity,int status,int type) {
        this.name = name;
        this.unit = unit;
        this.quantity = quantity;
        this.status = status;
        this.type = type;
    }

    public static PartWrapper create(PartIngredient pi,int status) {
        return new PartWrapper(pi.getIngredientId() + "",pi.getUnitName(),pi.getQuantity(),status,PartWrapper.INGREDIENT);
    }

    public static PartWrapper create(PartCategory pc,int status) {
        return new PartWrapper(pc.getCategoryName(),pc.getUnitName(),pc.getQuantity(),status,PartWrapper.CATEGORY);
    }
}
