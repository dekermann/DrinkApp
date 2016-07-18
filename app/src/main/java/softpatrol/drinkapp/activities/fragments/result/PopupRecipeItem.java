package softpatrol.drinkapp.activities.fragments.result;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import softpatrol.drinkapp.R;
import softpatrol.drinkapp.model.dto.PartWrapper;

/**
 * Created by root on 7/18/16.
 */
public class PopupRecipeItem extends LinearLayout {


    private PartWrapper partWrapper;

    private TextView twName;
    private TextView twUnitAndQuantity;


    private ImageButton ibShoppingCart;


    public PopupRecipeItem(Context context) {
        super(context);
        initInflate();
    }

    private void initInflate() {
        inflate(getContext(),R.layout.fragment_result_popup_ingredient_item,this);

        // set layout parmas
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0,0,0,20);
        setLayoutParams(lp);

        twName = (TextView) findViewById(R.id.fragment_result_popup_ingredient_item_name);
        twUnitAndQuantity = (TextView) findViewById(R.id.fragment_result_popup_ingredient_item_extra);
        ibShoppingCart =  (ImageButton) findViewById(R.id.fragment_result_popup_ingredient_item_img_shopping);
    }

    public ImageButton getIbShoppingCart() {
        return ibShoppingCart;
    }

    public TextView getTwUnitAndQuantity() {
        return twUnitAndQuantity;
    }

    public TextView getTwName() {
        return twName;
    }

    public void setPartWrapper(PartWrapper pw) {
        this.partWrapper = pw;
        twName.setText(pw.name);
        twUnitAndQuantity.setText(pw.quantity + " " + pw.type);

        switch(pw.status) {
            case PartWrapper.ADDED_TO_CART:
                setBackgroundColor(getContext().getResources().getColor(R.color.light_green));
                ibShoppingCart.setVisibility(View.GONE);
            case PartWrapper.HAVE_IT:
                setBackgroundColor(getContext().getResources().getColor(R.color.light_green));
                ibShoppingCart.setVisibility(View.GONE);
                break;
            case PartWrapper.MISSING:
                setBackgroundColor(getContext().getResources().getColor(R.color.LightPink));
                ibShoppingCart.setVisibility(View.VISIBLE);
                break;
        }
    }
}
