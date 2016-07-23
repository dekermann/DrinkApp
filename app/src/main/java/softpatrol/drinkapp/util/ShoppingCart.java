package softpatrol.drinkapp.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import softpatrol.drinkapp.database.models.ingredient.Ingredient;

/**
 * Created by rasmus on 7/23/16.
 */
public class ShoppingCart {

    private static ShoppingCart instance;
    private static Lock lock = new ReentrantLock();

    private List<Ingredient> ingredientList;

    private ShoppingCart() {
        ingredientList = new ArrayList<Ingredient>();
    }

    public static ShoppingCart getInstance() {
        lock.lock();

        if (instance == null) {
                instance = new ShoppingCart();
        }

        lock.unlock();
        return instance;
    }

    public boolean hasIngredient(Ingredient ingredient) {
        lock.lock();
        boolean returnVal = ingredientList.contains(ingredient);
        lock.unlock();
        return returnVal;
    }

    public void addIngredient(Ingredient ingredient) {
        lock.lock();
        ingredientList.add(ingredient);
        lock.unlock();
    }

    public Lock getInternalLock() {
        return lock;
    }

    public List<Ingredient> getIngredientList() {
        return ingredientList;
    }


}
