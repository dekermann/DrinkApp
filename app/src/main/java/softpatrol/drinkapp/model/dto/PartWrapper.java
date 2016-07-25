package softpatrol.drinkapp.model.dto;

import android.content.Context;

import softpatrol.drinkapp.database.DatabaseHandler;
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

    public Long id;
    public String name;
    public String unit;
    public float quantity;
    public PartType type;
    public ItemStatus status;

    public PartWrapper(String name,String unit,float quantity,Long id,ItemStatus status,PartType type) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.quantity = quantity;
        this.status = status;
        this.type = type;
    }

    public static PartWrapper create(PartIngredient pi,ItemStatus status,Context ctx) {
        DatabaseHandler db = DatabaseHandler.getInstance(ctx);
        return new PartWrapper(db.getIngredient(pi.getIngredientId()).getName(),pi.getUnitName(),pi.getQuantity(),pi.getIngredientId(),status, PartType.INGREDIENT);
    }

    public static PartWrapper create(PartCategory pc,ItemStatus status,Context ctx) {
        DatabaseHandler db = DatabaseHandler.getInstance(ctx);
        return new PartWrapper(pc.getCategoryName(),pc.getUnitName(),pc.getQuantity(),pc.getCategoryServerId(),status,PartType.CATEGORY);
    }
}
