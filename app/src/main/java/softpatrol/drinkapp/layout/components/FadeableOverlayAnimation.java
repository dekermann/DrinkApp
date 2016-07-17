package softpatrol.drinkapp.layout.components;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.transition.Fade;
import android.view.View;

import softpatrol.drinkapp.R;

/**
 * Created by rasmus on 6/25/16.
 */
public class FadeableOverlayAnimation {

    private View refView;


    public FadeableOverlayAnimation(View refView) {
        this.refView = refView;
    }

    public View getRefView() {
        return refView;
    }
}
