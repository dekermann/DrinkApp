package softpatrol.drinkapp.model.event;

import softpatrol.drinkapp.database.models.recipe.Recipe;

/**
 * Created by root on 7/24/16.
 */
public class EventRecipe {

    public Recipe recipe;

    public EventRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
}
