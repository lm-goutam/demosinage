package com.lemma.lemmasignageclient.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.lemma.lemmasignageclient.common.AppConfig;
import com.lemma.lemmasignageclient.common.AppUtil;
import com.lemma.lemmasignageclient.common.logger.Applogger;
import com.lemma.lemmasignageclient.databinding.UpdateConfigDialogBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class UpdateConfigDialog {

    private UpdateConfigDialogBinding binding;

    public static interface OnDismissCallback {
        public void onDismiss(boolean shouldRestart);
    }

    private OnDismissCallback onDismissCallback;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private int currentRadioGroupBtnId;

    public void setupView() {

        binding.pubIdEt.setText(AppConfig.instance.getPublisherId());
        binding.auIdEt.setText(AppConfig.instance.getAdunitId());
        Rect rect = AppConfig.instance.viewFrame();

        binding.startxEt.setText(String.valueOf(rect.left));
        binding.startyEt.setText(String.valueOf(rect.top));
        binding.widthEt.setText(String.valueOf(rect.right-rect.left));
        binding.heightEt.setText(String.valueOf(rect.bottom-rect.top));

        binding.customParValId.setText(new JSONObject(AppConfig.instance.getCustomParams()).toString());

        if (AppConfig.getNamespace(dialogBuilder.getContext()).equalsIgnoreCase(AppConfig.NAMESPACE.SYNC_SCHEDULE.toString())){
            binding.syncRadioId.setChecked(true);
            currentRadioGroupBtnId = binding.syncRadioId.getId();
        }else if (AppConfig.getNamespace(dialogBuilder.getContext()).equalsIgnoreCase(AppConfig.NAMESPACE.LIVE.toString())){
            binding.liveRadioId.setChecked(true);
            currentRadioGroupBtnId = binding.liveRadioId.getId();
        }

        if (AppConfig.instance.getEnvironmentType() == 1){
            binding.serEndProdRadioId.setChecked(true);
        }else {
            binding.serEndLocalRadioId.setChecked(true);
        }
    }

    private void handleOnSave() {
        AppConfig.instance.setPublisherId(binding.pubIdEt.getText().toString());
        AppConfig.instance.setAdunitId(binding.auIdEt.getText().toString());

        String startXValue = binding.startxEt.getText().toString();
        String startYValue = binding.startyEt.getText().toString();
        String widthValue = binding.widthEt.getText().toString();
        String heightValue = binding.heightEt.getText().toString();

        if (startXValue != null && startYValue != null &&
                widthValue != null && heightValue != null) {

            Rect rect = new Rect(Integer.parseInt(startXValue),
                    Integer.parseInt(startYValue),
                    Integer.parseInt(startXValue)+Integer.parseInt(widthValue),
                    Integer.parseInt(startYValue)+Integer.parseInt(heightValue)
            );
            AppConfig.instance.setViewFrame(rect);
        }

        boolean shouldRestart = false;
        if (binding.syncRadioId.isChecked()){
            shouldRestart = (currentRadioGroupBtnId != binding.syncRadioId.getId());
            AppConfig.setNamespace(dialogBuilder.getContext(), AppConfig.NAMESPACE.SYNC_SCHEDULE);
        }else if (binding.liveRadioId.isChecked()){
            shouldRestart = (currentRadioGroupBtnId != binding.liveRadioId.getId());
            AppConfig.setNamespace(dialogBuilder.getContext(), AppConfig.NAMESPACE.LIVE);
        }

        if (binding.serEndProdRadioId.isChecked()){
            AppConfig.instance.setEnvironmentType(1);
        }else if (binding.serEndLocalRadioId.isChecked()){
            AppConfig.instance.setEnvironmentType(0);
        }

        String customParString = binding.customParValId.getText().toString();
        try {
            HashMap map = AppUtil.stringToMap(customParString);
            AppConfig.instance.setCustomParams(map);
        } catch (JSONException e) {
            AppUtil.showMsg(this.dialogBuilder.getContext(), e.getLocalizedMessage());
            Applogger.e(e.getLocalizedMessage());
        }

        dialog.dismiss();
        onDismissCallback.onDismiss(shouldRestart);
    }

    private TextView titleView(Context context, String titleString) {
        TextView title = new TextView(context);
        title.setText(titleString);
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 32);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);
        return  title;
    }

    public UpdateConfigDialog(Activity context) {
        dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setCustomTitle(titleView(context, "Settings"));
        dialogBuilder.setNegativeButton("Cancel", null);
        dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                handleOnSave();
           }
        });
        LayoutInflater inflater = context.getLayoutInflater();
        binding = UpdateConfigDialogBinding.inflate(inflater);
        dialogBuilder.setView(binding.getRoot());
        setupView();
    }

    public void show(OnDismissCallback onDismissCallback){
        this.onDismissCallback = onDismissCallback;
        this.dialog = dialogBuilder.show();
    }
}

