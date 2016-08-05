package softpatrol.drinkapp.layout.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
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
    private ImageView randomImage;

    private int internal_state = 0;


    public CameraBottomBar(Context context, AttributeSet aSet) {
        super(context, aSet);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getInternal_state() == CAMERA_STATE) {
                    // send photo event

                    final float growTo = 1f;
                    final long duration = 250;

                    ScaleAnimation shrink = new ScaleAnimation(growTo, 0, growTo, 0,
                            Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f);
                    shrink.setDuration(duration);
                    shrink.setStartOffset(duration / 2);
                    AnimationSet growAndShrink = new AnimationSet(true);
                    growAndShrink.setInterpolator(new LinearInterpolator());
                    growAndShrink.addAnimation(shrink);
                    getIconImageView().startAnimation(growAndShrink);

                    EventBus.getDefault().post(new EventPhoto());
                } else {
                    // change to this one
                    EventBus.getDefault().post(new EventSwapFragment(getFragmentId()));
                }
            }
        });

        currImage = getIconImageView().getDrawable();
    }

    public void setRootView(ImageView someView) {
        randomImage = someView;
        randomImage.setVisibility(View.VISIBLE);
    }

    public void changeToCameraMode() {
        setInternal_state(CAMERA_STATE);
        getIconImageView().setImageDrawable(getContext().getResources().getDrawable(R.drawable.camera_icon));
        getIconImageView().setScaleX(2f);
        getIconImageView().setScaleY(2f);


        /*
        final CameraBottomBar self = this;
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);

                int[] locations = new int[2];
                getLocationOnScreen(locations);
                int x = locations[0];
                int y = locations[1];
                ViewGroup parent = (ViewGroup) self;
                parent.removeView(self.getIconImageView());
                ViewGroup rootParent = (ViewGroup) getRootView();
                rootParent.addView(self.getIconImageView());
            }
        });
        */

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
