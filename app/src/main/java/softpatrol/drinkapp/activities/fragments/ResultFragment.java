package softpatrol.drinkapp.activities.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import softpatrol.drinkapp.R;
import softpatrol.drinkapp.api.Analyzer;
import softpatrol.drinkapp.api.Definitions;
import softpatrol.drinkapp.api.Getter;
import softpatrol.drinkapp.database.models.recipe.Recipe;
import softpatrol.drinkapp.layout.components.BottomBarItem;
import softpatrol.drinkapp.model.dto.SearchResult;
import softpatrol.drinkapp.model.event.BadgeEvent;

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

    private List<SearchResult> searchResults = new ArrayList<>();

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

        resultListAdapter = new ResultRecipeAdapter(new ArrayList<Recipe>());
        mRecycleView.setAdapter(resultListAdapter);
        mRecycleView.setHasFixedSize(true);

        return rootView;
    }

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
            searchResults = results;
        }
    }

    class ResultRecipeAdapter extends RecyclerView.Adapter<ResultRecipeAdapter.MyViewHolder> {

        private List<Recipe> dataSet;

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView text;
            public MyViewHolder(View itemView) {
                super(itemView);
                this.text = (TextView) itemView.findViewById(R.id.fragment_result_row_text);
            }
        }

        public ResultRecipeAdapter(List<Recipe> data) {
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

            text.setText(dataSet.get(listPosition).getName());
        }

        public void addRecipe(Recipe recipe) {
            dataSet.add(recipe);
            notifyItemInserted(dataSet.size()-1);
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }
    }

    @Override
    public void onFocused() {
        Log.d("Swapped to fragment","got stash: " + StashFragment.CURRENT_STASH.toString());
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);

        // When the fragment is visible
        if (visible) {
            List<NameValuePair> nvps = new ArrayList<>();

            NameValuePair searchIds = new BasicNameValuePair("ingredientIds","1,2,3");
            nvps.add(searchIds);


            if (!hasTestResults) {

                hasTestResults = true;
                new Getter(new ResultParser(this.getContext()),nvps).execute(Definitions.GET_SEARCH);

                // mimic getter time
                new CountDownTimer(1, 1) {


                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        new CountDownTimer(1500, 250) {

                            private String testNames[] = {"Gin och Tonic", "Bombay Sapphire", "Random shit", "Something gut"};
                            private int index = 0;

                            public void onTick(long millisUntilFinished) {
                                if (index < testNames.length) {
                                    Recipe testRecipe = new Recipe();
                                    testRecipe.setName(testNames[index++]);
                                    resultListAdapter.addRecipe(testRecipe);
                                }
                            }

                            public void onFinish() {
                                // TODO: change
                                EventBus.getDefault().post(new BadgeEvent(10));
                            }
                        }.start();
                    }
                }.start();
            }
        }
    }


}