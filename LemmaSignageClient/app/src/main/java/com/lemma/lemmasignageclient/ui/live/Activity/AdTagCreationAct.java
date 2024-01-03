package com.lemma.lemmasignageclient.ui.live.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.lemma.lemmasignageclient.R;
import com.lemma.lemmasignageclient.common.AppManager;
import com.lemma.lemmasignageclient.common.AppUtil;
import com.lemma.lemmasignageclient.ui.MainActivity;
import com.lemma.lemmasignageclient.ui.live.Bean.GeoDetail;
import com.lemma.lemmasignageclient.ui.live.Bean.Publisher;
import com.lemma.lemmasignageclient.ui.live.Views.GeoLevelitem;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AdTagCreationAct extends Activity implements OnItemSelectedListener {

    Button configureBtn = null;
    EditText adTagNameTxtFld = null;
    GeoLevelitem geoItem = null;
    LinearLayout containerLayout = null;
    ArrayList<GeoLevelitem> inputViews = new ArrayList<GeoLevelitem>();
    List<String> categories = null;
    Spinner spinner_Invent_Cat;
    Spinner spinner_Invent_Type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atc);

        containerLayout = (LinearLayout) findViewById(R.id.containerLayout);
        adTagNameTxtFld = (EditText) findViewById(R.id.adtag_name);

        Spinner spinner = (Spinner) findViewById(R.id.Spinner01);
        spinner_Invent_Cat = (Spinner) findViewById(R.id.Spinner_Inventory_Category);
        spinner_Invent_Type = (Spinner) findViewById(R.id.Spinner_Inventory_Type);

        spinner.setOnItemSelectedListener(this);

        categories = new ArrayList<String>() {{
            add("Country");
            add("State");
            add("City");
            add("Area");
        }};

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        configureBtn = (Button) findViewById(R.id.configure_btn);
        configureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configure();
            }
        });

        configureForIndex(3);
        setupTargetingParams();
    }

    private void setupTargetingParams() {
        Publisher publisher = AppManager.getInstance().publisher;
        final ProgressDialog progress = AppUtil.showDialog(this, "Configuring...");

        publisher.getTargetingParams(new Publisher.DataListener<Publisher.TargetingParams>() {
            @Override
            public void onSuccess(final Publisher.TargetingParams data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();

                        // Creating adapter for spinner
                        ArrayAdapter invTypesAdapter = new ArrayAdapter<Publisher.TargetingParamItem>(AdTagCreationAct.this, android.R.layout.simple_spinner_item, data.inventoryTypes);

                        // Drop down layout style - list view with radio button
                        invTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        // attaching data adapter to spinner
                        AdTagCreationAct.this.spinner_Invent_Type.setAdapter(invTypesAdapter);

                        spinner_Invent_Type.setOnItemSelectedListener(new OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                ArrayList<Publisher.TargetingParamItem> items = data.inventoryCategories;
                                ArrayList<Publisher.TargetingParamItem> filteredItems = new ArrayList<>();
                                Publisher.TargetingParamItem selectedInvtType = data.inventoryTypes.get(i);
                                for (int index = 0; i < items.size(); ++i) {
                                    Publisher.TargetingParamItem invtCategoryItem = items.get(i);
                                    if (selectedInvtType.id == invtCategoryItem.type) {
                                        filteredItems.add(invtCategoryItem);
                                    }
                                }

                                // Creating adapter for spinner
                                ArrayAdapter invCatAdapter = new ArrayAdapter<Publisher.TargetingParamItem>(AdTagCreationAct.this, android.R.layout.simple_spinner_item, filteredItems);

                                // Drop down layout style - list view with radio button
                                invCatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                // attaching data adapter to spinner
                                AdTagCreationAct.this.spinner_Invent_Cat.setAdapter(invCatAdapter);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });


                    }
                });
            }

            @Override
            public void onError(Error error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                               long arg3) {

        for (GeoLevelitem gv : inputViews) {
            containerLayout.removeView(gv);
        }
        inputViews.clear();
        configureForIndex(arg2);
    }

    private void configureForIndex(int index) {

        switch (index) {
            case 0: {
                GeoLevelitem countryVw = new GeoLevelitem(getApplicationContext(), true);
                inputViews.add(countryVw);
            }
            break;
            case 1: {
                GeoLevelitem countryVw = new GeoLevelitem(getApplicationContext(), false);
                inputViews.add(countryVw);

                GeoLevelitem stateVw = new GeoLevelitem(getApplicationContext(), true);
                inputViews.add(stateVw);
            }
            break;
            case 2: {

                GeoLevelitem countryVw = new GeoLevelitem(getApplicationContext(), false);
                inputViews.add(countryVw);

                GeoLevelitem stateVw = new GeoLevelitem(getApplicationContext(), false);
                inputViews.add(stateVw);

                GeoLevelitem cityVw = new GeoLevelitem(getApplicationContext(), true);
                inputViews.add(cityVw);
            }
            break;
            case 3: {

                GeoLevelitem countryVw = new GeoLevelitem(getApplicationContext(), false);
                inputViews.add(countryVw);

                GeoLevelitem stateVw = new GeoLevelitem(getApplicationContext(), false);
                inputViews.add(stateVw);

                GeoLevelitem cityVw = new GeoLevelitem(getApplicationContext(), false);
                inputViews.add(cityVw);

                GeoLevelitem areaVw = new GeoLevelitem(getApplicationContext(), true);
                inputViews.add(areaVw);
            }
            break;
            default:
                break;
        }

        GeoLevelitem parentItemVw = null;
        int size = inputViews.size();
        int insertIndex = containerLayout.getChildCount() - 1;
        for (int i = 0; i < size; ++i) {
            GeoLevelitem itemVw = inputViews.get(i);
            itemVw.setHint(categories.get(i));
            if (parentItemVw != null) {
                parentItemVw.nextItem = itemVw;
            }
            if (insertIndex >= 0) {

                containerLayout.addView(itemVw, insertIndex);
                insertIndex++;
            }
            if (i == 0) {
                itemVw.loadGeoList();
            }
            parentItemVw = itemVw;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    private void configure() {

        boolean isValid = true;
        for (GeoLevelitem gv : inputViews) {
            isValid = isValid && gv.validate();
        }

        if (isValid) {
            GeoLevelitem item = (GeoLevelitem) inputViews.get(inputViews.size() - 1);

            String id = UUID.randomUUID().toString();
            String adTagname = "ad_tag_" + id;

            if (adTagNameTxtFld != null) {

                if (adTagNameTxtFld.getText().toString().isEmpty()) {
                    adTagNameTxtFld.setError("enter a valid Ad unit name");
                } else {
                    adTagname = adTagNameTxtFld.getText().toString();
                }
            }
            Publisher publisher = AppManager.getInstance().publisher;
            ArrayList<GeoDetail> geos = new ArrayList<GeoDetail>();
            for (GeoDetail geoDetail : item.selectedGeos) {
                geos.add(geoDetail);
            }

            final ProgressDialog progress = AppUtil.showDialog(this, "Configuring...");
            Publisher.TargetingParamItem inventoryTypeItem = (Publisher.TargetingParamItem) spinner_Invent_Type.getSelectedItem();
            Publisher.TargetingParamItem inventoryCatItem = (Publisher.TargetingParamItem) spinner_Invent_Cat.getSelectedItem();

            HashMap map = new HashMap() {{
                JSONArray inventory_Type = new JSONArray();
                JSONArray inventory_Cat = new JSONArray();
                put("inventory_type", inventory_Type.put(inventoryTypeItem.id));
                put("inventory_category", inventory_Cat.put(inventoryCatItem.id));
            }};

            publisher.adTag(map, adTagname, geos, new Publisher.AdTagListener() {

                @Override
                public void onSuccess(final String adTag) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (adTag == null) {
                                AppUtil.showMsg(getApplicationContext(), "Got null ad tag");
                                return;
                            }

                            progress.dismiss();
                            Error error = AppUtil.configureAuForLivePlayerMode(adTag);
                            if (error != null){
                                AppUtil.showMsg(getApplicationContext(), error.toString());
                            }else {
                                AppUtil.launchActivity(AdTagCreationAct.this,MainActivity.class);
                            }
                        }
                    });

                }

                @Override
                public void onError(final Error error) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                            AppUtil.showMsg(getBaseContext(),"Failed to configure with error " + error.getMessage());
                        }
                    });
                }
            });
        }
    }

}

