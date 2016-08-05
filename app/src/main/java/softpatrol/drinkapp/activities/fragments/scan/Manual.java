package softpatrol.drinkapp.activities.fragments.scan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;

import softpatrol.drinkapp.R;
import softpatrol.drinkapp.activities.fragments.Fragment;
import softpatrol.drinkapp.activities.fragments.ScanFragment;
import softpatrol.drinkapp.activities.fragments.stash.StashFragment;
import softpatrol.drinkapp.database.models.ingredient.Ingredient;
import softpatrol.drinkapp.model.event.EditCurrentStashEvent;

/**
 * David was here on 2016-06-08!
 * Copy this code for instant regret...
 */
public class Manual extends Fragment {
    int sectionNumber;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";


    private AppCompatActivity activity;

    public Manual() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Manual newInstance(int sectionNumber) {
        Manual fragment = new Manual();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int fragmentId = getArguments().getInt(ARG_SECTION_NUMBER);
        View rootView = inflater.inflate(R.layout.fragment_scan_manual, container, false);

        final ImageView manual_add = (ImageView) rootView.findViewById(R.id.manual_add);
        manual_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manual_add.setImageDrawable(getContext().getDrawable(R.drawable.manual_add));
                //FAKE ADD
                long fakeId = (long) (Math.random()*10) + 1;
                fakeId = 1;
                //Debug.debugMessage((BaseActivity) getActivity(), "FOUND INGREDIENT " + fakeId + ": " + DatabaseHandler.getInstance(getContext()).getServerIngredient(fakeId).getName());
                //((TestAdapter)mScannedItems.getAdapter()).addItems(DatabaseHandler.getInstance(getContext()).getServerIngredient(fakeId));
                ((ScanFragment.TestAdapter) ScanFragment.mScannedItems.getAdapter()).addItems(new Ingredient());
                StashFragment.CURRENT_STASH.addIngredientId(fakeId);
                EventBus.getDefault().post(new EditCurrentStashEvent());
            }
        });

        return rootView;
    }
}