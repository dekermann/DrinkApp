package softpatrol.drinkapp.activities.fragments.result;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import softpatrol.drinkapp.R;
import softpatrol.drinkapp.database.models.ingredient.Category;
import softpatrol.drinkapp.model.event.EventFilterPopupClose;

/**
 * Created by rasmus on 7/23/16.
 */
public class PopupFilter extends RelativeLayout{

    private GridView layoutCategoryList;
    private SeekBar  seekBarMatchPercent;
    private TextView txtViewMatchPercent;
    private Spinner spinnerSortBy;
    private Button btnApply;

    private FilterSettings fsSettings;


    public PopupFilter(Context context,FilterSettings filterSettings) {
        super(context);

        fsSettings = filterSettings.clone();

        inflate(getContext(), R.layout.fragment_result_filter_popup,this);

        layoutCategoryList = (GridView) findViewById(R.id.fragment_result_filter_include_categories);

        txtViewMatchPercent = (TextView) findViewById(R.id.fragment_result_filter_slider_percent_text);

        spinnerSortBy = (Spinner) findViewById(R.id.fragment_result_filter_sortby_spinner);
        final ArrayAdapter<FilterSettings.FilterSortBy> dataAdapter = new ArrayAdapter<FilterSettings.FilterSortBy>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, FilterSettings.FilterSortBy.values());
        spinnerSortBy.setAdapter(dataAdapter);
        spinnerSortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                fsSettings.sortBy = dataAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        spinnerSortBy.setSelection(dataAdapter.getPosition(fsSettings.sortBy),true);

        btnApply = (Button) findViewById(R.id.fragment_result_filter_popup_apply_btn);
        btnApply.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new EventFilterPopupClose(fsSettings));
            }
        });

        seekBarMatchPercent = (SeekBar) findViewById(R.id.fragment_result_filter_popup_slider_percent);
        seekBarMatchPercent.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar.getMax() == progress) {
                    fsSettings.maxMissing = Integer.MAX_VALUE;
                    txtViewMatchPercent.setText("all");
                } else {
                    txtViewMatchPercent.setText(progress + "");
                    fsSettings.maxMissing = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarMatchPercent.setProgress(fsSettings.maxMissing);
    }

    public Button getBtnApply() {
        return btnApply;
    }

    public Spinner getSpinnerSortBy() {
        return spinnerSortBy;
    }

    public TextView getTxtViewMatchPercent() {
        return txtViewMatchPercent;
    }

    public GridView getLayoutCategoryList() {
        return layoutCategoryList;
    }

    public class CategoryBtnAdapter extends BaseAdapter {

        private List<Category> categories;


        public CategoryBtnAdapter(List<Category> categories) {
            this.categories = categories;
        }

        @Override
        public int getCount() {
            return categories.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View gridView;

            if (convertView == null) {

                gridView = new View(getContext());

                // get layout from mobile.xml
                gridView = inflater.inflate(R.layout.fragment_result_filter_popup_category_btn, null);

                ToggleButton tb = (ToggleButton) gridView.findViewById(R.id.fragment_result_filter_popup_category_btn_btn);
                final Category c = categories.get(position);
                tb.setText(c.getName());
                tb.setTextOff(c.getName());

                // find out if the buttons category is already filtered from previous search
                if (fsSettings.categoriesInSearch.contains(c)) {
                    tb.setChecked(true);
                } else {
                    tb.setChecked(false);
                }

                tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            if (!fsSettings.categoriesInSearch.contains(c)) {
                                fsSettings.categoriesInSearch.add(c);
                            }
                        } else {
                            if (!fsSettings.categoriesInSearch.contains(c)) {
                                fsSettings.categoriesInSearch.remove(c);
                            }
                        }
                    }
                });
            } else {
                gridView = (View) convertView;
            }

            return gridView;
        }
    }
}