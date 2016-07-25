
package softpatrol.drinkapp.activities.fragments.result;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import softpatrol.drinkapp.R;
import softpatrol.drinkapp.api.Analyzer;
import softpatrol.drinkapp.api.Definitions;
import softpatrol.drinkapp.api.Getter;
import softpatrol.drinkapp.database.models.recipe.PartCategory;
import softpatrol.drinkapp.database.models.recipe.PartIngredient;
import softpatrol.drinkapp.database.models.recipe.Recipe;
import softpatrol.drinkapp.model.dto.PartWrapper;
import softpatrol.drinkapp.model.dto.SearchResult;
import softpatrol.drinkapp.model.event.EventRecipe;

/**
 * Created by root on 7/17/16.
 */
public class PopupRecipe extends RelativeLayout {

    private TextView textViewTitle;
    private ImageView imgViewTitle;
    private TextView txtViewBody;
    private LinearLayout layoutIngredientsList;
    private TextView txtViewMissing;
    private TextView txtViewTime;
    private TextView txtViewLevel;
    private Button btnShowAll;
    private Button btnShowMissing;

    private SearchResult item;

    public PopupRecipe(Context context, SearchResult item) {
        super(context);
        init();
        this.item = item;

        // search for the recipe
        new Getter(new ResultParser(this.getContext())).execute(Definitions.GET_RECIPES + "/" + item.getRecipeId());
    }

    public PopupRecipe(Context context,SearchResult item,Recipe recipe) {
        super(context);
        init();
        this.item = item;
    }

    public void populateGui(SearchResult result,Recipe recipe) {
        textViewTitle.setText(recipe.getName());
        txtViewBody.setText(recipe.getBody());

        // Loop over all parts and check what is missing or added in the cart
        for (PartIngredient pi : recipe.getPartIngredients()) {
            PartWrapper pw = null;

            // check if it is a miss
            if (item.getIngredMisses().contains(pi.getIngredientId())) {
                pw = PartWrapper.create(pi, PartWrapper.ItemStatus.MISSING,getContext());
            } else {
                pw = PartWrapper.create(pi, PartWrapper.ItemStatus.HAVE_IT,getContext());
            }
            addPart(pw);
        }

        for (PartCategory pc : recipe.getPartCategories()) {
            PartWrapper pw = null;
            // check for hit here, since server returns category hits
            if (item.getCategoryHits().contains(pc.getCategoryServerId())) {
                pw = PartWrapper.create(pc, PartWrapper.ItemStatus.HAVE_IT,getContext());
            } else {
                pw = PartWrapper.create(pc, PartWrapper.ItemStatus.MISSING,getContext());
            }
            addPart(pw);
        }
    }

    private void addPart(PartWrapper pw) {
        PopupRecipeItem popupRecipeItem = new PopupRecipeItem(getContext());
        popupRecipeItem.setPartWrapper(pw);
        layoutIngredientsList.addView(popupRecipeItem);
    }

    public void close() {
        EventBus.getDefault().unregister(this);
    }

    private void init() {
        final PopupRecipe self = this;

        inflate(getContext(),R.layout.fragment_result_recipe_popup,this);

        imgViewTitle = (ImageView) findViewById(R.id.fragment_result_recipe_popup_image);
        txtViewBody = (TextView) findViewById(R.id.fragment_result_recipe_popup_body);
        textViewTitle = (TextView) findViewById(R.id.fragment_result_recipe_popup_title);
        txtViewMissing = (TextView) findViewById(R.id.fragment_result_recipe_popup_missing);
        txtViewTime = (TextView) findViewById(R.id.fragment_result_recipe_popup_time);
        txtViewLevel = (TextView) findViewById(R.id.fragment_result_recipe_popup_level);

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

        EventBus.getDefault().register(this);
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

    public TextView getTextViewTitle() {
        return textViewTitle;
    }

    public void setTextViewTitle(TextView textViewTitle) {
        this.textViewTitle = textViewTitle;
    }

    private interface PopupRecipeItemIterate {
        void onPopupRecipeItem(PopupRecipeItem item);
    }

    class ResultParser extends Analyzer {

        public ResultParser(Context context) {
            super(context);
        }

        @Override
        public void analyzeData(JSONObject result) throws Exception {
            JSONObject data = result.getJSONObject("data");
            Recipe recipe = new Recipe(data);
            EventBus.getDefault().post(new EventRecipe(recipe));
        }
    }

    @Subscribe
    public void onRecipeData(EventRecipe event) {
        populateGui(item,event.recipe);
    }
}
