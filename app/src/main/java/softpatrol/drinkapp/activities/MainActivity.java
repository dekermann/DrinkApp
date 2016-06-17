package softpatrol.drinkapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import softpatrol.drinkapp.R;
import softpatrol.drinkapp.activities.fragments.MyPageFragment;
import softpatrol.drinkapp.activities.fragments.ResultFragment;
import softpatrol.drinkapp.activities.fragments.ScanFragment;
import softpatrol.drinkapp.activities.fragments.SocialFragment;
import softpatrol.drinkapp.activities.fragments.StashFragment;
import softpatrol.drinkapp.api.DataSynchronizer;
import softpatrol.drinkapp.database.DatabaseHandler;
import softpatrol.drinkapp.database.models.stash.Stash;
import softpatrol.drinkapp.util.Utils;

/**
 * David was here on 2016-06-08!
 * Copy this code for instant regret...
 */
public class MainActivity extends BaseActivity {
    public static final String SERVICE_ID = "serviceId";
    public static final String NOTIFICATION = "com.example.monstermaskinen.drinkapp.receiver";
    public static final String RESULT = "result";
    public static final int SERVICE_1 = 1;
    public static final int SERVICE_2 = 2;
    public static final int SERVICE_GCM = 3;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    //Singleton
    private static MainActivity mainActivity;
    public static MainActivity getMain(){
        if (mainActivity == null){
            return null;
        }
        return mainActivity;
    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int whatService = bundle.getInt(MainActivity.SERVICE_ID);
                int resultCode = bundle.getInt(RESULT);
                switch (whatService) {
                    case SERVICE_1:
                        if (resultCode == RESULT_OK) {
                            Toast.makeText(MainActivity.this, "Service_1 succeeded",
                                    Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(MainActivity.this, "Service_1 failed",
                                    Toast.LENGTH_LONG).show();
                        }
                        break;
                    case SERVICE_2:
                        if (resultCode == RESULT_OK) {
                            Toast.makeText(MainActivity.this, "Service_2 succeeded",
                                    Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(MainActivity.this, "Service_2 failed",
                                    Toast.LENGTH_LONG).show();
                        }
                        break;
                    case SERVICE_GCM:
                        if (resultCode == RESULT_OK) {
                            Toast.makeText(MainActivity.this, "Service_GCM succeeded",
                                    Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(MainActivity.this, "Service_GCM failed",
                                    Toast.LENGTH_LONG).show();
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ACTIVITY_NAME = "Main";
        super.onCreate(savedInstanceState);
        mainActivity = this;

        Utils.makeFolder();

        DataSynchronizer dataSynchronizer = new DataSynchronizer(this);
        dataSynchronizer.syncIngredients();
        dataSynchronizer.syncRecipes();
        setContentView(R.layout.activity_root);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        assert mViewPager != null;
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                mSectionsPagerAdapter.setCurrentPage(position);
                mSectionsPagerAdapter.notifyDataSetChanged();
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.setCurrentItem(2);
    }
    @Override
    public void onResume() {
        super.onResume();
        DatabaseHandler.getInstance(this);
    }
    @Override
    public void onPause() {
        DatabaseHandler.getInstance(this).close();
        super.onPause();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        int currentPage = 2;
        int lastPage = 2;
        SocialFragment socialFragment;
        StashFragment stashFragment;
        ScanFragment scanFragment;
        ResultFragment resultFragment;
        MyPageFragment myPageFragment;
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            socialFragment = SocialFragment.newInstance(0);
            stashFragment = StashFragment.newInstance(1);
            scanFragment = ScanFragment.newInstance(2);
            resultFragment = ResultFragment.newInstance(3);
            myPageFragment = MyPageFragment.newInstance(4);
        }
        public void setCurrentPage(int page) { lastPage = currentPage; currentPage = page; }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return socialFragment;
                case 1: return stashFragment;
                case 2: return scanFragment;
                case 3: return resultFragment;
                case 4: return myPageFragment;
                default: return null;
            }
        }
        @Override
        public int getCount() { return 5; }

    }

    public void refreshStash() {
        ((softpatrol.drinkapp.activities.fragments.Fragment) ((SectionsPagerAdapter) mViewPager.getAdapter()).getItem(0)).refreshStash();
        ((softpatrol.drinkapp.activities.fragments.Fragment) ((SectionsPagerAdapter) mViewPager.getAdapter()).getItem(1)).refreshStash();
        ((softpatrol.drinkapp.activities.fragments.Fragment) ((SectionsPagerAdapter) mViewPager.getAdapter()).getItem(2)).refreshStash();
        ((softpatrol.drinkapp.activities.fragments.Fragment) ((SectionsPagerAdapter) mViewPager.getAdapter()).getItem(3)).refreshStash();
        ((softpatrol.drinkapp.activities.fragments.Fragment) ((SectionsPagerAdapter) mViewPager.getAdapter()).getItem(4)).refreshStash();
    }
}
