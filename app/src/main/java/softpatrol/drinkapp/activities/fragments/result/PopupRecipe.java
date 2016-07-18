package softpatrol.drinkapp.activities.fragments.result;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import softpatrol.drinkapp.R;
import softpatrol.drinkapp.database.models.recipe.PartCategory;
import softpatrol.drinkapp.database.models.recipe.PartIngredient;
import softpatrol.drinkapp.model.dto.PartWrapper;

/**
 * Created by root on 7/17/16.
 */
public class PopupRecipe extends RelativeLayout {

    private TextView title;
    private ImageView image;
    private TextView body;
    private LinearLayout ingredientsList;
    private TextView missing;
    private TextView time;
    private TextView level;



    public PopupRecipe(Context context, ResultViewItem item) {
        super(context);
        init(item);
    }

    private void init(ResultViewItem item) {
        inflate(getContext(),R.layout.fragment_result_recipe_popup,this);

        image = (ImageView) findViewById(R.id.fragment_result_recipe_popup_image);
        body = (TextView) findViewById(R.id.fragment_result_recipe_popup_body);
        body.setText(item.getRecipe().getBody());
        title = (TextView) findViewById(R.id.fragment_result_recipe_popup_title);
        title.setText(item.getRecipe().getName());
        missing = (TextView) findViewById(R.id.fragment_result_recipe_popup_missing);
        missing.setText(item.getResult().getTotalMisses() + "");
        time = (TextView) findViewById(R.id.fragment_result_recipe_popup_time);
        time.setText("10 min");
        level = (TextView) findViewById(R.id.fragment_result_recipe_popup_level);
        level.setText("Novice");


        ingredientsList = (LinearLayout) findViewById(R.id.fragment_result_recipe_popup_ingredient_item);

        for (PartCategory pc : item.getRecipe().getPartCategories()) {
            PartWrapper pw = PartWrapper.create(pc,PartWrapper.MISSING);
            PopupRecipeItem pri = new PopupRecipeItem(getContext());
            pri.setPartWrapper(pw);
            ingredientsList.addView(pri);
        }

        for (PartIngredient pi : item.getRecipe().getPartIngredients()) {
            PartWrapper pw = PartWrapper.create(pi,PartWrapper.MISSING);
            PopupRecipeItem pri = new PopupRecipeItem(getContext());
            pri.setPartWrapper(pw);
            ingredientsList.addView(pri);
        }


        /*
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (PartWrapper pw : partWrappers) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0,0,0,20);
            LinearLayout itemLayout = (LinearLayout)inflater.inflate(R.layout.fragment_result_popup_ingredient_item,this,false);
            itemLayout.setLayoutParams(lp);

            ((TextView) itemLayout.findViewById(R.id.fragment_result_popup_ingredient_item_name)).setText(pw.name);
            ((TextView) itemLayout.findViewById(R.id.fragment_result_popup_ingredient_item_extra)).setText(pw.quantity + " " + pw.unit);

            ImageButton ib =  ((ImageButton) itemLayout.findViewById(R.id.fragment_result_popup_ingredient_item_img_shopping));

            switch (pw.status) {
                case PartWrapper.ADDED_TO_CART:
                    break;
                case PartWrapper.HAVE_IT:
                    ib.setVisibility(View.INVISIBLE);
                    itemLayout.setBackgroundColor(getResources().getColor(R.color.light_green));
                    break;
                case PartWrapper.MISSING:
                    ib.setVisibility(View.VISIBLE);
                    itemLayout.setBackgroundColor(getResources().getColor(R.color.LightPink));
                    break;
            }
            ingredientsList.addView(itemLayout);
        }
        */
    }

    public TextView getLevel() {
        return level;
    }

    public void setLevel(TextView level) {
        this.level = level;
    }

    public TextView getTime() {
        return time;
    }

    public void setTime(TextView time) {
        this.time = time;
    }

    public TextView getMissing() {
        return missing;
    }

    public void setMissing(TextView missing) {
        this.missing = missing;
    }

    public TextView getBody() {
        return body;
    }

    public void setBody(TextView body) {
        this.body = body;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }
}
