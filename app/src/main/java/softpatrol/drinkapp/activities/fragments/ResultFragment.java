package softpatrol.drinkapp.activities.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import softpatrol.drinkapp.R;
import softpatrol.drinkapp.api.Analyzer;
import softpatrol.drinkapp.api.Definitions;
import softpatrol.drinkapp.api.Getter;
import softpatrol.drinkapp.database.DatabaseHandler;
import softpatrol.drinkapp.database.models.recipe.Recipe;
import softpatrol.drinkapp.database.models.stash.Stash;
import softpatrol.drinkapp.layout.components.popups.FragmentFilter;
import softpatrol.drinkapp.layout.components.popups.FragmentResult;
import softpatrol.drinkapp.model.dto.ResultViewItem;
import softpatrol.drinkapp.model.dto.SearchResultSimple;
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

        showingText = (TextView) rootView.findViewById(R.id.fragment_result_showing);
        showingText.setText("No results found ...");

        Button b = (Button) rootView.findViewById(R.id.fragment_result_toolbar_filter_btn);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentFilter ff = new FragmentFilter(getActivity());
                EventBus.getDefault().post(new EventCreatePopUp(ff));
            }
        });


        Toolbar tb = (Toolbar) rootView.findViewById(R.id.fragment_result_toolbar);
        tb.setTitle("Result");

        activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(tb);
        return rootView;
    }



    /*
    * On stash change, do a search on the server
     */
    private void stashChange(Stash stash) {
        if (stash.getIngredientsIds().size() > 0) {
            // clear old list
            //resultListAdapter.clear();
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

            ArrayList<SearchResultSimple> results = new ArrayList<>();

            for (int i = 0; i < array.length();i++) {
                JSONObject obj = (JSONObject) array.get(i);
                SearchResultSimple sr = SearchResultSimple.deserialize(obj);
                results.add(sr);
            }

            EventBus.getDefault().post(new EventRecipeSearchComplete(results));
        }
    }

    /*
    * Adapter
     */
    class ResultRecipeAdapter extends RecyclerView.Adapter<ResultRecipeAdapter.MyViewHolder> {

        private List<ResultViewItem> dataSet;

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView titleText;
            TextView likeText;
            TextView commentText;

            ResultViewItem item;


            public MyViewHolder(View itemView) {
                super(itemView);
                this.titleText = (TextView) itemView.findViewById(R.id.fragment_result_row_title_text);
                this.likeText = (TextView) itemView.findViewById(R.id.fragment_result_row_like_text);
                this.commentText = (TextView) itemView.findViewById(R.id.fragment_result_row_comments_text);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item != null) {
                            SearchResultSimple srs = item.getResult();
                            Recipe recipe = item.getRecipe();
                            String title = recipe.getName();
                            String body = recipe.getBody();
                            FragmentResult fr = new FragmentResult(getActivity(),title,body,"Missing "+srs.getTotalMisses(),"Time: fast","Level: Novice",recipe.getPartCategories(),recipe.getPartIngredients());
                            EventBus.getDefault().post(new EventCreatePopUp(fr));
                        } else {
                            Toast.makeText(getContext(),"Could not open recipe, not loaded",Toast.LENGTH_LONG);
                        }
                    }
                });
            }

            public void setItem(ResultViewItem item) {
                this.item = item;
            }

            public ResultViewItem getItem() {
                return item;
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

            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
            ResultViewItem item = dataSet.get(listPosition);
            holder.setItem(item);
            SearchResultSimple sr2 = item.getResult();

            int missing = sr2.getTotalMisses();
            holder.titleText.setText(dataSet.get(listPosition).getRecipe().getName());
            holder.commentText.setText("Missing " + missing + " ingredient" + (missing == 1 ? "" : "s"));
            holder.likeText.setText("2 likes");
        }

        public void addRecipe(ResultViewItem recipe) {
            dataSet.add(recipe);
            notifyItemInserted(dataSet.size()-1);
        }

        public void clearAddRecipes(List<ResultViewItem> items) {
            dataSet.clear();
            dataSet.addAll(items);
            notifyDataSetChanged();
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
    public void onRecipeComplete(EventRecipeSearchComplete event) {
        List<ResultViewItem> items = new ArrayList<>();
        DatabaseHandler db = DatabaseHandler.getInstance(getContext());

        for (SearchResultSimple result : event.results) {
            ResultViewItem item = new ResultViewItem();

            Recipe recipe = db.getServerRecipe(result.getRecipeId());

            if (recipe != null) {
                item.setRecipe(recipe);
                item.setResult(result);
                items.add(item);
            }
        }
        showingText.setText("Showing " + items.size() + " out of " + items.size() + " found recipes...");
        resultListAdapter.clearAddRecipes(items);
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