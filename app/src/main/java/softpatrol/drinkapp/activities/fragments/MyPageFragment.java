package softpatrol.drinkapp.activities.fragments;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import softpatrol.drinkapp.R;

/**
 * David was here on 2016-06-08!
 * Copy this code for instant regret...
 */
public class MyPageFragment extends Fragment {
    int sectionNumber;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private AppCompatActivity activity;

    public MyPageFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MyPageFragment newInstance(int sectionNumber) {
        MyPageFragment fragment = new MyPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int fragmentId = getArguments().getInt(ARG_SECTION_NUMBER);
        View rootView = inflater.inflate(R.layout.fragment_mypage, container, false);

        Toolbar tb = (Toolbar) rootView.findViewById(R.id.fragment_mypage_toolbar);
        tb.setTitle("My page");

        activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(tb);
        return rootView;
    }
}