package softpatrol.drinkapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.IdRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
import softpatrol.drinkapp.layout.components.BottomBarItem;
import softpatrol.drinkapp.model.event.BadgeEvent;
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


    private BottomBarItem resultBottomBarItem;




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

        /*
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
        */

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        assert mViewPager != null;
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View arg0, MotionEvent arg1) {
                return true;
            }
        });

        mViewPager.setCurrentItem(2);


        BottomBarItem bbTab1 = (BottomBarItem) findViewById(R.id.activity_root_bottom_bar_tab_1);
        bbTab1.setCustomClickListener(mViewPager,0);
        bbTab1.setIconImageView(getResources().getDrawable(R.drawable.fragment_home,null));

        BottomBarItem bbTab2 = (BottomBarItem) findViewById(R.id.activity_root_bottom_bar_tab_2);
        bbTab2.setCustomClickListener(mViewPager,1);
        bbTab2.setIconImageView(getResources().getDrawable(R.drawable.search,null));

        BottomBarItem bbTab3 = (BottomBarItem) findViewById(R.id.activity_root_bottom_bar_tab_3);
        bbTab3.setCustomClickListener(mViewPager,2);
        bbTab3.setIconImageView(getResources().getDrawable(R.drawable.fragment_scan,null));

        resultBottomBarItem = (BottomBarItem) findViewById(R.id.activity_root_bottom_bar_tab_4);
        resultBottomBarItem.setCustomClickListener(mViewPager,3);
        resultBottomBarItem.setIconImageView(getResources().getDrawable(R.drawable.fragment_home,null));

        BottomBarItem bbTab5 = (BottomBarItem) findViewById(R.id.activity_root_bottom_bar_tab_5);
        bbTab5.setCustomClickListener(mViewPager,4);
        bbTab5.setIconImageView(getResources().getDrawable(R.drawable.fragment_home,null));


        bbTab1.addOutsideTabListeners(bbTab2,bbTab3,resultBottomBarItem,bbTab5);
        bbTab2.addOutsideTabListeners(bbTab1,bbTab3,resultBottomBarItem,bbTab5);
        bbTab3.addOutsideTabListeners(bbTab1,bbTab2,resultBottomBarItem,bbTab5);
        resultBottomBarItem.addOutsideTabListeners(bbTab1,bbTab2,bbTab3,bbTab5);
        bbTab5.addOutsideTabListeners(bbTab1,bbTab2,bbTab3,resultBottomBarItem);
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

    /*
    * Messaging service between stuff
     */

    @Subscribe
    public void onBadgeEvent(BadgeEvent event) {
        resultBottomBarItem.setBadges(event.numberOfBadges);
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
