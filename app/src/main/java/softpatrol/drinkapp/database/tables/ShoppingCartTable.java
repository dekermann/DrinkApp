package softpatrol.drinkapp.database.tables;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by rasmus on 7/23/16.
 */
public class ShoppingCartTable {

    private static final String TABLE_SHOPPING_CART = "shopping_cart";

    private static final String INGREDIENT_NAME = "ingredient_name";
    private static final String CREATED_AT = "created_at";

    private static String[] allColumns = {INGREDIENT_NAME,CREATED_AT};

    private static final String DATABASE_CREATE = "create table " + TABLE_SHOPPING_CART + "(" +
            INGREDIENT_NAME + " integer primary key, " +
            CREATED_AT + " long not null);";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOPPING_CART);
    }

}
