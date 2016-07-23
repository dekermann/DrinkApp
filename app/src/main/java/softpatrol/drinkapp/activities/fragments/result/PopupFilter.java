package softpatrol.drinkapp.activities.fragments.result;

import android.content.Context;
import android.widget.GridView;
import android.widget.RelativeLayout;

import softpatrol.drinkapp.R;

/**
 * Created by rasmus on 7/23/16.
 */
public class PopupFilter extends RelativeLayout{

    private GridView layoutCategoryList;


    public PopupFilter(Context context) {
        super(context);

        inflate(getContext(), R.layout.fragment_result_filter_popup,this);

        layoutCategoryList = (GridView) findViewById(R.id.fragment_result_filter_include_categories);

    }
}
