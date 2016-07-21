package softpatrol.drinkapp.model.dto;

import softpatrol.drinkapp.database.models.recipe.PartCategory;
import softpatrol.drinkapp.database.models.recipe.PartIngredient;

/**
 * Created by root on 7/18/16.
 */
public class PartWrapper {

    public enum ItemStatus {
        MISSING,HAVE_IT,ADDED_TO_CART
    }

    public enum PartType {
        INGREDIENT,CATEGORY
    }

    public String name;
    public String unit;
    public float quantity;
    public PartType type;
    public ItemStatus status;

    public PartWrapper(String name,String unit,float quantity,ItemStatus status,PartType type) {
        this.name = name;
        this.unit = unit;
        this.quantity = quantity;
        this.status = status;
        this.type = type;
    }

    public static PartWrapper create(PartIngredient pi,ItemStatus status) {
        return new PartWrapper(pi.getIngredientId() + "",pi.getUnitName(),pi.getQuantity(),status, PartType.INGREDIENT);
    }

    public static PartWrapper create(PartCategory pc,ItemStatus status) {
        return new PartWrapper(pc.getCategoryName(),pc.getUnitName(),pc.getQuantity(),status,PartType.CATEGORY);
    }
}
