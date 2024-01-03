package com.lemma.lemmasignageclient.ui.live.Views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.lemma.lemmasignageclient.R;
import com.lemma.lemmasignageclient.common.AppManager;
import com.lemma.lemmasignageclient.common.logger.LMLogger;
import com.lemma.lemmasignageclient.ui.live.Bean.GeoDetail;
import com.lemma.lemmasignageclient.ui.live.Bean.Publisher;

import java.util.ArrayList;


public class GeoLevelitem extends FrameLayout {

    public ArrayList<GeoDetail> selectedGeos;
    public GeoLevelitem nextItem;
    ArrayList<GeoDetail> availableGeos, originalGeos;
    ArrayList<GeoDetail> suggestions;
    Context mContext;
    String currentText; //keep track!
    GeoDetail selectedGeo;
    //	ArrayAdapter<GeoDetail> adapter;
    Boolean isMultiSelect = true;
    private MultiAutoCompleteTextView multiAutocompletetextview;
    private AutoCompleteTextView autocompletetextview;


    public GeoLevelitem(Context context) {
        super(context);

    }

    public GeoLevelitem(Context context, boolean isMultiSelect) {
        super(context);

        this.isMultiSelect = isMultiSelect;
        mContext = context;
        selectedGeos = new ArrayList<GeoDetail>();
        originalGeos = new ArrayList<GeoDetail>();
        availableGeos = new ArrayList<GeoDetail>();
        suggestions = new ArrayList<GeoDetail>();

        inflate(mContext, R.layout.geo_level_list_item, this);

        multiAutocompletetextview = (MultiAutoCompleteTextView) findViewById(R.id.multiAutoCompleteTextView);

        ColorDrawable cd = new ColorDrawable(0xFFFFFFFF);

        multiAutocompletetextview.setDropDownBackgroundDrawable(cd);
        autocompletetextview = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);


        if (isMultiSelect) {

            autocompletetextview.setVisibility(GONE);

        } else {

            multiAutocompletetextview.setVisibility(GONE);
        }

        disableInteractions(true);
    }

    public void loadSubGeos(GeoDetail selectedGeo) {

        this.selectedGeo = selectedGeo;

        loadSubGeoList();
    }

    public boolean validate() {

        boolean isValid = true;
        if (isMultiSelect) {
            if (multiAutocompletetextview.getText().length() == 0) {
                isValid = false;
                multiAutocompletetextview.setError("Enter valid value");
            }
        } else {
            if (autocompletetextview.getText().length() == 0) {
                isValid = false;
                autocompletetextview.setError("Enter valid value");
            }
        }
        return isValid;
    }

    public void setHint(String hint) {

        if (isMultiSelect) {
            multiAutocompletetextview.setHint(hint);
        } else {
            autocompletetextview.setHint(hint);
        }
    }

    private void disableInteractions(boolean disable) {

        if (isMultiSelect) {

            multiAutocompletetextview.setClickable(!disable);
            multiAutocompletetextview.setEnabled(!disable);

        } else {

            autocompletetextview.setClickable(!disable);
            autocompletetextview.setEnabled(!disable);
        }
    }

    private void confiureInput(final ArrayAdapter<GeoDetail> adapter) {

        if (isMultiSelect) {

            multiAutocompletetextview.setAdapter(adapter);
            multiAutocompletetextview.setThreshold(1);


            multiAutocompletetextview.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

            multiAutocompletetextview.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int index, long position) {

                    currentText = adapter.getItem((int) position).toString();

                    GeoDetail geo = availableGeos.get((int) position);
                    selectedGeos.add(geo);

                    if (nextItem != null) {
                        nextItem.loadSubGeos(geo);
                    }
                }
            });

            multiAutocompletetextview.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    currentText = null;
                }

                @Override
                public void afterTextChanged(Editable arg0) {
                }
            });


            multiAutocompletetextview.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    autocompletetextview.showDropDown();
                }
            });

        } else {

            autocompletetextview.setAdapter(adapter);
            autocompletetextview.setThreshold(1);


            autocompletetextview.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int index, long position) {

                    currentText = adapter.getItem((int) position).toString();

                    GeoDetail geo = availableGeos.get((int) position);
                    selectedGeos.add(geo);

                    if (nextItem != null) {

                        nextItem.loadSubGeos(geo);
                    }
                }
            });

            autocompletetextview.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    currentText = null;
                }

                @Override
                public void afterTextChanged(Editable arg0) {

                }
            });


            autocompletetextview.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    autocompletetextview.showDropDown();
                }
            });
        }
    }

    private void loadSubGeoList() {

//		int lev=Integer.parseInt(selectedGeo.level);


        final Publisher publisher = AppManager.getInstance().publisher;
        ArrayList<GeoDetail> passedGeos = publisher.countryLevelGeos;


        ArrayList<GeoDetail> filteredGeos = selectedGeo.filterGeoDetails(passedGeos);
        if (filteredGeos != null) {

            reloadNewAdapter(filteredGeos);

        } else {

            publisher.geo(selectedGeo, new GeoDetail.GeoListingListener() {

                @Override
                public void onSuccess(final ArrayList<GeoDetail> geoDetails) {

                    Handler uiHandler = new Handler(Looper.getMainLooper());
                    uiHandler.post(new Runnable() {

                        @Override
                        public void run() {

                            if (publisher.countryLevelGeos == null) {
                                publisher.countryLevelGeos = (ArrayList<GeoDetail>) geoDetails.clone();
                            }
                            reloadNewAdapter(geoDetails);
                        }
                    });
                }

                @Override
                public void onError(Error error) {
                    // TODO Auto-generated method stub

                }
            });
        }

    }

    public void reloadNewAdapter(ArrayList<GeoDetail> geoDetails) {

        disableInteractions(false);

        availableGeos = geoDetails;
        originalGeos = (ArrayList<GeoDetail>) availableGeos.clone();

        ArrayAdapter adapter = new ArrayAdapter<GeoDetail>(mContext, android.R.layout.simple_list_item_1, availableGeos) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);

                GeoDetail geo = getItem(position);
                text.setText(geo.geoName());
                Log.v("GeoLevelitem", "Loaded geo name - " + geo.geoName());

                text.setTextColor(Color.BLACK);
                return view;
            }

            @Override
            public Filter getFilter() {
                return new Filter() {

                    public String convertResultToString(Object resultValue) {
                        return ((GeoDetail) resultValue).geoName();
                    }

                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {


                        if (constraint != null) {

                            LMLogger.v("GeoLevelitem", "performFiltering  - " + constraint.toString());

                            suggestions = new ArrayList<GeoDetail>();

                            for (GeoDetail geoDetail : availableGeos) {
                                if (geoDetail.geoName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {

                                    suggestions.add(geoDetail);
                                }
                            }
                            FilterResults filterResults = new FilterResults();
                            filterResults.values = suggestions;
                            filterResults.count = suggestions.size();
                            return filterResults;
                        } else {
                            return new FilterResults();
                        }
                    }

                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {

                        clear();
                        if (results.count > 0) {
                            addAll((ArrayList<GeoDetail>) results.values);
                        } else {
                            addAll(originalGeos);
                        }
                        notifyDataSetChanged();
                    }

                };
            }

        };

        confiureInput(adapter);
    }

    public void loadGeoList() {

        LMLogger.v("GeoLevelitem", "Loading geoes - ");
        if (originalGeos.size() > 0) {
            return;
        }
        Publisher publisher = AppManager.getInstance().publisher;
        publisher.geo(null, new GeoDetail.GeoListingListener() {

            @Override
            public void onSuccess(final ArrayList<GeoDetail> geoDetails) {


                Handler uiHandler = new Handler(Looper.getMainLooper());
                uiHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        disableInteractions(false);
                        ArrayAdapter adapter = geoArrayAdapter(geoDetails);
                        confiureInput(adapter);
                    }
                });
            }

            @Override
            public void onError(Error error) {
                // TODO Auto-generated method stub

            }
        });
    }

    public ArrayAdapter<GeoDetail> geoArrayAdapter(final ArrayList<GeoDetail> geoDetails) {


        availableGeos = geoDetails;
        originalGeos = (ArrayList<GeoDetail>) availableGeos.clone();

        ArrayAdapter adapter = new ArrayAdapter<GeoDetail>(mContext, android.R.layout.simple_list_item_1, availableGeos) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);

                GeoDetail geo = getItem(position);
                text.setText(geo.geoName());
                text.setTextColor(Color.BLACK);
                return view;
            }

            @Override
            public Filter getFilter() {
                return new Filter() {

                    public String convertResultToString(Object resultValue) {
                        return ((GeoDetail) resultValue).geoName();
                    }

                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        if (constraint != null) {

                            suggestions = new ArrayList<GeoDetail>();

                            for (GeoDetail geoDetail : availableGeos) {
                                if (geoDetail.geoName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                                    suggestions.add(geoDetail);
                                }
                            }
                            FilterResults filterResults = new FilterResults();
                            filterResults.values = suggestions;
                            filterResults.count = suggestions.size();
                            return filterResults;
                        } else {
                            return new FilterResults();
                        }
                    }

                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {

                        clear();
                        if (results.count > 0) {
                            addAll((ArrayList<GeoDetail>) results.values);
                        } else {
                            addAll(originalGeos);
                        }
                        notifyDataSetChanged();
                    }

                };
            }
        };
        return adapter;
    }

}