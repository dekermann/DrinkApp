package softpatrol.drinkapp.activities.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import softpatrol.drinkapp.R;
import softpatrol.drinkapp.api.Analyzer;
import softpatrol.drinkapp.api.Definitions;
import softpatrol.drinkapp.api.Getter;
import softpatrol.drinkapp.database.DatabaseHandler;
import softpatrol.drinkapp.database.models.ingredient.Category;
import softpatrol.drinkapp.database.models.ingredient.Ingredient;
import softpatrol.drinkapp.database.models.recipe.Recipe;
import softpatrol.drinkapp.database.models.stash.Stash;
import softpatrol.drinkapp.model.dto.ResultViewItem;
import softpatrol.drinkapp.model.dto.SearchResult;
import softpatrol.drinkapp.model.event.ChangeCurrentStashEvent;
import softpatrol.drinkapp.model.event.EditCurrentStashEvent;
import softpatrol.drinkapp.model.event.RecipeSearchComplete;

/**
 * David was here on 2016-06-08!
 * Copy this code for instant regret...
 */
public class ResultFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private RecyclerView mRecycleView;
    private ResultRecipeAdapter resultListAdapter;

    private boolean hasTestResults = false;

    public ResultFragment() {}

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ResultFragment newInstance(int sectionNumber) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int fragmentId = getArguments().getInt(ARG_SECTION_NUMBER);
        View rootView = inflater.inflate(R.layout.fragment_result, container, false);

        mRecycleView = (RecyclerView) rootView.findViewById(R.id.fragment_result_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.setItemAnimator(new DefaultItemAnimator());

        resultListAdapter = new ResultRecipeAdapter(new ArrayList<ResultViewItem>());
        mRecycleView.setAdapter(resultListAdapter);
        mRecycleView.setHasFixedSize(true);

        return rootView;
    }

    /*
    * On stash change, do a search on the server
     */
    private void stashChange(Stash stash) {
        if (stash.getIngredientsIds().size() > 0) {
            // clear old list
            resultListAdapter.clear();
            String strList = "";

            for (int i = 0; i < stash.getIngredientsIds().size(); i++) {
                strList += stash.getIngredientsIds().get(i);

                if (i < stash.getIngredientsIds().size() - 1) {
                    strList += ",";
                }
            }

            NameValuePair nvp = new BasicNameValuePair("ingredientIds", strList);
            new Getter(new ResultParser(this.getContext()), nvp).execute(Definitions.GET_SEARCH);
        }
    }

    /*
    * Analyzer
     */
    class ResultParser extends Analyzer {

        public ResultParser(Context context) {
            super(context);
        }

        @Override
        public void analyzeData(JSONObject result) throws Exception {
            JSONArray array = result.getJSONArray("data");

            ArrayList<SearchResult> results = new ArrayList<>();

            for (int i = 0; i < array.length();i++) {
                JSONObject obj = (JSONObject) array.get(i);
                int recipeId = obj.getInt("recipeId");

                SearchResult sr = new SearchResult();


                JSONArray ingredientMatches = obj.getJSONArray("ingredientMatches");

                for (int k = 0;k < ingredientMatches.length();k++) {
                    sr.getIngredientMatches().add((Integer) ingredientMatches.get(k));
                }


                JSONArray ingredientNoMatches = obj.getJSONArray("ingredientNoMatches");

                for (int k = 0;k < ingredientMatches.length();k++) {
                    sr.getIngredientNoMatches().add((Integer) ingredientMatches.get(k));
                }

                JSONArray categoryMatches = obj.getJSONArray("categoryMatches");

                for (int k = 0;k < categoryMatches.length();k++) {
                    sr.getCategoryMatches().add((Integer) ingredientMatches.get(k));
                }

                JSONArray categoryNoMatches = obj.getJSONArray("categoryNoMatches");

                for (int k = 0;k < categoryNoMatches.length();k++) {
                    sr.getCategoryNoMatches().add((Integer) ingredientMatches.get(k));
                }

                results.add(sr);
            }

            Collections.sort(results);
            EventBus.getDefault().post(new RecipeSearchComplete(results));
        }
    }

    /*
    * Adapter
     */
    class ResultRecipeAdapter extends RecyclerView.Adapter<ResultRecipeAdapter.MyViewHolder> {

        private List<ResultViewItem> dataSet;

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView text;
            public MyViewHolder(View itemView) {
                super(itemView);
                this.text = (TextView) itemView.findViewById(R.id.fragment_result_row_text);
            }
        }

        public ResultRecipeAdapter(List<ResultViewItem> data) {
            this.dataSet = data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_result_row, parent, false);

            //view.setOnClickListener(MainActivity.myOnClickListener);

            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

            TextView text = holder.text;

            text.setText(dataSet.get(listPosition).getResult().getRecipieId());
        }

        public void addRecipe(ResultViewItem recipe) {
            dataSet.add(recipe);
            notifyItemInserted(dataSet.size()-1);
        }

        public void clear() {
            dataSet.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }
    }

    /*
    * Messaging service between stuff
     */

    @Subscribe
    public void onCurrentStashEvent(ChangeCurrentStashEvent stashEvent) {
        stashChange(StashFragment.CURRENT_STASH);
    }

    @Subscribe
    public void onEditStashEvent(EditCurrentStashEvent stashEvent) {
        stashChange(StashFragment.CURRENT_STASH);
    }

    @Subscribe
    public void onRecipeComplete(RecipeSearchComplete event) {
        DatabaseHandler db = DatabaseHandler.getInstance(getContext());

        for (SearchResult result : event.results) {
            ResultViewItem item = new ResultViewItem();

            /*
            item.setRecipe(db.getServerRecipe(result.getRecipieId()));

            List<Ingredient> noIngredientMatches = new ArrayList<>();
            for (Integer ingredientId : result.getIngredientNoMatches()) {
                noIngredientMatches.add(db.getServerIngredient(ingredientId));
            }
            item.setMissingIngredients(noIngredientMatches);

            /*
            List<Category> noCategoryMatches = new ArrayList<>();
            for (Integer categoryId : result.getCategoryMatches()) {
                noCategoryMatches.add();
            }
            item.setMissingIngredients(noMatches);
            */
            item.setResult(result);
            resultListAdapter.addRecipe(item);
        }
    }

    private class AddRecipeOnUiThread implements Runnable {

        private ResultViewItem item;

        public AddRecipeOnUiThread(ResultViewItem item) {
            this.item = item;
        }

        @Override
        public void run() {
            resultListAdapter.addRecipe(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}