package softpatrol.drinkapp.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import softpatrol.drinkapp.database.models.ingredient.Category;
import softpatrol.drinkapp.database.models.ingredient.Ingredient;
import softpatrol.drinkapp.database.models.ingredient.MetaData;

/**
 * Divine was here on 2016-06-09!
 * Copy this code for instant regret...
 */
public class IngredientTable {

    private static final String TABLE_INGREDIENTS = "ingredients";

    private static final String INGREDIENT_ID = "id";
    private static final String INGREDIENT_SERVER_ID = "serverId";
    private static final String INGREDIENT_NAME = "name";
    private static final String INGREDIENT_META_DATA = "metaData";
    private static final String INGREDIENT_CATEGORIES = "categories";
    private static final String INGREDIENT_CREATED_AT = "createdAt";
    private static final String INGREDIENT_LATEST_MODIFICATION = "latestModified";

    private static final String DATABASE_CREATE = "create table " + TABLE_INGREDIENTS + "(" +
            INGREDIENT_ID + " integer primary key autoincrement, " +
            INGREDIENT_SERVER_ID + " integer not null, " +
            INGREDIENT_NAME + " text not null, " +
            INGREDIENT_META_DATA + " text not null, " +
            INGREDIENT_CATEGORIES + " text not null, " +
            INGREDIENT_CREATED_AT + " integer not null, " +
            INGREDIENT_LATEST_MODIFICATION + " integer not null);";

    private static String[] allColumns = {INGREDIENT_ID,
            INGREDIENT_SERVER_ID, INGREDIENT_NAME,
            INGREDIENT_META_DATA, INGREDIENT_CATEGORIES,
            INGREDIENT_CREATED_AT, INGREDIENT_LATEST_MODIFICATION};

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTS);
    }

    public static void createIngredient(SQLiteDatabase db, Ingredient ingredient) {
        ContentValues values = new ContentValues();

        values.put(INGREDIENT_SERVER_ID, ingredient.getServerId());
        values.put(INGREDIENT_NAME, ingredient.getName());
        values.put(INGREDIENT_META_DATA, encodeMetaData(ingredient.getMetaData()));
        values.put(INGREDIENT_CATEGORIES, encodeCategories(ingredient.getCategories()));
        values.put(INGREDIENT_CREATED_AT, ingredient.getCreatedAt());
        values.put(INGREDIENT_LATEST_MODIFICATION, ingredient.getLatestModification());

        db.insert(TABLE_INGREDIENTS, null, values);
        db.close();
    }

    public static Ingredient getIngredient(SQLiteDatabase db, long id) {
        if (id == -1) return null;
        Cursor cursor = db.query(TABLE_INGREDIENTS, allColumns, INGREDIENT_ID + " = '" + id + "'", null, null, null, null);
        if (cursor.getCount() == 0) return null;

        cursor.moveToFirst();

        Ingredient ingredient = cursorToIngredient(cursor);

        cursor.close();
        return ingredient;
    }

    public static Ingredient getIngredient(SQLiteDatabase db, String name) {
        Cursor cursor = db.query(TABLE_INGREDIENTS, allColumns, INGREDIENT_NAME + " = '" + name + "'", null, null, null, null);
        if (cursor.getCount() == 0) return null;

        cursor.moveToFirst();

        Ingredient ingredient = cursorToIngredient(cursor);

        cursor.close();
        return ingredient;
    }

    public static List<Ingredient> getAllIngredients(SQLiteDatabase db) {
        List<Ingredient> ingredients = new ArrayList<>();

        Cursor cursor = db.query(TABLE_INGREDIENTS, allColumns, null, null, null, null, null);
        if (cursor.getCount() == 0) return null;

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Ingredient ingredient = cursorToIngredient(cursor);
            ingredients.add(ingredient);
            cursor.moveToNext();
        }

        cursor.close();
        return ingredients;
    }

    public static int updateIngredient(SQLiteDatabase db, Ingredient ingredient) {
        ContentValues values = new ContentValues();

        values.put(INGREDIENT_SERVER_ID, ingredient.getServerId());
        values.put(INGREDIENT_NAME, ingredient.getName());
        values.put(INGREDIENT_META_DATA, encodeMetaData(ingredient.getMetaData()));
        values.put(INGREDIENT_CATEGORIES, encodeCategories(ingredient.getCategories()));
        values.put(INGREDIENT_CREATED_AT, ingredient.getCreatedAt());
        values.put(INGREDIENT_LATEST_MODIFICATION, ingredient.getLatestModification());

        int ret = db.update(TABLE_INGREDIENTS, values, INGREDIENT_ID + " = '" + ingredient.getId() + "'", null);
        db.close();
        return ret;
    }

    private static Ingredient cursorToIngredient(Cursor cursor) {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(cursor.getLong(0));
        ingredient.setServerId(cursor.getLong(1));
        ingredient.setName(cursor.getString(2));
        ingredient.setMetaData(decodeMetaData(cursor.getString(3)));
        ingredient.setCategories(decodeCategories(cursor.getString(4)));
        ingredient.setCreatedAt(cursor.getLong(5));
        ingredient.setLatestModification(cursor.getLong(6));
        return ingredient;
    }

    private static String encodeMetaData(MetaData metaData) {
        return metaData.toString();
    }
    private static MetaData decodeMetaData(String string) {
        return new MetaData(string);
    }
    private static String encodeCategories(ArrayList<Category> categories) {
        String ret = "";
        for(Category c : categories) ret += c.toString() + ",";
        return ret;
    }
    private static ArrayList<Category> decodeCategories(String string) {
        ArrayList<Category> categories = new ArrayList<>();
        String[] data = string.split(",");
        for (String aData : data) {
            categories.add(new Category(aData));
        }
        return categories;
    }

}
