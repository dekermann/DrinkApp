package softpatrol.drinkapp.layout.components.popups;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import softpatrol.drinkapp.R;
import softpatrol.drinkapp.activities.fragments.StashFragment;
import softpatrol.drinkapp.database.DatabaseHandler;
import softpatrol.drinkapp.database.models.ingredient.Ingredient;
import softpatrol.drinkapp.layout.components.PopUp;
import softpatrol.drinkapp.model.event.ChangeCurrentStashEvent;
import softpatrol.drinkapp.model.event.EventSwapFragment;

/**
 * Created by MonsterMaskinen on 2016-06-08.
 */

public class Stash extends PopUp {
    private TextView name;
    private Button edit;
    private Button share;
    private Button search;
    private Button delete;
    public RecyclerView stash_item_list;
    softpatrol.drinkapp.database.models.stash.Stash stash;

    public Stash(Context context) {
        super(context);
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View menuLayout = inflater.inflate(R.layout.popup_stash, this, false);
        ((RelativeLayout) findViewById(R.id.popup_content)).addView(menuLayout);
        this.name = (TextView)findViewById(R.id.name);
        this.edit = (Button)findViewById(R.id.edit);
        edit.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    edit.setBackground(getContext().getResources().getDrawable(R.drawable.popup_stash_button_pressed));
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    edit.setBackground(getContext().getResources().getDrawable(R.drawable.popup_stash_button));
                }
                return false;
            }
        });
        this.edit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
                EventBus.getDefault().post(new EventSwapFragment(2));
            }
        });
        this.share = (Button)findViewById(R.id.share);
        share.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    share.setBackground(getContext().getResources().getDrawable(R.drawable.popup_stash_button_pressed));
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    share.setBackground(getContext().getResources().getDrawable(R.drawable.popup_stash_button));
                }
                return false;
            }
        });
        this.search = (Button)findViewById(R.id.search);
        search.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    search.setBackground(getContext().getResources().getDrawable(R.drawable.popup_stash_button_pressed));
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    search.setBackground(getContext().getResources().getDrawable(R.drawable.popup_stash_button));
                }
                return false;
            }
        });
        this.search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
                EventBus.getDefault().post(new EventSwapFragment(3));
            }
        });
        this.delete = (Button)findViewById(R.id.delete);
        delete.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    delete.setBackground(getContext().getResources().getDrawable(R.drawable.popup_stash_button_pressed));
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    delete.setBackground(getContext().getResources().getDrawable(R.drawable.popup_stash_button));
                }
                return false;
            }
        });
        this.delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHandler db = DatabaseHandler.getInstance(getContext());
                Log.d("DELETE STASH", "RETURNS: " + db.deleteStash(stash));
                StashFragment.CURRENT_STASH = new softpatrol.drinkapp.database.models.stash.Stash();
                EventBus.getDefault().post(new ChangeCurrentStashEvent());
                defaultClose();
            }
        });
        this.stash_item_list  = (RecyclerView) findViewById(R.id.stash_item_list);
        setUpRecyclerView();
    }

    public void bindStash(softpatrol.drinkapp.database.models.stash.Stash stash) {
        this.stash = stash;
        this.name.setText(stash.getName());

        DatabaseHandler db = DatabaseHandler.getInstance(getContext());
        for(long l : stash.getIngredientsIds()) {
            ((TestAdapter)stash_item_list.getAdapter()).addItems(db.getIngredient(l));
        }

    }
    public void defaultClose() {
        super.close();
    }

    @Override
    public void close() {
        DatabaseHandler db = DatabaseHandler.getInstance(getContext());
        db.updateStash(stash);
        StashFragment.CURRENT_STASH = stash;
        EventBus.getDefault().post(new ChangeCurrentStashEvent());
        super.close();
    }

    public void setName(String string) {
        name.setText(string);
    }


    //----- SCANNED LIST -----
    private LinearLayoutManager mLinearLayourManager;
    private void setUpRecyclerView() {
        mLinearLayourManager = new LinearLayoutManager(getContext());
        stash_item_list.setLayoutManager(mLinearLayourManager);
        stash_item_list.setAdapter(new TestAdapter());
        stash_item_list.setHasFixedSize(true);
        setUpItemTouchHelper();
        setUpAnimationDecoratorHelper();
    }

    /**
     * This is the standard support library way of implementing "swipe to delete" feature. You can do custom drawing in onChildDraw method
     * but whatever you draw will disappear once the swipe is over, and while the items are animating to their new position the recycler view
     * background will be visible. That is rarely an desired effect.
     */
    private void setUpItemTouchHelper() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.TRANSPARENT);
                xMark = ContextCompat.getDrawable(getContext(), R.drawable.ic_clear_24dp);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) getContext().getResources().getDimension(R.dimen.ic_clear_margin);
                initiated = true;
            }

            // not important, we don't want drag & drop
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                TestAdapter testAdapter = (TestAdapter)recyclerView.getAdapter();
                if (testAdapter.isUndoOn() && testAdapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                TestAdapter adapter = (TestAdapter) stash_item_list.getAdapter();
                boolean undoOn = adapter.isUndoOn();
                if (undoOn) {
                    adapter.pendingRemoval(swipedPosition);
                } else {
                    adapter.remove(swipedPosition);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }

                if (!initiated) {
                    init();
                }

                // draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                //xMark.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(stash_item_list);
    }

    /**
     * We're gonna setup another ItemDecorator that will draw the red background in the empty space while the items are animating to their new positions
     * after an item is removed.
     */
    private void setUpAnimationDecoratorHelper() {
        stash_item_list.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.TRANSPARENT);
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

                if (!initiated) {
                    init();
                }

                // only if animation is in progress
                if (parent.getItemAnimator().isRunning()) {

                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    // this is fixed
                    int left = 0;
                    int right = parent.getWidth();

                    // this we need to find out
                    int top = 0;
                    int bottom = 0;

                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }

        });
    }

    /**
     * RecyclerView adapter enabling undo on a swiped away item.
     */
    class TestAdapter extends RecyclerView.Adapter {

        private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec

        List<Ingredient> items;
        List<Ingredient> itemsPendingRemoval;
        boolean undoOn; // is undo on, you can turn it on from the toolbar menu

        private Handler handler = new Handler(); // hanlder for running delayed runnables
        HashMap<Ingredient, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be

        public TestAdapter() {
            items = new ArrayList<>();
            itemsPendingRemoval = new ArrayList<>();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TestViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TestViewHolder viewHolder = (TestViewHolder)holder;
            final String item = items.get(position).getName();

            if (itemsPendingRemoval.contains(item)) {
                // we need to show the "undo" state of the row
                viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
                viewHolder.titleTextView.setVisibility(View.GONE);
            } else {
                // we need to show the "normal" state
                viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
                viewHolder.titleTextView.setVisibility(View.VISIBLE);
                viewHolder.titleTextView.setText(item);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        /**
         *  Utility method to add some rows for testing purposes. You can add rows from the toolbar menu.
         */
        public void addItems(Ingredient ingredient) {
            Log.d("ADAPTED", "Adding item: " + ingredient.getName());
            for(Ingredient scannedItem : items) if(scannedItem.getServerId() == ingredient.getServerId()) return; //Can only scan same type once
            items.add(0, ingredient);
            notifyItemInserted(0);
        }

        public void clearItems() {
            for(int i = 0;i<items.size();) {
                items.remove(i);
                notifyItemRemoved(i);
            }
        }

        public void setUndoOn(boolean undoOn) {
            this.undoOn = undoOn;
        }

        public boolean isUndoOn() {
            return undoOn;
        }

        public void pendingRemoval(int position) {
            final Ingredient item = items.get(position);
            if (!itemsPendingRemoval.contains(item)) {
                itemsPendingRemoval.add(item);
                // this will redraw row in "undo" state
                notifyItemChanged(position);
                // let's create, store and post a runnable to remove the item
                Runnable pendingRemovalRunnable = new Runnable() {
                    @Override
                    public void run() {
                        remove(items.indexOf(item));
                    }
                };
                handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
                pendingRunnables.put(item, pendingRemovalRunnable);
            }
        }

        public void remove(int position) {
            Ingredient item = items.get(position);
            if (itemsPendingRemoval.contains(item)) {
                itemsPendingRemoval.remove(item);
            }
            if (items.contains(item)) {
                items.remove(position);
                stash.removeIngredientId(item.getServerId());
                notifyItemRemoved(position);
            }
        }

        public boolean isPendingRemoval(int position) {
            Ingredient item = items.get(position);
            return itemsPendingRemoval.contains(item);
        }
    }

    /**
     * ViewHolder capable of presenting two states: "normal" and "undo" state.
     */
    static class TestViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;

        public TestViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.scanned_item, parent, false));
            titleTextView = (TextView) itemView.findViewById(R.id.title_text_view);
            Typeface type = Typeface.createFromAsset(parent.getContext().getAssets(),"fonts/font.ttf");
            titleTextView.setTypeface(type);
        }

    }

}
