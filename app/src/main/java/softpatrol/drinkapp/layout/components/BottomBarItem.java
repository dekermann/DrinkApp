package softpatrol.drinkapp.layout.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import softpatrol.drinkapp.R;
import softpatrol.drinkapp.activities.MainActivity;
import softpatrol.drinkapp.activities.fragments.Fragment;


/**
 * Created by root on 6/17/16.
 */
public class BottomBarItem extends RelativeLayout implements IOutsideTabClicked {

    private TextView titleTextView;
    private ImageView iconImageView;
    private int fragmentActivityId;

    private String titleName = "";
    private List<IOutsideTabClicked> listeners;

    private int badges = 0;

    private boolean isFocused = false;
    private int tabId = 0;

    private int selectBgColor,unselectBgColor;

    public BottomBarItem(Context context,AttributeSet aSet) {
        super(context,aSet);

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
        listeners = new ArrayList<>();
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
        fragmentActivityId = fragmentId;

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isFocused = true;
                self.setScaleX(1.2f);
                self.setScaleY(1.2f);

                setBackgroundColor(selectBgColor);
                fragmentViewPager.setCurrentItem(fragmentId,true);
                ((Fragment) ((MainActivity.SectionsPagerAdapter)fragmentViewPager.getAdapter()).getItem(fragmentId)).onEntered();

                self.notifyOutsideListeners();
            }
        });
    }

    public void select() {
        isFocused = true;
        setScaleX(1.2f);
        setScaleY(1.2f);
        setBackgroundColor(selectBgColor);
        notifyOutsideListeners();
    }

    public void unselect() {
        isFocused = false;
        setScaleX(1);
        setScaleY(1);
        setBackgroundColor(unselectBgColor);
    }

    private void notifyOutsideListeners() {
        for (IOutsideTabClicked listener : listeners) {
            listener.outsideClick(tabId);
        }
    }

    public void addOutsideTabListener(IOutsideTabClicked listener) {
        listeners.add(listener);
    }

    public void addOutsideTabListeners(BottomBarItem ... newListeners) {
        for (IOutsideTabClicked listener : newListeners) {
            addOutsideTabListener(listener);
        }
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

    public int getBadges() {
        return badges;
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
}
