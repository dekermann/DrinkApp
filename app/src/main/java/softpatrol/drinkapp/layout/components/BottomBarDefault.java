package softpatrol.drinkapp.layout.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import softpatrol.drinkapp.model.event.EventSwapFragment;

/**
 * Created by root on 8/5/16.
 */
public class BottomBarDefault extends BottomBarItem {
    public BottomBarDefault(Context context, AttributeSet aSet) {
        super(context, aSet);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventSwapFragment(getFragmentId()));
            }
        });

    }
}
