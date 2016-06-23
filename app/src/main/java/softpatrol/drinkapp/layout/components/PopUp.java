package softpatrol.drinkapp.layout.components;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import softpatrol.drinkapp.R;
import softpatrol.drinkapp.database.models.stash.Stash;

/**
 * Created by MonsterMaskinen on 2016-06-08.
 */

public class PopUp extends RelativeLayout {

    ImageView cancelButton;
    RelativeLayout parent;

    public PopUp(Context context) {
        super(context);
        inflate(getContext(), R.layout.popup, this);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Absorb Clicks
            }
        });
        this.cancelButton = (ImageView) findViewById(R.id.cancelPopUp);
        cancelButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    cancelButton.setBackground(getContext().getResources().getDrawable(R.drawable.popup_cancel_icon_background_pressed));
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    cancelButton.setBackground(getContext().getResources().getDrawable(R.drawable.popup_cancel_icon_background));
                }
                return false;
            }
        });
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.removeAllViews();
                parent.setVisibility(View.GONE);
                parent.invalidate();
            }
        });
    }

    public void setParent(RelativeLayout parent) {
        this.parent = parent;
    }
}
