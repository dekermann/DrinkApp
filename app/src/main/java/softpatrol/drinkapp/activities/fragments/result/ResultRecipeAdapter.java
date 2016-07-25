package softpatrol.drinkapp.activities.fragments.result;


import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import softpatrol.drinkapp.R;
import softpatrol.drinkapp.model.dto.SearchResult;
/**
 * Created by root on 7/17/16.
 */
class ResultRecipeAdapter extends RecyclerView.Adapter<ResultRecipeAdapter.MyViewHolder> {

    private List<SearchResult> dataSet;
    private Context ctx;

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titleText;
        TextView likeText;
        TextView commentText;

        SearchResult item;


        public MyViewHolder(View itemView, final Context ctx) {
            super(itemView);
            this.titleText = (TextView) itemView.findViewById(R.id.fragment_result_row_title_text);
            this.likeText = (TextView) itemView.findViewById(R.id.fragment_result_row_like_text);
            this.commentText = (TextView) itemView.findViewById(R.id.fragment_result_row_comments_text);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item != null) {
                        PopupRecipe popupRecipe = new PopupRecipe(ctx,item);

                        Dialog d = new MaterialDialog.Builder(ctx)
                                .customView(popupRecipe,false)
                                .positiveColor(ctx.getResources().getColor(R.color.DarkGreen))
                                .positiveText("Close")
                                .show();
                    } else {
                        Toast.makeText(ctx,"Could not open recipe, not loaded",Toast.LENGTH_LONG);
                    }
                }
            });
        }

        public void setItem(SearchResult item) {
            this.item = item;
        }

        public SearchResult getItem() {
            return item;
        }
    }

    public ResultRecipeAdapter(List<SearchResult> data,Context ctx) {
        this.dataSet = data;
        this.ctx = ctx;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_result_row, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view,ctx);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        SearchResult item = dataSet.get(listPosition);
        holder.setItem(item);

        holder.titleText.setText(item.getName());
        if (item.getTotalMisses() > 0) {
            holder.commentText.setText("Missing " + item.getTotalMisses() + " ingredient" + (item.getTotalMisses() == 1 ? "" : "s"));
        } else {
            holder.commentText.setText("Not missing any ingredients");
        }
        holder.likeText.setText("- likes");
    }

    public void addRecipe(SearchResult recipe) {
        dataSet.add(recipe);
        notifyItemInserted(dataSet.size()-1);
    }

    public void clearAndAddRecipes(List<SearchResult> items) {
        dataSet.clear();
        dataSet.addAll(items);
        notifyDataSetChanged();
    }

    public void clear() {
        dataSet.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}