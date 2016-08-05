package softpatrol.drinkapp.layout.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import softpatrol.drinkapp.R;
import softpatrol.drinkapp.model.event.EventPhoto;
import softpatrol.drinkapp.model.event.EventScanFragment;
import softpatrol.drinkapp.model.event.EventSwapFragment;

/**
 * Created by rasmus on 8/5/16.
 */
public class CameraBottomBar extends BottomBarItem {

    public static final int CAMERA_STATE = 1;
    public static final int FRAGMENT_SWITCH_STATE = 0;

    private Drawable currImage;

    private int internal_state = 0;


    public CameraBottomBar(Context context, AttributeSet aSet) {
        super(context, aSet);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getInternal_state() == CAMERA_STATE) {
                    // send photo event
                    EventBus.getDefault().post(new EventPhoto());
                } else {
                    // change to this one
                    EventBus.getDefault().post(new EventSwapFragment(getFragmentId()));
                }
            }
        });

        currImage = getIconImageView().getDrawable();
    }

    public void changeToCameraMode() {
        setInternal_state(CAMERA_STATE);
        getIconImageView().setImageDrawable(getContext().getResources().getDrawable(R.drawable.photo_camera));
        getIconImageView().setScaleX(2);
        getIconImageView().setScaleY(2);
        getTitleTextView().setVisibility(View.GONE);
    }

    public void changeToFragmentSwitchMode() {
        setInternal_state(FRAGMENT_SWITCH_STATE);
        getIconImageView().setImageDrawable(currImage);
        getIconImageView().setScaleX(1);
        getIconImageView().setScaleY(1);
        getTitleTextView().setVisibility(View.VISIBLE);
    }

    @Override
    public void select() {
        changeToCameraMode();
    }

    @Override
    public void unselect() {
        changeToFragmentSwitchMode();
    }

    @Subscribe
    public void onScanFragmentEvent(EventScanFragment event) {
        switch (event.event) {
            case EventScanFragment.FRAGMENT_ENTERED:
                changeToCameraMode();
                break;
            case EventScanFragment.FRAGMENT_EXITED:
                changeToFragmentSwitchMode();
                break;
        }
    }

    public int getInternal_state() {
        return internal_state;
    }

    public void setInternal_state(int internal_state) {
        this.internal_state = internal_state;
    }
}
