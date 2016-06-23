package softpatrol.drinkapp.layout.components.popups;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import softpatrol.drinkapp.R;
import softpatrol.drinkapp.layout.components.PopUp;

/**
 * Created by MonsterMaskinen on 2016-06-08.
 */

public class Stash extends PopUp {
    private TextView name;

    public Stash(Context context) {
        super(context);
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View menuLayout = inflater.inflate(R.layout.popup_stash, this, false);
        ((RelativeLayout) findViewById(R.id.popup_content)).addView(menuLayout);
        this.name = (TextView)findViewById(R.id.name);
    }

    public void bindStash(softpatrol.drinkapp.database.models.stash.Stash stash) {
        this.name.setText(stash.getName());
    }

    public void setName(String string) {
        name.setText(string);
    }

}
