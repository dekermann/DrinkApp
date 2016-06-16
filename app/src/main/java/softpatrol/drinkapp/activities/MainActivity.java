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

        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        int currentPage = 2;
        int lastPage = 2;
        public SectionsPagerAdapter(FragmentManager fm) { super(fm); }
        public void setCurrentPage(int page) { lastPage = currentPage; currentPage = page; }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return SocialFragment.newInstance(position);
                case 1: return StashFragment.newInstance(position);
                case 2: return ScanFragment.newInstance(position);
                case 3: return ResultFragment.newInstance(position);
                case 4: return MyPageFragment.newInstance(position);
                default: return ScanFragment.newInstance(position);
            }
        }
        @Override
        public int getCount() { return 5; }

        //Bottom Action Bar
        @Override
        public CharSequence getPageTitle(int position) {
            SpannableString sb;
            Drawable image;
            ImageSpan imageSpan;
            switch (position) {
                case 0:
                    if(currentPage == position) {
                        image = ContextCompat.getDrawable(getBaseContext(), R.drawable.settings);
                        image.setBounds(0, 0, 140, 140);
                        ((softpatrol.drinkapp.activities.fragments.Fragment) getItem(position)).onFocused();
                    }
                    else {
                        image = ContextCompat.getDrawable(getBaseContext(), R.drawable.settings);
                        image.setBounds(0, 0, 100, 100);
                    }
                    sb = new SpannableString(" ");
                    imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
                    sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case 1:
                    if(currentPage == position) {
                        image = ContextCompat.getDrawable(getBaseContext(), R.drawable.settings);
                        image.setBounds(0, 0, 140, 140);
                        ((softpatrol.drinkapp.activities.fragments.Fragment) getItem(position)).onFocused();
                    }
                    else {
                        image = ContextCompat.getDrawable(getBaseContext(), R.drawable.settings);
                        image.setBounds(0, 0, 100, 100);
                    }
                    sb = new SpannableString(" ");
                    imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
                    sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case 2:
                    if(currentPage == position) {
                        image = ContextCompat.getDrawable(getBaseContext(), R.drawable.settings);
                        image.setBounds(0, 0, 140, 140);
                        ((softpatrol.drinkapp.activities.fragments.Fragment) getItem(position)).onFocused();
                    }
                    else {
                        image = ContextCompat.getDrawable(getBaseContext(), R.drawable.settings);
                        image.setBounds(0, 0, 100, 100);
                    }
                    sb = new SpannableString(" ");
                    imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
                    sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case 3:
                    if(currentPage == position) {
                        image = ContextCompat.getDrawable(getBaseContext(), R.drawable.settings);
                        image.setBounds(0, 0, 140, 140);
                        ((softpatrol.drinkapp.activities.fragments.Fragment) getItem(position)).onFocused();
                    }
                    else {
                        image = ContextCompat.getDrawable(getBaseContext(), R.drawable.settings);
                        image.setBounds(0, 0, 100, 100);
                    }
                    sb = new SpannableString(" ");
                    imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
                    sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case 4:
                    if(currentPage == position) {
                        image = ContextCompat.getDrawable(getBaseContext(), R.drawable.settings);
                        image.setBounds(0, 0, 140, 140);
                        ((softpatrol.drinkapp.activities.fragments.Fragment) getItem(position)).onFocused();
                    }
                    else {
                        image = ContextCompat.getDrawable(getBaseContext(), R.drawable.settings);
                        image.setBounds(0, 0, 100, 100);
                    }
                    sb = new SpannableString(" ");
                    imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
                    sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                default:
                    if(currentPage == position) {
                        image = ContextCompat.getDrawable(getBaseContext(), R.drawable.settings);
                        image.setBounds(0, 0, 140, 140);
                        ((softpatrol.drinkapp.activities.fragments.Fragment) getItem(position)).onFocused();
                    }
                    else {
                        image = ContextCompat.getDrawable(getBaseContext(), R.drawable.settings);
                        image.setBounds(0, 0, 100, 100);
                    }
                    sb = new SpannableString(" ");
                    imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
                    sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
            }
            return sb;
        }

    }
}
