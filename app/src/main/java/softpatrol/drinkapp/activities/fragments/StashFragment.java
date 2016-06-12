package softpatrol.drinkapp.activities.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import softpatrol.drinkapp.R;
import softpatrol.drinkapp.activities.BaseActivity;
import softpatrol.drinkapp.activities.RootActivity;
import softpatrol.drinkapp.api.Analyzer;
import softpatrol.drinkapp.api.Definitions;
import softpatrol.drinkapp.api.Poster;
import softpatrol.drinkapp.database.DatabaseHandler;
import softpatrol.drinkapp.database.models.stash.Stash;
import softpatrol.drinkapp.layout.components.StashView;
import softpatrol.drinkapp.util.Debug;

/**
 * David was here on 2016-06-08!
 * Copy this code for instant regret...
 */
public class StashFragment extends Fragment {
    private int selectedStash;
    private ArrayList<Stash> stashes;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public StashFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static StashFragment newInstance(int sectionNumber) {
        StashFragment fragment = new StashFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stash, container, false);
        selectedStash = -1;
        updateView(rootView);
        return rootView;
    }

    private void updateView(final View rootView) {
        DatabaseHandler db = new DatabaseHandler(getContext());
        stashes = ((ArrayList<Stash>)db.getAllStashes());
        ((LinearLayout)rootView.findViewById(R.id.stash_item_container)).removeAllViews();
        LinearLayout row = new LinearLayout(getContext());
        ((LinearLayout) rootView.findViewById(R.id.stash_item_container)).addView(row);
        //row.setPadding(10,10,10,10);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, RootActivity.displayHeight / 5));
        row.setPadding(25, 25, 25, 25);

        LinearLayout nextBox = new LinearLayout(getContext());
        nextBox.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f));
        row.addView(nextBox);
        if(stashes != null) {
            for (int i = 0; i < stashes.size(); i++) {
                if(selectedStash == stashes.get(i).getId())nextBox.setBackground(getActivity().getDrawable(R.drawable.button_border_selected));
                else nextBox.setBackground(getActivity().getDrawable(R.drawable.button_border));
                StashView stashView = new StashView(getContext());
                stashView.setName(stashes.get(i).getName());
                stashView.setIcon(getActivity().getDrawable(R.drawable.phonebook_filled));
                nextBox.addView(stashView);
                final int finalI = i;
                nextBox.setOnClickListener(new View.OnClickListener() {
                    private Stash stash = stashes.get(finalI);
                    @Override
                    public void onClick(View v) {
                        System.out.println(stash.toString());
                        selectedStash = (int) stash.getId();
                        updateView(rootView);
                        //TODO: Edit Stash
                    }
                });
                if (i % 2 == 1) {
                    row = new LinearLayout(getContext());
                    ((LinearLayout) rootView.findViewById(R.id.stash_item_container)).addView(row);
                    //row.setPadding(10,10,10,10);
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, RootActivity.displayHeight / 5));
                    row.setPadding(25, 25, 25, 25);
                }
                nextBox = new LinearLayout(getContext());
                nextBox.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT, 1f));
                row.addView(nextBox);
            }
        }
        nextBox.setBackground(getActivity().getDrawable(R.drawable.button_border));
        StashView stashView = new StashView(getContext());
        stashView.setName("Create New Stash");
        stashView.setIcon(getActivity().getDrawable(R.drawable.settings));
        nextBox.addView(stashView);
        nextBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("CREATE NEW STASH!");
                addStash();
                updateView(rootView);
            }
        });
        if(stashes == null || stashes.size() % 2 == 0) {
            if(stashes == null) stashes = new ArrayList<>();
            LinearLayout empty = new LinearLayout(getContext());
            empty.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, 1f));
            row.addView(empty);
        }
    }

    private void addStash() {
        DatabaseHandler db = DatabaseHandler.getInstance(getContext());
        Stash stash = new Stash();
        //Fake load
        long serverId = 0;
        String pictureId = "picture0";
        String name = "Home" + stashes.size();
        String ownerId = "Account1";
        ArrayList<Long> ingredientsIds = new ArrayList<>();
        ingredientsIds.add(1L);
        ingredientsIds.add(2L);
        ingredientsIds.add(3L);
        ArrayList<Long> resultingDrinks = new ArrayList<>();
        resultingDrinks.add(1L);
        String accessState = "private";
        long createdAt = System.currentTimeMillis();
        long latestModification = System.currentTimeMillis();
        //Fake load
        stash.setServerId(serverId);
        stash.setPictureId(pictureId);
        stash.setName(name);
        stash.setOwnerId(ownerId);
        stash.setIngredientsIds(ingredientsIds);
        stash.setResultingDrinks(resultingDrinks);
        stash.setAccessState(accessState);
        stash.setCreatedAt(createdAt);
        stash.setLatestModification(latestModification);

        db.addStash(stash);

        JSONObject postParameters = new JSONObject();
        try {
            postParameters.put("stashName", stash.getName());
            JSONArray jsonArray = new JSONArray();
            for(Long l : stash.getIngredientsIds()) jsonArray.put(l);
            postParameters.put("ingredientIds", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<Pair<String, String>> headers = new ArrayList<>();
        headers.add(new Pair<>("Content-Type", "application/json"));
        headers.add(new Pair<>("Authorization", db.getAccount(DatabaseHandler.getCurrentAccount(getContext())).getToken()));

        new Poster(new PostStash(getActivity()), postParameters, headers).execute(Definitions.POST_STASH);
    }

    private class PostStash extends Analyzer {
        public PostStash(Activity activity) { super(activity); }
        @Override
        public void analyzeData(JSONObject result) throws JSONException {
            Debug.debugMessage((BaseActivity) caller, "attempting to analyze stash");
            JSONObject stash = result.getJSONObject("data");

            DatabaseHandler db = DatabaseHandler.getInstance(caller);
            Stash fromServer = new Stash(stash);
            Stash inDb = db.getStash(fromServer.getName());
            inDb.setServerId(fromServer.getServerId());
            inDb.setCreatedAt(fromServer.getCreatedAt());
            inDb.setLatestModification(fromServer.getLatestModification());
            db.updateStash(inDb);

            ArrayList<Stash> stashes1 = new ArrayList<>(db.getAllStashes());
            for(Stash i : stashes1) Debug.debugMessage((BaseActivity) caller, i.toString());
        }
    }
}