package softpatrol.drinkapp.layout.components;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import softpatrol.drinkapp.R;

/**
 * Created by MonsterMaskinen on 2016-06-08.
 */

public class StashView extends RelativeLayout {
    private TextView name;
    private ImageView icon;

    public StashView(Context context) {
        super(context);
        init();
    }

    public StashView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StashView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.item_stash, this);
        this.name = (TextView)findViewById(R.id.name);
        this.icon = (ImageView)findViewById(R.id.icon);
    }

    public void setIcon(Drawable drawable) {
        icon.setImageDrawable(drawable);
    }
    public void setName(String string) {
        name.setText(string);
    }
}
