package softpatrol.drinkapp.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import softpatrol.drinkapp.activities.RootActivity;
import softpatrol.drinkapp.database.models.account.Account;
import softpatrol.drinkapp.database.models.ingredient.Ingredient;
import softpatrol.drinkapp.database.models.recipe.Recipe;
import softpatrol.drinkapp.database.models.stash.Stash;
import softpatrol.drinkapp.database.tables.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * David was here on 2016-06-08!
 * Copy this code for instant regret...
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static DatabaseHandler databaseHandler;
    private static Context context;

    // All Static variables
    // Current Account Defualt String
    public static final String CURRENT_ACCOUNT_ID = "caid";
    public static final String FRAGMENT_EVENTS = "fragmentEvents";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "drinkApp";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        databaseHandler = this;
    }

    public static DatabaseHandler getInstance(Context context) {
        DatabaseHandler.context = context;
        if(databaseHandler == null) databaseHandler = new DatabaseHandler(context);
        return databaseHandler;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        AccountTable.onCreate(db);
        StashTable.onCreate(db);
        IngredientTable.onCreate(db);
        RecipeTable.onCreate(db);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        AccountTable.onUpgrade(db);
        StashTable.onUpgrade(db);
        IngredientTable.onUpgrade(db);
        RecipeTable.onUpgrade(db);
        // Create tables again
        onCreate(db);
        setCurrentAccount(-1,context);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    /*    ACCOUNTS     */
    public void addAccount(Account account) {
        AccountTable.createAccount(this.getWritableDatabase(), account);
    }
    public Account getAccount(long id) {
        return AccountTable.getAccount(this.getReadableDatabase(), id);
    }
    public Account getAccount(String email) {
        return AccountTable.getAccount(this.getReadableDatabase(), email);
    }
    public List<Account> getAllAccounts() {
        return AccountTable.getAllAccounts(this.getWritableDatabase());
    }
    public int updateAccount(Account account) {
        return AccountTable.updateAccount(this.getWritableDatabase(), account);
    }
    public void deleteAccount(Account account) {

    }
    /*    STASHES     */
    public void addStash(Stash stash) {
        StashTable.createStash(this.getWritableDatabase(), stash);
    }
    public Stash getStash(long id) {
        return StashTable.getStash(this.getReadableDatabase(), id);
    }
    public Stash getStash(String name) {
        return StashTable.getStash(this.getReadableDatabase(), name);
    }
    public Stash getServerStash(long serverId) {
        return StashTable.getServerStash(this.getReadableDatabase(), serverId);
    }
    public int deleteStash(Stash stash) {
        return StashTable.deleteStash(this.getWritableDatabase(), stash.getId());
    }
    public ArrayList<Stash> getAllStashes() {
        return StashTable.getAllStashes(this.getWritableDatabase());
    }
    public int updateStash(Stash stash) {
        return StashTable.updateStash(this.getWritableDatabase(), stash);
    }
    /*    INGREDIENTS     */
    public void addIngredient(Ingredient ingredient) {
        IngredientTable.createIngredient(this.getWritableDatabase(), ingredient);
    }
    public Ingredient getIngredient(long id) {
        return IngredientTable.getIngredient(this.getReadableDatabase(), id);
    }

    public Ingredient getServerIngredient(long id) {
        return IngredientTable.getServerIngredient(this.getReadableDatabase(), id);
    }
    public Ingredient getIngredient(String name) {
        return IngredientTable.getIngredient(this.getReadableDatabase(), name);
    }
    public List<Ingredient> getAllIngredients() {
        return IngredientTable.getAllIngredients(this.getWritableDatabase());
    }
    public int updateIngredient(Ingredient ingredient) {
        return IngredientTable.updateIngredient(this.getWritableDatabase(), ingredient);
    }
    public void deleteIngredient(Ingredient ingredient) {
        //TODO: Delete ingredient
    }
    /*    RECIPES     */
    public void addRecipe(Recipe recipe) {
        RecipeTable.createRecipe(this.getWritableDatabase(), recipe);
    }
    public Recipe getRecipe(long id) {
        return RecipeTable.getRecipe(this.getReadableDatabase(), id);
    }
    public Recipe getServerRecipe(long id) {
        return RecipeTable.getServerRecipe(this.getReadableDatabase(), id);
    }
    public Recipe getRecipe(String name) {
        return RecipeTable.getRecipe(this.getReadableDatabase(), name);
    }
    public List<Recipe> getAllRecipes() {
        return RecipeTable.getAllRecipes(this.getWritableDatabase());
    }
    public int updateRecipe(Recipe recipe) {
        return RecipeTable.updateRecipe(this.getWritableDatabase(), recipe);
    }
    public void deleteRecipe(Recipe recipe) {
        //TODO: Delete recipe
    }

    /**
     * Other Database related methods
     */
    public static void setCurrentAccount(long ID, Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(CURRENT_ACCOUNT_ID, ID);
        editor.commit();
    }
    public static long getCurrentAccount(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getLong(CURRENT_ACCOUNT_ID, -1);
    }
    public void setFragmentEvents(Context context, String type, Set<String> events) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putStringSet(FRAGMENT_EVENTS + getCurrentAccount(context) + type, events).commit();
    }
    public Set<String> getFragmentEvents(Context context, String type) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> events = sharedPref.getStringSet(FRAGMENT_EVENTS+getCurrentAccount(context)+type, null);
        if(events == null) events = new HashSet<>();
        return events;
    }
    public File getAccDir(Context context, String email) {
        return context.getDir(RootActivity.ACCOUNT_DIR + email, 0);
    }
}