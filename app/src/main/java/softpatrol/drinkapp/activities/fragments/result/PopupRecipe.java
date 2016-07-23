
package softpatrol.drinkapp.activities.fragments.result;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import softpatrol.drinkapp.R;
import softpatrol.drinkapp.database.models.recipe.PartCategory;
import softpatrol.drinkapp.database.models.recipe.PartIngredient;
import softpatrol.drinkapp.database.models.recipe.Recipe;
import softpatrol.drinkapp.model.dto.PartWrapper;
import softpatrol.drinkapp.model.dto.SearchResult;

/**
 * Created by root on 7/17/16.
 */
public class PopupRecipe extends RelativeLayout {

    private TextView title;
    private ImageView imgViewTitle;
    private TextView txtViewBody;
    private LinearLayout layoutIngredientsList;
    private TextView txtViewMissing;
    private TextView txtViewTime;
    private TextView txtViewLevel;
    private Button btnShowAll;
    private Button btnShowMissing;

    public PopupRecipe(Context context, ResultViewItem item) {
        super(context);
        init(item);
    }

    private void init(ResultViewItem item) {
        final PopupRecipe self = this;

        inflate(getContext(),R.layout.fragment_result_recipe_popup,this);

        imgViewTitle = (ImageView) findViewById(R.id.fragment_result_recipe_popup_image);
        txtViewBody = (TextView) findViewById(R.id.fragment_result_recipe_popup_body);
        txtViewBody.setText(item.getRecipe().getBody());
        title = (TextView) findViewById(R.id.fragment_result_recipe_popup_title);
        title.setText(item.getRecipe().getName());
        txtViewMissing = (TextView) findViewById(R.id.fragment_result_recipe_popup_missing);
        txtViewMissing.setText(item.getResult().getTotalMisses() + "");
        txtViewTime = (TextView) findViewById(R.id.fragment_result_recipe_popup_time);
        txtViewTime.setText("10 min");
        txtViewLevel = (TextView) findViewById(R.id.fragment_result_recipe_popup_level);
        txtViewLevel.setText("Novice");

        btnShowAll = (Button) findViewById(R.id.fragment_result_recipe_popup_all_btn);
        btnShowAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                self.showAllIngredients();
            }
        });

        btnShowMissing = (Button) findViewById(R.id.fragment_result_recipe_popup_ingredient_missing_btn);
        btnShowMissing.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                self.showMissingIngredients();
            }
        });


        layoutIngredientsList = (LinearLayout) findViewById(R.id.fragment_result_recipe_popup_ingredient_item);
        SearchResult srs = item.getResult();
        Recipe r = item.getRecipe();

        for (PartCategory pc : item.getRecipe().getPartCategories()) {
            PartWrapper pw = PartWrapper.create(pc,PartWrapper.ItemStatus.HAVE_IT);

            PopupRecipeItem pri = new PopupRecipeItem(getContext());
            pri.setPartWrapper(pw);
            layoutIngredientsList.addView(pri);
        }

        for (PartIngredient pi : item.getRecipe().getPartIngredients()) {
            PartWrapper pw = PartWrapper.create(pi,PartWrapper.ItemStatus.HAVE_IT);
            PopupRecipeItem pri = new PopupRecipeItem(getContext());
            pri.setPartWrapper(pw);
            layoutIngredientsList.addView(pri);
        }
    }

    private void iteratePopupRecipeItem(PopupRecipeItemIterate iterate) {
        int length = layoutIngredientsList.getChildCount();

        for (int i = 0; i < length;i++) {
            View v = layoutIngredientsList.getChildAt(i);


            if (v instanceof PopupRecipeItem) {
                iterate.onPopupRecipeItem((PopupRecipeItem) v);
            }
        }
    }

    public void showAllIngredients() {
        iteratePopupRecipeItem(new PopupRecipeItemIterate() {
            @Override
            public void onPopupRecipeItem(PopupRecipeItem item) {
                item.setVisibility(VISIBLE);
            }
        });
    }

    public void showMissingIngredients() {
        iteratePopupRecipeItem(new PopupRecipeItemIterate() {
            @Override
            public void onPopupRecipeItem(PopupRecipeItem item) {
                if (item.getPartWrapper().status == PartWrapper.ItemStatus.MISSING) {
                    item.setVisibility(VISIBLE);
                } else {
                    item.setVisibility(GONE);
                }
            }
        });
    }

    public TextView getLevel() {
        return txtViewLevel;
    }

    public void setLevel(TextView level) {
        this.txtViewLevel = level;
    }

    public TextView getTxtViewTime() {
        return txtViewTime;
    }

    public void setTxtViewTime(TextView txtViewTime) {
        this.txtViewTime = txtViewTime;
    }

    public TextView getTxtViewMissing() {
        return txtViewMissing;
    }

    public void setTxtViewMissing(TextView txtViewMissing) {
        this.txtViewMissing = txtViewMissing;
    }

    public TextView getTxtViewBody() {
        return txtViewBody;
    }

    public void setTxtViewBody(TextView txtViewBody) {
        this.txtViewBody = txtViewBody;
    }

    public ImageView getImgViewTitle() {
        return imgViewTitle;
    }

    public void setImgViewTitle(ImageView imgViewTitle) {
        this.imgViewTitle = imgViewTitle;
    }

    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }

    private interface PopupRecipeItemIterate {
        void onPopupRecipeItem(PopupRecipeItem item);
    }
}
