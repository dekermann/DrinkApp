package softpatrol.drinkapp.layout.components;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import softpatrol.drinkapp.R;

/**
 * Created by MonsterMaskinen on 2016-06-08.
 */

public class ActionBarButton extends RelativeLayout {
    private String nameText;
    private TextView name;
    private ImageView icon;

    public ActionBarButton(Context context) {
        super(context);
        init();
    }

    public ActionBarButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ActionBarButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.action_bar_button, this);
        this.name = (TextView)findViewById(R.id.name);
        this.icon = (ImageView)findViewById(R.id.icon);
    }

    public void pressAnimation() {
        Animation anim = new ScaleAnimation(
                1f, 1.3f, // Start and end values for the X axis scaling
                1f, 1.3f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.6f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        this.startAnimation(anim);
        this.name.setText(nameText);
    }

    public void releaseAnimation() {
        Animation anim = new ScaleAnimation(
                1.3f, 1f, // Start and end values for the X axis scaling
                1.3f, 1f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.6f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        this.startAnimation(anim);
        this.name.setText("");
    }

    public void setIcon(Drawable drawable) {
        icon.setImageDrawable(drawable);
    }
    public void setName(String string) {
        nameText = string;
    }
}
