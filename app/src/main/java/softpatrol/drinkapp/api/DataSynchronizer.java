package softpatrol.drinkapp.api;

import android.app.Activity;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import softpatrol.drinkapp.activities.BaseActivity;
import softpatrol.drinkapp.database.DatabaseHandler;
import softpatrol.drinkapp.database.models.recipe.Recipe;
import softpatrol.drinkapp.database.models.ingredient.Ingredient;
import softpatrol.drinkapp.database.models.stash.Stash;
import softpatrol.drinkapp.util.Debug;

/**
 * MonsterMaskinen was here on 2016-06-10!
 * Copy this code for instant regret...
 */
public class DataSynchronizer {
    BaseActivity parent;

    public DataSynchronizer(BaseActivity parent) {
        this.parent = parent;
    }

    public void sync() {
        if(parent == null) throw new NullPointerException("Set parent activity before using sync");
        ArrayList<Pair<String, String>> headers = new ArrayList<>();
        headers.add(new Pair<>("Content-Type", "application/json"));
        DatabaseHandler db = DatabaseHandler.getInstance(parent);
        headers.add(new Pair<>("Authorization", db.getAccount(DatabaseHandler.getCurrentAccount(parent)).getToken()));
        new Getter(new SynchronizeStash(parent), null, headers).execute(Definitions.GET_STASH);
        new Getter(new SynchronizeIngredients(parent)).execute(Definitions.GET_INGREDIENTS);
        new Getter(new SynchronizeRecipes(parent)).execute(Definitions.GET_RECIPES);
    }

    public void syncStash() {
        if(parent == null) throw new NullPointerException("Set parent activity before using sync");
        ArrayList<Pair<String, String>> headers = new ArrayList<>();
        headers.add(new Pair<>("Content-Type", "application/json"));
        DatabaseHandler db = DatabaseHandler.getInstance(parent);
        headers.add(new Pair<>("Authorization", db.getAccount(DatabaseHandler.getCurrentAccount(parent)).getToken()));
        new Getter(new SynchronizeStash(parent), null, headers).execute(Definitions.GET_STASH);
    }

    private void syncIngredients() {
        new Getter(new SynchronizeIngredients(parent)).execute(Definitions.GET_INGREDIENTS);
    }

    private void syncRecipes() {
        new Getter(new SynchronizeRecipes(parent)).execute(Definitions.GET_RECIPES);
    }

    private class SynchronizeStash extends Analyzer {
        public SynchronizeStash(Activity activity) { super(activity); }
        @Override
        public void analyzeData(JSONObject result) throws JSONException {
            Debug.debugMessage((BaseActivity) caller, "attempting to sync stashes");
            JSONArray stashes = result.getJSONArray("data");

            DatabaseHandler db = DatabaseHandler.getInstance(caller);
            ArrayList<Stash> serverStashes = new ArrayList<>();
            //Parse stashes
            for(int i = 0;i<stashes.length();i++)
                serverStashes.add(new Stash(stashes.getJSONObject(i)));

            for(Stash s : serverStashes) {
                Stash s2 = db.getServerStash(s.getServerId());
                if(s2!= null) {
                    s.setId(s2.getId());
                    db.updateStash(s);
                }
                else db.addStash(s);
            }

            ArrayList<Stash> stashes1 = new ArrayList<>(db.getAllStashes());
            for(Stash i : stashes1) Debug.debugMessage((BaseActivity) caller, i.toString());
        }
    }

    private class SynchronizeIngredients extends Analyzer {
        public SynchronizeIngredients(Activity activity) { super(activity); }
        @Override
        public void analyzeData(JSONObject result) throws JSONException {
            Debug.debugMessage((BaseActivity) caller, "attempting to sync ingredients");
            JSONArray ingredients = result.getJSONArray("data");

            DatabaseHandler db = DatabaseHandler.getInstance(caller);
            //Parse Ingredients
            for(int i = 0;i<ingredients.length();i++)
                db.addIngredient(new Ingredient(ingredients.getJSONObject(i)));

            ArrayList<Ingredient> ingredients1 = new ArrayList<>(db.getAllIngredients());
            for(Ingredient i : ingredients1) Debug.debugMessage((BaseActivity) caller, i.toString());
        }
    }

    private class SynchronizeRecipes extends Analyzer {
        public SynchronizeRecipes(Activity activity) { super(activity); }
        @Override
        public void analyzeData(JSONObject result) throws JSONException {
            Debug.debugMessage((BaseActivity) caller, "attempting to sync recipes");
            JSONArray recipes = result.getJSONArray("data");

            DatabaseHandler db = DatabaseHandler.getInstance(caller);
            //Parse recipes
            for(int i = 0;i<recipes.length();i++)
                db.addRecipe(new Recipe(recipes.getJSONObject(i)));

            ArrayList<Recipe> recipes1 = new ArrayList<>(db.getAllRecipes());
            for(Recipe r : recipes1) Debug.debugMessage((BaseActivity) caller, r.toString());
        }
    }
}
