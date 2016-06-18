package softpatrol.drinkapp.layout.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import softpatrol.drinkapp.R;


/**
 * Created by root on 6/17/16.
 */
public class BottomBarItem extends RelativeLayout implements IOutsideTabClicked {

    private TextView titleTextView;
    private ImageView iconImageView;

    private String titleName = "";
    private List<IOutsideTabClicked> listeners;

    private int badges = 0;
    private ImageView badgeIcon;
    private TextView badgeText;

    private boolean isFocused = false;
    private int tabId = 0;

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

        /*
        // Set the badge icon position
        badgeIcon = (ImageView) findViewById(R.id.bottom_bar_menu_item_img_badge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.leftMargin=iconImageView.getLayoutParams().width;
        params.bottomMargin=iconImageView.getLayoutParams().height;
        badgeIcon.setLayoutParams(params);
        */


        badgeText = (TextView) findViewById(R.id.bottom_bar_menu_item_badge_text);
        /*
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(100, 100);
        params1.leftMargin=iconImageView.getLayoutParams().width-30;
        params1.bottomMargin=iconImageView.getLayoutParams().height-15;
        badgeText.setLayoutParams(params1);
        badgeText.setText(""+badges);
        */

        if (badges <= 0) {
            //badgeIcon.setVisibility(INVISIBLE);
            badgeText.setVisibility(INVISIBLE);
        }

        titleTextView.setVisibility(INVISIBLE);

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

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isFocused = true;
                self.setScaleX(1.3f);
                self.setScaleY(1.3f);

                titleTextView.setVisibility(VISIBLE);
                fragmentViewPager.setCurrentItem(fragmentId,true);

                self.notifyOutsideListeners();
            }
        });
    }

    private void notifyOutsideListeners() {
        for (IOutsideTabClicked listener : listeners) {
            listener.outsideClick(tabId);
        }
    }

    public TextView getBadgeText() {
        return badgeText;
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
        isFocused = false;
        this.setScaleY(1);
        this.setScaleX(1);
        titleTextView.setVisibility(INVISIBLE);
    }

    public int getBadges() {
        return badges;
    }

    public void setBadges(int badges) {
        if (badges > 0) {
            //badgeIcon.setVisibility(VISIBLE);
            badgeText.setVisibility(VISIBLE);
        }
        if (badgeText != null) {
            badgeText.setText("" +badges);
        }
        this.badges = badges;
    }

    public int getTabId() {
        return tabId;
    }

    public void setTabId(int tabId) {
        this.tabId = tabId;
    }
}
