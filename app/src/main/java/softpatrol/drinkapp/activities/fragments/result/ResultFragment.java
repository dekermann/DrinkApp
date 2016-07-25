package softpatrol.drinkapp.activities.fragments.result;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import softpatrol.drinkapp.R;
import softpatrol.drinkapp.activities.fragments.Fragment;
import softpatrol.drinkapp.activities.fragments.stash.StashFragment;
import softpatrol.drinkapp.api.Analyzer;
import softpatrol.drinkapp.api.Definitions;
import softpatrol.drinkapp.api.Getter;
import softpatrol.drinkapp.database.DatabaseHandler;
import softpatrol.drinkapp.database.models.recipe.Recipe;
import softpatrol.drinkapp.database.models.stash.Stash;
import softpatrol.drinkapp.layout.components.popups.FragmentFilter;
import softpatrol.drinkapp.model.dto.SearchResult;
import softpatrol.drinkapp.model.event.ChangeCurrentStashEvent;
import softpatrol.drinkapp.model.event.EditCurrentStashEvent;
import softpatrol.drinkapp.model.event.EventCreatePopUp;
import softpatrol.drinkapp.model.event.EventRecipeSearchComplete;

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

    private TextView showingText;
    private AppCompatActivity activity;

    private Set<Long> latestSearchParams = new HashSet<>();

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

        resultListAdapter = new ResultRecipeAdapter(new ArrayList<SearchResult>(),getContext());
        mRecycleView.setAdapter(resultListAdapter);
        mRecycleView.setHasFixedSize(true);

        showingText = (TextView) rootView.findViewById(R.id.fragment_result_showing);
        showingText.setText("No results found ...");
            ArrayList<Pair<String, String>> headers = new ArrayList<>();
            headers.add(new Pair<>("Content-Type", "application/json"));
            DatabaseHandler db = DatabaseHandler.getInstance(getContext());
            headers.add(new Pair<>("Authorization", db.getAccount(DatabaseHandler.getCurrentAccount(getContext())).getToken()));

        Button b = (Button) rootView.findViewById(R.id.fragment_result_toolbar_filter_btn);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentFilter ff = new FragmentFilter(getActivity());
                EventBus.getDefault().post(new EventCreatePopUp(ff));
            }
        });


        Toolbar tb = (Toolbar) rootView.findViewById(R.id.fragment_result_toolbar);
        tb.setTitle("");

        activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(tb);
        return rootView;
    }

    public Set<Long> getLatestSearchParams() {
        return latestSearchParams;
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
                SearchResult sr = SearchResult.deserialize(obj);
                results.add(sr);
            }

            EventBus.getDefault().post(new EventRecipeSearchComplete(results));
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

    private void stashChange(Stash stash) {
        if (stash.getIngredientsIds().size() > 0) {
            String strList = "";
            latestSearchParams.clear();

            for (int i = 0; i < stash.getIngredientsIds().size(); i++) {
                strList += stash.getIngredientsIds().get(i);
                latestSearchParams.add(stash.getIngredientsIds().get(i));

                if (i < stash.getIngredientsIds().size() - 1) {
                    strList += ",";
                }
            }

            NameValuePair nvp = new BasicNameValuePair("ingredientIds", strList);

            ArrayList<Pair<String, String>> headers = new ArrayList<>();
            headers.add(new Pair<>("Content-Type", "application/json"));
            DatabaseHandler db = DatabaseHandler.getInstance(getContext());
            headers.add(new Pair<>("Authorization", db.getAccount(DatabaseHandler.getCurrentAccount(getContext())).getToken()));

            new Getter(new ResultParser(this.getContext()), nvp,headers).execute(Definitions.GET_SEARCH);
        }
    }

    @Subscribe
    public void onRecipeComplete(EventRecipeSearchComplete event) {
        showingText.setText("Showing " + event.results.size() + " out of " + event.results.size() + " found recipes...");
        resultListAdapter.clearAndAddRecipes(event.results);
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