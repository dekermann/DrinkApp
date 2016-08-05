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
import softpatrol.drinkapp.model.event.EventBottomBar;
import softpatrol.drinkapp.activities.fragments.Fragment;
import softpatrol.drinkapp.model.event.EventSwapFragment;

/**
 * Created by root on 6/17/16.
 */
public class BottomBarItem extends RelativeLayout {

    private String titleName = "";

    private TextView titleTextView;
    private ImageView iconImageView;


    private int fragmentId = -1;

    private int selectBgColor,unselectBgColor;

    public BottomBarItem(Context context,AttributeSet aSet) {
        super(context,aSet);

        EventBus.getDefault().register(this);

        TypedArray arr = context.obtainStyledAttributes(aSet, R.styleable.BottomBarItem);
        CharSequence titleName = arr.getString(R.styleable.BottomBarItem_name);
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

    public ImageView getIconImageView() {
        return iconImageView;
    }


    public void setIconImageView(Drawable drawable) {
        iconImageView.setImageDrawable(drawable);
    }

    public void select() {
        setScaleX(1);
        setScaleY(1);
        setBackgroundColor(selectBgColor);
    }

    public void unselect() {
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

    public int getFragmentId() {
        return fragmentId;
    }

    public void setFragmentId(int id) {
        this.fragmentId = id;
    }

    public int getSelectBgColor() {
        return selectBgColor;
    }

    public int getUnselectBgColor() {
        return unselectBgColor;
    }

    public void setSelectBgColor(int selectBgColor) {
        this.selectBgColor = selectBgColor;
    }

    @Subscribe
    public void onSwapFragment(final EventSwapFragment event) {
        if (event.fragmentId == this.fragmentId)  {
            select();
        } else {
            unselect();
        }
    }
}
