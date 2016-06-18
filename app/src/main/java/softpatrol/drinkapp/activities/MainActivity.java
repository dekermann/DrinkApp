package softpatrol.drinkapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import softpatrol.drinkapp.R;
import softpatrol.drinkapp.activities.fragments.MyPageFragment;
import softpatrol.drinkapp.activities.fragments.ResultFragment;
import softpatrol.drinkapp.activities.fragments.ScanFragment;
import softpatrol.drinkapp.activities.fragments.SocialFragment;
import softpatrol.drinkapp.activities.fragments.StashFragment;
import softpatrol.drinkapp.api.DataSynchronizer;
import softpatrol.drinkapp.database.DatabaseHandler;
import softpatrol.drinkapp.layout.components.BottomBarItem;
import softpatrol.drinkapp.layout.components.CustomViewPager;
import softpatrol.drinkapp.model.event.EventRecipeSearchComplete;
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
    private CustomViewPager mViewPager;

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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

        mViewPager = (CustomViewPager) findViewById(R.id.container);
        mViewPager.setPagingEnabled(false);
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
        bbTab1.setSelectBgColor(getResources().getColor(R.color.green));
        bbTab1.setIconImageView(getResources().getDrawable(R.drawable.fragment_home,null));

        BottomBarItem bbTab2 = (BottomBarItem) findViewById(R.id.activity_root_bottom_bar_tab_2);
        bbTab2.setCustomClickListener(mViewPager,1);
        bbTab2.setSelectBgColor(getResources().getColor(R.color.blue));
        bbTab2.setIconImageView(getResources().getDrawable(R.drawable.search,null));

        BottomBarItem bbTab3 = (BottomBarItem) findViewById(R.id.activity_root_bottom_bar_tab_3);
        bbTab3.setCustomClickListener(mViewPager,2);
        bbTab3.setSelectBgColor(getResources().getColor(R.color.Pink));
        bbTab3.setIconImageView(getResources().getDrawable(R.drawable.fragment_scan,null));
        bbTab3.getTitleTextView().setVisibility(View.VISIBLE);

        resultBottomBarItem = (BottomBarItem) findViewById(R.id.activity_root_bottom_bar_tab_4);
        resultBottomBarItem.setCustomClickListener(mViewPager,3);
        resultBottomBarItem.setSelectBgColor(getResources().getColor(R.color.Aqua));
        resultBottomBarItem.setIconImageView(getResources().getDrawable(R.drawable.fragment_result,null));

        BottomBarItem bbTab5 = (BottomBarItem) findViewById(R.id.activity_root_bottom_bar_tab_5);
        bbTab5.setCustomClickListener(mViewPager,4);
        bbTab5.setSelectBgColor(getResources().getColor(R.color.yellow));
        bbTab5.setIconImageView(getResources().getDrawable(R.drawable.fragment_social,null));


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
    /*
    * Messaging service between stuff
     */

    @Subscribe
    public void onRecipeComplete(EventRecipeSearchComplete event) {
        RotateAnimation rotate= new RotateAnimation(0,30);
        rotate.setDuration(10);
        rotate.setRepeatCount(10);
        resultBottomBarItem.getBadgeText().startAnimation(rotate);
        resultBottomBarItem.setBadges(event.results.size());
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
