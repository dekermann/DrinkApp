package softpatrol.drinkapp.layout.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
    private ImageView moveableImage;

    private int internal_state = 0;


    public CameraBottomBar(Context context, AttributeSet aSet) {
        super(context, aSet);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getInternal_state() == CAMERA_STATE) {
                    // send photo event

                    /*
                    RotateAnimation rotate = new RotateAnimation(0, 360,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                            0.5f);

                    rotate.setDuration(1000);
                    rotate.setRepeatCount(Animation.ABSOLUTE);
                    moveableImage.startAnimation(rotate);
                    */

                    EventBus.getDefault().post(new EventPhoto());
                } else {
                    // change to this one
                    EventBus.getDefault().post(new EventSwapFragment(getFragmentId()));
                }
            }
        });

        currImage = getIconImageView().getDrawable();
    }

    public void setMoveableImageView(ImageView someView) {
        moveableImage = someView;
        moveableImage.setZ(100);
    }

    public void changeToCameraMode() {
        setInternal_state(CAMERA_STATE);
        getIconImageView().setImageDrawable(getContext().getResources().getDrawable(R.drawable.photo_camera));
        getIconImageView().setScaleX(1.5f);
        getIconImageView().setScaleY(1.5f);
        setBackgroundColor(getSelectBgColor());
        getTitleTextView().setVisibility(View.GONE);


        /*
        final CameraBottomBar self = this;
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);

                int[] locations = new int[2];
                getLocationOnScreen(locations);
                int x = locations[0];
                int y = locations[1];

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(200,200);
                params.leftMargin = x;
                params.topMargin = y-50;
                moveableImage.setLayoutParams(params);
            }
        });
        */
    }

    public void changeToFragmentSwitchMode() {
        setInternal_state(FRAGMENT_SWITCH_STATE);
        getIconImageView().setImageDrawable(currImage);
        getIconImageView().setScaleX(1);
        getIconImageView().setScaleY(1);
        setBackgroundColor(getUnselectBgColor());
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
