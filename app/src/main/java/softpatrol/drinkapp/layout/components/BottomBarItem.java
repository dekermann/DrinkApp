package softpatrol.drinkapp.layout.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import softpatrol.drinkapp.R;
import softpatrol.drinkapp.activities.MainActivity;
import softpatrol.drinkapp.model.event.BottomBarEvent;
import softpatrol.drinkapp.activities.fragments.Fragment;

/**
 * Created by root on 6/17/16.
 */
public class BottomBarItem extends RelativeLayout implements IOutsideTabClicked {

    private String titleName = "";

    private TextView titleTextView;
    private ImageView iconImageView;
    private ViewPager fragmentViewPager;

    private int tabId = -1;
    private int fragmentId = -1;
    private boolean isFocused = false;

    private int selectBgColor,unselectBgColor;

    public BottomBarItem(Context context,AttributeSet aSet) {
        super(context,aSet);

        EventBus.getDefault().register(this);

        TypedArray arr = context.obtainStyledAttributes(aSet, R.styleable.BottomBarItem);
        CharSequence titleName = arr.getString(R.styleable.BottomBarItem_name);
        tabId = arr.getInt(R.styleable.BottomBarItem_tab_id,0);
        arr.recycle();

        inflate(getContext(), R.layout.bottom_bar_item, this);
        titleTextView = (TextView) findViewById(R.id.bottom_bar_menu_item_text);
        titleTextView.setText(titleName);
        iconImageView = (ImageView) findViewById(R.id.bottom_bar_menu_item_img);

        titleTextView.setVisibility(VISIBLE);

        selectBgColor = getResources().getColor(R.color.white);
        unselectBgColor = getResources().getColor(R.color.white);
    }

    public void setTitleTextView(String titleTextView) {
        this.titleTextView.setText(titleTextView);
    }

    public TextView getTitleTextView() {
        return titleTextView;
    }


    public void setIconImageView(Drawable drawable) {
        iconImageView.setImageDrawable(drawable);
    }

    public void setCustomClickListener(final ViewPager fragmentViewPager, final int fragmentId) {
        final BottomBarItem self = this;
        this.fragmentId = fragmentId;
        this.fragmentViewPager = fragmentViewPager;

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new BottomBarEvent(self.fragmentId,true));
            }
        });
    }

    private void select() {
        isFocused = true;
        setScaleX(1.2f);
        setScaleY(1.2f);
        setBackgroundColor(selectBgColor);
    }

    private void unselect() {
        isFocused = false;
        setScaleX(1);
        setScaleY(1);
        setBackgroundColor(unselectBgColor);
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
        if (titleTextView != null) {
            titleTextView.setText(titleName);
        }
    }

    @Override
    public void outsideClick(int tabIndex) {
        unselect();
    }

    public int getTabId() {
        return tabId;
    }

    public void setTabId(int tabId) {
        this.tabId = tabId;
    }

    public int getSelectBgColor() {
        return selectBgColor;
    }

    public void setSelectBgColor(int selectBgColor) {
        this.selectBgColor = selectBgColor;
    }

    @Subscribe
    public void onEditStashEvent(BottomBarEvent event) {
        if (event.id == this.fragmentId)  {
            select();
            if (event.changeFragment) {
                fragmentViewPager.setCurrentItem(fragmentId,true);
                ((Fragment) ((MainActivity.SectionsPagerAdapter)fragmentViewPager.getAdapter()).getItem(fragmentId)).onEntered();
            }
        } else {
            unselect();
        }
    }

    @Override
    public boolean isFocused() {
        return isFocused;
    }

    public void setFocused(boolean focused) {
        isFocused = focused;
    }
}
