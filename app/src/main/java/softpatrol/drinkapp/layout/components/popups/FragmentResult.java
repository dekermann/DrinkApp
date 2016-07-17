package softpatrol.drinkapp.layout.components.popups;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import softpatrol.drinkapp.R;
import softpatrol.drinkapp.database.models.recipe.PartCategory;
import softpatrol.drinkapp.database.models.recipe.PartIngredient;
import softpatrol.drinkapp.layout.components.PopUp;

/**
 * Created by root on 7/17/16.
 */
public class FragmentResult extends PopUp {

    private TextView title;
    private ImageView image;
    private TextView body;
    private RecyclerView ingredientsList;
    private TextView missing;
    private TextView time;
    private TextView level;
    private Button ok;


    public FragmentResult(Context context) {
        super(context);
        ArrayList<PartIngredient> pil = new ArrayList<>();
        ArrayList<PartCategory> pcl = new ArrayList<>();
        init("Unkown title","Unkown body","-1","-1","-1",pcl,pil);
    }

    public FragmentResult(Context context,String strTitle,String strBody,String strMissing,String strTime,String strLevel,List<PartCategory> partCategoryList, List<PartIngredient> partIngredientList) {
        super(context);
        init(strTitle,strBody,strMissing,strTime,strLevel,partCategoryList,partIngredientList);
    }

    private void init(String strTitle, String strBody, String strMissing, String strTime, String strLevel, List<PartCategory> partCategoryList, List<PartIngredient> partIngredientList) {
        final FragmentResult self = this;

        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View menuLayout = inflater.inflate(R.layout.fragment_result_recipe_popup, this, false);
        ((RelativeLayout) findViewById(R.id.popup_content)).addView(menuLayout);

        image = (ImageView) menuLayout.findViewById(R.id.fragment_result_recipe_popup_image);
        body = (TextView) menuLayout.findViewById(R.id.fragment_result_recipe_popup_body);
        body.setText(strBody);
        title = (TextView) menuLayout.findViewById(R.id.fragment_result_recipe_popup_title);
        title.setText(strTitle);
        ingredientsList = (RecyclerView) menuLayout.findViewById(R.id.fragment_result_recipe_popup_list_view);
        missing = (TextView) menuLayout.findViewById(R.id.fragment_result_recipe_popup_missing);
        missing.setText(strMissing);
        time = (TextView) menuLayout.findViewById(R.id.fragment_result_recipe_popup_time);
        time.setText(strTime);
        level = (TextView) menuLayout.findViewById(R.id.fragment_result_recipe_popup_level);
        level.setText(strLevel);

        ok = (Button) menuLayout.findViewById(R.id.fragment_result_filter_recipe_ok_btn);
        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                self.close();
            }
        });


        List<PartWrapper> partWrappers = new ArrayList<>();

        for (PartCategory pc : partCategoryList) {

            PartWrapper pw = new PartWrapper(pc.getCategoryName(),pc.getUnitName(),pc.getQuantity());
            partWrappers.add(pw);
        }

        for (PartIngredient pi : partIngredientList) {
            PartWrapper pw = new PartWrapper(pi.getIngredientId() +"",pi.getUnitName(),pi.getQuantity());
            partWrappers.add(pw);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        ingredientsList.setNestedScrollingEnabled(false);
        ingredientsList.setLayoutManager(linearLayoutManager);
        ingredientsList.setItemAnimator(new DefaultItemAnimator());
        ingredientsList.setAdapter(new ResultRecipeAdapter(partWrappers));
    }

    public RecyclerView getIngredientsList() {
        return ingredientsList;
    }

    public void setIngredientsList(RecyclerView ingredientsList) {
        this.ingredientsList = ingredientsList;
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

    public Button getOk() {
        return ok;
    }

    public void setOk(Button ok) {
        this.ok = ok;
    }


    /*
    *
    *
    *  INNER CLASSES
    *
    *
     */

    private class PartWrapper {
        public String name;
        public String unit;
        public float quanitity;

        public PartWrapper(String name,String unit,float quantity) {
            this.name = name;
            this.unit = unit;
            this.quanitity = quantity;
        }
    }

    class ResultRecipeAdapter extends RecyclerView.Adapter<ResultRecipeAdapter.MyViewHolder> {

        private List<PartWrapper> partWrapperList;

        public ResultRecipeAdapter(List<PartWrapper> partWrapperList) {
            this.partWrapperList = partWrapperList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_result_popup_ingredient_item, parent, false);

            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            PartWrapper pw = partWrapperList.get(position);

            holder.getIngredientName().setText(pw.name);
            holder.getIngredientExtra().setText(pw.quanitity + " " + pw.unit);
        }

        @Override
        public int getItemCount() {
            return partWrapperList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView ingredientName;
            private TextView ingredientExtra;

            public MyViewHolder(View itemView) {
                super(itemView);

                this.ingredientName = (TextView) itemView.findViewById(R.id.fragment_result_popup_ingredient_item_name);
                this.ingredientExtra = (TextView) itemView.findViewById(R.id.fragment_result_popup_ingredient_item_extra);
            }

            public TextView getIngredientExtra() {
                return ingredientExtra;
            }

            public TextView getIngredientName() {
                return ingredientName;
            }
        }

    }

}
