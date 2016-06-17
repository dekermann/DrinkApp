package softpatrol.drinkapp.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import softpatrol.drinkapp.database.models.stash.Stash;
import softpatrol.drinkapp.util.Utils;

/**
 * Divine was here on 2016-06-09!
 * Copy this code for instant regret...
 */
public class StashTable {

    private static final String TABLE_STASHES = "stashes";

    private static final String STASH_ID = "id";
    private static final String STASH_SERVER_ID = "serverId";
    private static final String STASH_PICTURE_ID = "pictureId";
    private static final String STASH_NAME = "name";
    private static final String STASH_OWNER_ID = "ownerId";
    private static final String STASH_INGREDIENTS_IDS = "ingredientsId";
    private static final String STASH_RECIPE_IDS = "resultDrinksId";
    private static final String STASH_ACCESS_STATE = "accessState";
    private static final String INGREDIENT_CREATED_AT = "createdAt";
    private static final String INGREDIENT_LATEST_MODIFICATION = "latestModified";

    private static final String DATABASE_CREATE = "create table " + TABLE_STASHES + "(" +
            STASH_ID + " integer primary key autoincrement, " +
            STASH_SERVER_ID + " integer not null, " +
            STASH_PICTURE_ID + " text not null, " +
            STASH_NAME + " text not null, " +
            STASH_OWNER_ID + " text not null, " +
            STASH_INGREDIENTS_IDS + " text not null, " +
            STASH_RECIPE_IDS + " text not null, " +
            STASH_ACCESS_STATE + " text not null, " +
            INGREDIENT_CREATED_AT + " integer not null, " +
            INGREDIENT_LATEST_MODIFICATION + " integer not null);";

    private static String[] allColumns = {STASH_ID,
            STASH_SERVER_ID, STASH_PICTURE_ID, STASH_NAME,
            STASH_OWNER_ID, STASH_INGREDIENTS_IDS,
            STASH_RECIPE_IDS, STASH_ACCESS_STATE,
            INGREDIENT_CREATED_AT, INGREDIENT_LATEST_MODIFICATION};

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STASHES);
    }

    public static void createStash(SQLiteDatabase db, Stash stash) {
        ContentValues values = new ContentValues();

        values.put(STASH_SERVER_ID, stash.getServerId());
        values.put(STASH_PICTURE_ID, stash.getServerId());
        values.put(STASH_NAME, stash.getName());
        values.put(STASH_OWNER_ID, stash.getOwnerId());
        values.put(STASH_INGREDIENTS_IDS, Utils.concatenate(stash.getIngredientsIds()));
        values.put(STASH_RECIPE_IDS, Utils.concatenate(stash.getResultingDrinks()));
        values.put(STASH_ACCESS_STATE, stash.getAccessState());
        values.put(INGREDIENT_CREATED_AT, stash.getCreatedAt());
        values.put(INGREDIENT_LATEST_MODIFICATION, stash.getLatestModification());

        db.insert(TABLE_STASHES, null, values);
        db.close();
    }

    public static Stash getStash(SQLiteDatabase db, long id) {
        if (id == -1) return null;
        Cursor cursor = db.query(TABLE_STASHES, allColumns, STASH_ID + " = '" + id + "'", null, null, null, null);
        if (cursor.getCount() == 0) return null;

        cursor.moveToFirst();

        Stash stash = cursorToStash(cursor);

        cursor.close();
        return stash;
    }

    public static Stash getServerStash(SQLiteDatabase db, long id) {
        if (id == -1) return null;
        Cursor cursor = db.query(TABLE_STASHES, allColumns, STASH_SERVER_ID + " = '" + id + "'", null, null, null, null);
        if (cursor.getCount() == 0) return null;

        cursor.moveToFirst();

        Stash stash = cursorToStash(cursor);

        cursor.close();
        return stash;
    }

    public static Stash getStash(SQLiteDatabase db, String name) {
        Cursor cursor = db.query(TABLE_STASHES, allColumns, STASH_NAME + " = '" + name + "'", null, null, null, null);
        if (cursor.getCount() == 0) return null;

        cursor.moveToFirst();

        Stash stash = cursorToStash(cursor);

        cursor.close();
        return stash;
    }

    public static ArrayList<Stash> getAllStashes(SQLiteDatabase db) {
        ArrayList<Stash> stashes = new ArrayList<>();

        Cursor cursor = db.query(TABLE_STASHES, allColumns, null, null, null, null, null);
        if (cursor.getCount() == 0) return null;

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Stash stash = cursorToStash(cursor);
            stashes.add(stash);
            cursor.moveToNext();
        }

        cursor.close();
        return stashes;
    }

    public static int updateStash(SQLiteDatabase db, Stash stash) {
        ContentValues values = new ContentValues();

        values.put(STASH_ID, stash.getId());
        values.put(STASH_SERVER_ID, stash.getServerId());
        values.put(STASH_PICTURE_ID, stash.getPictureId());
        values.put(STASH_NAME, stash.getName());
        values.put(STASH_OWNER_ID, stash.getOwnerId());
        values.put(STASH_INGREDIENTS_IDS, Utils.concatenate(stash.getIngredientsIds()));
        values.put(STASH_RECIPE_IDS, Utils.concatenate(stash.getResultingDrinks()));
        values.put(STASH_ACCESS_STATE, stash.getAccessState());
        values.put(INGREDIENT_CREATED_AT, stash.getCreatedAt());
        values.put(INGREDIENT_LATEST_MODIFICATION, stash.getLatestModification());

        int ret = db.update(TABLE_STASHES, values, STASH_ID + " = '" + stash.getId() + "'", null);
        db.close();
        return ret;
    }

    private static Stash cursorToStash(Cursor cursor) {
        Stash stash = new Stash();
        stash.setId(cursor.getLong(0));
        stash.setServerId(cursor.getLong(1));
        stash.setPictureId(cursor.getString(2));
        stash.setName(cursor.getString(3));
        stash.setOwnerId(cursor.getString(4));
        stash.setIngredientsIds(Utils.split(cursor.getString(5)));
        stash.setResultingDrinks(Utils.split(cursor.getString(6)));
        stash.setAccessState(cursor.getString(7));
        stash.setCreatedAt(cursor.getLong(8));
        stash.setLatestModification(cursor.getLong(9));
        return stash;
    }

}
