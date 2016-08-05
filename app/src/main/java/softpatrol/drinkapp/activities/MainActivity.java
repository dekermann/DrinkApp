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
import android.view.animation.RotateAnimation;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import softpatrol.drinkapp.R;
import softpatrol.drinkapp.activities.fragments.MyPageFragment;
import softpatrol.drinkapp.activities.fragments.result.ResultFragment;
import softpatrol.drinkapp.activities.fragments.ScanFragment;
import softpatrol.drinkapp.activities.fragments.SocialFragment;
import softpatrol.drinkapp.activities.fragments.stash.StashFragment;
import softpatrol.drinkapp.api.DataSynchronizer;
import softpatrol.drinkapp.database.DatabaseHandler;
import softpatrol.drinkapp.layout.components.BottomBarDefault;
import softpatrol.drinkapp.layout.components.CameraBottomBar;
import softpatrol.drinkapp.layout.components.CustomViewPager;
import softpatrol.drinkapp.model.event.EventCreatePopUp;
import softpatrol.drinkapp.model.event.EventRecipeSearchComplete;
import softpatrol.drinkapp.model.event.EventSwapFragment;
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

    private BottomBarDefault bottomButtonHome;
    private BottomBarDefault bottomButtonStash;
    private CameraBottomBar bottomButtonScan;
    private BottomBarDefault bottomButtonRecipe;
    private BottomBarDefault bottomButtonSocial;

    public static final int FRAGMENT_ID_BUTTON_HOME = 0;
    public static final int FRAGMENT_ID_BUTTON_STASH = 1;
    public static final int FRAGMENT_ID_SCAN = 2;
    public static final int FRAGMENT__ID_RECIPE = 3;
    public static final int FRAGMENT_ID_SOCIAL = 4;

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
        //dataSynchronizer.syncRecipes();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_root);

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

        bottomButtonHome = (BottomBarDefault) findViewById(R.id.activity_root_bottom_bar_tab_1);
        bottomButtonHome.setFragmentId(FRAGMENT_ID_BUTTON_HOME);
        bottomButtonHome.setSelectBgColor(getResources().getColor(R.color.light_green));
        bottomButtonHome.setIconImageView(getResources().getDrawable(R.drawable.fragment_home,null));

        bottomButtonStash = (BottomBarDefault) findViewById(R.id.activity_root_bottom_bar_tab_2);
        bottomButtonStash.setFragmentId(FRAGMENT_ID_BUTTON_STASH);
        bottomButtonStash.setSelectBgColor(getResources().getColor(R.color.Thistle));
        bottomButtonStash.setIconImageView(getResources().getDrawable(R.drawable.search,null));

        bottomButtonScan = (CameraBottomBar) findViewById(R.id.activity_root_bottom_bar_tab_3);
        bottomButtonScan.setFragmentId(FRAGMENT_ID_SCAN);
        bottomButtonScan.setMoveableImageView((ImageView) getWindow().getDecorView().getRootView().findViewById(R.id.acitivty_root_random_img));
        bottomButtonScan.setSelectBgColor(getResources().getColor(R.color.Wheat));
        bottomButtonScan.setIconImageView(getResources().getDrawable(R.drawable.fragment_scan,null));

        bottomButtonRecipe = (BottomBarDefault) findViewById(R.id.activity_root_bottom_bar_tab_4);
        bottomButtonRecipe.setFragmentId(FRAGMENT__ID_RECIPE);
        bottomButtonRecipe.setSelectBgColor(getResources().getColor(R.color.PaleTurquoise));
        bottomButtonRecipe.setIconImageView(getResources().getDrawable(R.drawable.fragment_result,null));

        bottomButtonSocial = (BottomBarDefault) findViewById(R.id.activity_root_bottom_bar_tab_5);
        bottomButtonSocial.setFragmentId(FRAGMENT_ID_SOCIAL);
        bottomButtonSocial.setSelectBgColor(getResources().getColor(R.color.light_yellow));
        bottomButtonSocial.setIconImageView(getResources().getDrawable(R.drawable.fragment_social,null));


        EventBus.getDefault().post(new EventSwapFragment(FRAGMENT_ID_SCAN));
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
    }

    @Subscribe
    public void onPopUp(final EventCreatePopUp event) {
        final RelativeLayout r = (RelativeLayout) findViewById(R.id.popup_main);
        r.setPadding(RootActivity.displayWidth / 10, RootActivity.displayHeight / 10, RootActivity.displayWidth / 10, RootActivity.displayHeight / 10);
        event.popUpLayout.setParent(r);
        r.addView(event.popUpLayout);
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.popUpLayout.close();
            }
        });
        r.setVisibility(View.VISIBLE);
        r.invalidate();
    }

    @Subscribe
    public void onSwapFragment(final EventSwapFragment event) {
        mViewPager.setCurrentItem(event.fragmentId);
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