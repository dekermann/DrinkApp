package softpatrol.drinkapp.api;

import android.app.Activity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import softpatrol.drinkapp.activities.BaseActivity;
import softpatrol.drinkapp.database.DatabaseHandler;
import softpatrol.drinkapp.database.models.recipe.Recipe;
import softpatrol.drinkapp.database.models.ingredient.Ingredient;
import softpatrol.drinkapp.util.Debug;

/**
 * MonsterMachine was here on 2016-06-10!
 * Copy this code for instant regret...
 */
public class DataSynchronizer {
    BaseActivity parent;

    public DataSynchronizer(BaseActivity parent) {
        this.parent = parent;
    }

    public void syncIngredients() {
        new Getter(new SynchronizeIngredients(parent)).execute(Definitions.GET_INGREDIENTS);
    }

    public void syncRecipes() {
        new Getter(new SynchronizeRecipes(parent)).execute(Definitions.GET_RECIPES);
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
            ArrayList<Recipe> serverStashes = new ArrayList<>();
            //Parse stashes
            for(int i = 0;i<recipes.length();i++) serverStashes.add(new Recipe(recipes.getJSONObject(i)));
            for(Recipe r : serverStashes) {
                Recipe r2 = db.getServerRecipe(r.getServerId());
                if(r2!= null) {
                    r.setId(r2.getId());
                    db.updateRecipe(r);
                }
                else db.addRecipe(r);
            }

            ArrayList<Recipe> recipes1 = new ArrayList<>(db.getAllRecipes());
            for(Recipe r : recipes1) Debug.debugMessage((BaseActivity) caller, r.toString());
        }
    }
}
