package softpatrol.drinkapp.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import softpatrol.drinkapp.database.models.recipe.PartCategory;
import softpatrol.drinkapp.database.models.recipe.PartIngredient;
import softpatrol.drinkapp.database.models.recipe.Recipe;

/**
 * Divine was here on 2016-06-09!
 * Copy this code for instant regret...
 */
public class RecipeTable {

    private static final String TABLE_RECIPES = "recipes";

    private static final String RECIPE_ID = "id";
    private static final String RECIPE_SERVER_ID = "serverId";
    private static final String RECIPE_NAME = "name";
    private static final String RECIPE_DESCRIPTION = "description";
    private static final String RECIPE_PICTURE_ID = "pictureId";
    private static final String RECIPE_PART_INGREDIENTS = "ingredients";
    private static final String RECIPE_PART_CATEGORIES = "categories";
    private static final String INGREDIENT_CREATED_AT = "createdAt";
    private static final String INGREDIENT_LATEST_MODIFICATION = "latestModified";

    private static final String DATABASE_CREATE = "create table " + TABLE_RECIPES + "(" +
            RECIPE_ID + " integer primary key autoincrement, " +
            RECIPE_SERVER_ID + " integer not null, " +
            RECIPE_NAME + " text not null, " +
            RECIPE_DESCRIPTION + " text not null, " +
            RECIPE_PICTURE_ID + " text not null, " +
            RECIPE_PART_INGREDIENTS + " text not null, " +
            RECIPE_PART_CATEGORIES + " text not null, " +
            INGREDIENT_CREATED_AT + " integer not null, " +
            INGREDIENT_LATEST_MODIFICATION + " integer not null);";

    private static String[] allColumns = {RECIPE_ID,
            RECIPE_SERVER_ID, RECIPE_NAME,
            RECIPE_DESCRIPTION, RECIPE_PICTURE_ID,
            RECIPE_PART_INGREDIENTS, RECIPE_PART_CATEGORIES,
            INGREDIENT_CREATED_AT, INGREDIENT_LATEST_MODIFICATION};

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
    }

    public static void createRecipe(SQLiteDatabase db, Recipe recipe) {
        ContentValues values = new ContentValues();

        values.put(RECIPE_SERVER_ID, recipe.getServerId());
        values.put(RECIPE_NAME, recipe.getName());
        values.put(RECIPE_DESCRIPTION, recipe.getBody());
        values.put(RECIPE_PICTURE_ID, recipe.getPictureId());
        values.put(RECIPE_PART_INGREDIENTS, encodePartIngredients(recipe.getPartIngredients()));
        values.put(RECIPE_PART_CATEGORIES, encodePartCategories(recipe.getPartCategories()));
        values.put(INGREDIENT_CREATED_AT, recipe.getCreatedAt());
        values.put(INGREDIENT_LATEST_MODIFICATION, recipe.getLatestModification());

        db.insert(TABLE_RECIPES, null, values);
        db.close();
    }

    public static Recipe getRecipe(SQLiteDatabase db, long id) {
        if (id == -1) return null;
        Cursor cursor = db.query(TABLE_RECIPES, allColumns, RECIPE_ID + " = '" + id + "'", null, null, null, null);
        if (cursor.getCount() == 0) return null;

        cursor.moveToFirst();

        Recipe recipe = cursorToRecipe(cursor);

        cursor.close();
        return recipe;
    }

    public static Recipe getRecipe(SQLiteDatabase db, String name) {
        Cursor cursor = db.query(TABLE_RECIPES, allColumns, RECIPE_NAME + " = '" + name + "'", null, null, null, null);
        if (cursor.getCount() == 0) return null;

        cursor.moveToFirst();

        Recipe recipe = cursorToRecipe(cursor);

        cursor.close();
        return recipe;
    }

    public static List<Recipe> getAllRecipes(SQLiteDatabase db) {
        List<Recipe> recipes = new ArrayList<>();

        Cursor cursor = db.query(TABLE_RECIPES, allColumns, null, null, null, null, null);
        if (cursor.getCount() == 0) return null;

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Recipe recipe = cursorToRecipe(cursor);
            recipes.add(recipe);
            cursor.moveToNext();
        }

        cursor.close();
        return recipes;
    }

    public static int updateRecipe(SQLiteDatabase db, Recipe recipe) {
        ContentValues values = new ContentValues();

        values.put(RECIPE_SERVER_ID, recipe.getServerId());
        values.put(RECIPE_NAME, recipe.getName());
        values.put(RECIPE_DESCRIPTION, recipe.getBody());
        values.put(RECIPE_PICTURE_ID, recipe.getPictureId());
        values.put(RECIPE_PART_INGREDIENTS, encodePartIngredients(recipe.getPartIngredients()));
        values.put(RECIPE_PART_CATEGORIES, encodePartCategories(recipe.getPartCategories()));
        values.put(INGREDIENT_CREATED_AT, recipe.getCreatedAt());
        values.put(INGREDIENT_LATEST_MODIFICATION, recipe.getLatestModification());

        int ret = db.update(TABLE_RECIPES, values, RECIPE_ID + " = '" + recipe.getId() + "'", null);
        db.close();
        return ret;
    }

    private static Recipe cursorToRecipe(Cursor cursor) {
        Recipe recipe = new Recipe();
        recipe.setId(cursor.getLong(0));
        recipe.setServerId(cursor.getLong(1));
        recipe.setName(cursor.getString(2));
        recipe.setBody(cursor.getString(3));
        recipe.setPictureId(cursor.getString(4));
        recipe.setPartIngredients(decodePartIngredients(cursor.getString(5)));
        recipe.setPartCategories(decodePartCategories(cursor.getString(6)));
        recipe.setCreatedAt(cursor.getLong(7));
        recipe.setLatestModification(cursor.getLong(8));
        return recipe;
    }

    private static String encodePartIngredients(ArrayList<PartIngredient> partIngredients) {
        String ret = "";
        for(PartIngredient p : partIngredients) ret += p.toString() + ",";
        return ret;
    }
    private static ArrayList<PartIngredient> decodePartIngredients(String string) {
        ArrayList<PartIngredient> partIngredients = new ArrayList<>();
        String[] data = string.split(",");
        for (String aData : data) {
            partIngredients.add(new PartIngredient(aData));
        }
        return partIngredients;
    }
    private static String encodePartCategories(ArrayList<PartCategory> partCategories) {
        String ret = "";
        for(PartCategory p : partCategories) ret += p.toString() + ",";
        return ret;
    }
    private static ArrayList<PartCategory> decodePartCategories(String string) {
        ArrayList<PartCategory> partCategories = new ArrayList<>();
        String[] data = string.split(",");
        for (String aData : data) {
            partCategories.add(new PartCategory(aData));
        }
        return partCategories;
    }

}
