package softpatrol.drinkapp.activities.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import softpatrol.drinkapp.R;
import softpatrol.drinkapp.activities.fragments.result.ResultFragment;
import softpatrol.drinkapp.activities.fragments.scan.Manual;
import softpatrol.drinkapp.activities.fragments.scan.Scanner;
import softpatrol.drinkapp.activities.fragments.stash.StashFragment;
import softpatrol.drinkapp.database.DatabaseHandler;
import softpatrol.drinkapp.database.models.ingredient.Ingredient;
import softpatrol.drinkapp.database.models.stash.Stash;
import softpatrol.drinkapp.layout.components.CustomViewPager;
import softpatrol.drinkapp.model.event.ChangeCurrentStashEvent;
import softpatrol.drinkapp.model.event.EditCurrentStashEvent;

/**
 * David was here on 2016-06-08!
 * Copy this code for instant regret...
 */
public class ScanFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    int fragmentId;
    View rootView;
    private CustomViewPager mViewPager;
    com.sothree.slidinguppanel.SlidingUpPanelLayout mScrollView;
    private SectionsPagerAdapter mSectionsPagerAdapter;


    private void clearList() {
        StashFragment.CURRENT_STASH = new Stash();
        StashFragment.CURRENT_STASH.setName("");
        mCurrentStashName.setHint("New Stash");
        StashFragment.CURRENT_STASH_ID = -1;
        ((TestAdapter)mScannedItems.getAdapter()).clearItems();
        DatabaseHandler db = DatabaseHandler.getInstance(getContext());
        for(long l : StashFragment.CURRENT_STASH.getIngredientsIds()) {
            ((TestAdapter)mScannedItems.getAdapter()).addItems(db.getIngredient(l));
        }
        mCurrentStashName.setText(StashFragment.CURRENT_STASH.getName());
        EventBus.getDefault().post(new EditCurrentStashEvent());
    }

    boolean CurrentStashNameFocused = false;
    private void changeStashNameState() {
        CurrentStashNameFocused = !CurrentStashNameFocused;
        Log.d("CurrentStashNameFocused", CurrentStashNameFocused + "");
        if(!CurrentStashNameFocused) {
            mScannedItems.requestFocus();
            hideSoftKeyboard();
        }
    }
    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    public ScanFragment() {}
    public static ScanFragment newInstance(int sectionNumber) {
        ScanFragment fragment = new ScanFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    EditText mCurrentStashName;
    ImageView mScannerButton;
    ImageView mManualButton;
    boolean mScanning = true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentId = getArguments().getInt(ARG_SECTION_NUMBER);
        View rootView = inflater.inflate(R.layout.fragment_scan, container, false);
        this.rootView = rootView;
        mViewPager = (CustomViewPager) rootView.findViewById(R.id.fragment_scan_container);
        mScannerButton = (ImageView) rootView.findViewById(R.id.scanner);
        mManualButton = (ImageView) rootView.findViewById(R.id.manual);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mScrollView = (com.sothree.slidinguppanel.SlidingUpPanelLayout) rootView.findViewById(R.id.scrollView);
        mScrollView.setOverlayed(true);
        mScrollView.setShadowHeight(0);
        mScrollView.setTouchEnabled(true);
        mScrollView.setPanelHeight(375);
        mScrollView.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    mCurrentStashName.setFocusable(false);
                    mCurrentStashName.setFocusableInTouchMode(false);
                    mCurrentStashName.setClickable(false);

                } else if(newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    mCurrentStashName.setFocusable(true);
                    mCurrentStashName.setFocusableInTouchMode(true);
                    mCurrentStashName.setClickable(true);
                }
                Log.d("ASD", "State changed: " + newState.toString());
            }
        });
        mScannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan();
            }
        });
        mManualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manual();
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0) scan();
                else manual();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        final ImageView clear = (ImageView) rootView.findViewById(R.id.clear);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearList();
            }
        });

        mScannedItems = (RecyclerView) rootView.findViewById(R.id.scanned_item_list);
        mScannedItems.requestFocus();
        mCurrentStashName = (EditText) rootView.findViewById(R.id.fragment_scan_stash_name);
        mCurrentStashName.setSelected(false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        
        //Typeface type = Typeface.createFromAsset(getContext().getAssets(),"fonts/font.ttf");
        //mCurrentStashName.setTypeface(type);
        setUpRecyclerView();
        DatabaseHandler db = DatabaseHandler.getInstance(getContext());
        for(long l : StashFragment.CURRENT_STASH.getIngredientsIds()) {
            ((TestAdapter)mScannedItems.getAdapter()).addItems(db.getIngredient(l));
        }


/*
        mCurrentStashName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });*/
        mCurrentStashName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mCurrentStashName.setHint("");
                if(!CurrentStashNameFocused) changeStashNameState();
            }
        });

        mCurrentStashName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                StashFragment.CURRENT_STASH.setName(mCurrentStashName.getText().toString());
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mCurrentStashName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event == null) {
                    changeStashNameState();
                    return true;
                }
                return false;
            }
        });

        return rootView;
    }

    private void scan() {
        if(mScanning) return;
        else {
            mScannerButton.setBackground(getResources().getDrawable(R.drawable.fragment_scan_scan_button_background));
            mScannerButton.setImageDrawable(getResources().getDrawable(R.drawable.scan_white));
            mManualButton.setImageDrawable(getResources().getDrawable(R.drawable.manual_black));
            mManualButton.setBackground(null);
            mScanning = !mScanning;
            mViewPager.setCurrentItem(0);
        }
    }
    private void manual() {
        if(!mScanning) return;
        else {
            mManualButton.setBackground(getResources().getDrawable(R.drawable.fragment_scan_manual_button_background));
            mManualButton.setImageDrawable(getResources().getDrawable(R.drawable.manual_white));
            mScannerButton.setImageDrawable(getResources().getDrawable(R.drawable.scan_black));
            mScannerButton.setBackground(null);
            mScanning = !mScanning;
            mViewPager.setCurrentItem(1);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        Scanner scannerFragment;
        Manual scannerManual;
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            scannerFragment = Scanner.newInstance(0);
            scannerManual = Manual.newInstance(1);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case 0: return scannerFragment;
                case 1: return scannerManual;
                default: return null;
            }
        }
        @Override
        public int getCount() { return 2; }

    }

    //----- SCANNED LIST -----
    public static RecyclerView mScannedItems;
    private LinearLayoutManager mLinearLayourManager;
    private void setUpRecyclerView() {
        mLinearLayourManager = new LinearLayoutManager(getContext());
        mScannedItems.setLayoutManager(mLinearLayourManager);
        mScannedItems.setAdapter(new TestAdapter());
        mScannedItems.setHasFixedSize(true);
        setUpItemTouchHelper();
        setUpAnimationDecoratorHelper();
    }

    private boolean listIsAtTop()   {
        return (mLinearLayourManager.findFirstVisibleItemPosition() == 0);
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
                TestAdapter adapter = (TestAdapter) mScannedItems.getAdapter();
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
        mItemTouchHelper.attachToRecyclerView(mScannedItems);
    }

    /**
     * We're gonna setup another ItemDecorator that will draw the red background in the empty space while the items are animating to their new positions
     * after an item is removed.
     */
    private void setUpAnimationDecoratorHelper() {
        mScannedItems.addItemDecoration(new RecyclerView.ItemDecoration() {

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
    public class TestAdapter extends RecyclerView.Adapter {

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
            if(listIsAtTop()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mScannedItems.scrollToPosition(0);
                        notifyItemInserted(0);
                    }
                });
            }
            else notifyItemInserted(0);
            StashFragment.CURRENT_STASH.addIngredientId(ingredient.getId());
            DatabaseHandler db = DatabaseHandler.getInstance(getContext());
            db.updateStash(StashFragment.CURRENT_STASH);
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
                StashFragment.CURRENT_STASH.removeIngredientId(item.getServerId());
                notifyItemRemoved(position);
                DatabaseHandler db = DatabaseHandler.getInstance(getContext());
                db.updateStash(StashFragment.CURRENT_STASH);
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
            //Typeface type = Typeface.createFromAsset(parent.getContext().getAssets(),"fonts/font.ttf");
            //titleTextView.setTypeface(type);
        }

    }
    /*
    * Messaging service between stuff
     */

    @Subscribe
    public void onCurrentStashEvent(ChangeCurrentStashEvent stashEvent) {
        ((TestAdapter)mScannedItems.getAdapter()).clearItems();
        DatabaseHandler db = DatabaseHandler.getInstance(getContext());
        for(long l : StashFragment.CURRENT_STASH.getIngredientsIds()) {
            ((TestAdapter)mScannedItems.getAdapter()).addItems(db.getIngredient(l));
        }
        mCurrentStashName.setText(StashFragment.CURRENT_STASH.getName());
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onEntered() {
        if(CurrentStashNameFocused) changeStashNameState();
        mCurrentStashName.setHint("New Stash");
    }
}