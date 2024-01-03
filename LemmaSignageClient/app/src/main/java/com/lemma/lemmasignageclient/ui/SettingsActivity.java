package com.lemma.lemmasignageclient.ui;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextClock;

import androidx.appcompat.app.AppCompatActivity;

import com.jakewharton.processphoenix.ProcessPhoenix;
import com.lemma.lemmasignageclient.common.AppConfig;
import com.lemma.lemmasignageclient.common.AppUtil;
import com.lemma.lemmasignageclient.databinding.SettingsActBinding;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton updateSettingsBtn;
    private SettingsActBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SettingsActBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final TextClock textClock = new TextClock(this);
        textClock.setTextSize(20);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textClock.setLayoutParams(layoutParams);
        textClock.setFormat12Hour("hh:mm:ss a");

        binding.clockContainerId.addView(textClock);

        updateSettingsBtn = binding.updateSettingsBtn;
        updateSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateConfigDialog dialog = new UpdateConfigDialog(SettingsActivity.this);
                dialog.show(new UpdateConfigDialog.OnDismissCallback() {
                    @Override
                    public void onDismiss(boolean shouldRestart) {
                        if (shouldRestart){
                            ProcessPhoenix.triggerRebirth(getBaseContext());
                        }else {
                            updateData();
                        }
                    }
                });
            }
        });

        ImageButton playBtn = binding.playBtn;
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppConfig.instance.alreadySetup()) {
                    ProcessPhoenix.triggerRebirth(SettingsActivity.this);
                }else {

                    if (AppConfig.instance.isSyncScheduleNamespace()){
                        AppUtil.showMsg(SettingsActivity.this,
                                "App is not configured yet");

                    }else {
                        AppUtil.showMsg(SettingsActivity.this,
                                "TODO: launch live player");

                    }
                }
            }
        });

        ImageButton closeAppBtn = binding.closeAppBtn;
        closeAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.deviceId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(android.content.Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(android.content.Intent.EXTRA_SUBJECT,"Device Id ");
                i.putExtra(android.content.Intent.EXTRA_TEXT, AppConfig.instance.getDeviceId());
                startActivity(Intent.createChooser(i,"Share via"));
            }
        });
        binding.deviceValId.setText(AppConfig.instance.getDeviceId());
        binding.resolutionValId.setText(AppConfig.instance.getResolution());
        binding.osValId.setText(AppConfig.instance.getDeviceName());
        binding.appSdkVerValId.setText(AppConfig.instance.getAppSDKVersion());
        updateData();
    }

    private void updateData() {
        Rect rect = AppConfig.instance.viewFrame();
        binding.pubValId.setText(AppConfig.instance.getPublisherId());
        binding.auValId.setText(AppConfig.instance.getAdunitId());

        binding.xyValId.setText(rect.left+"x"+rect.top);
        binding.widthHeightValId.setText((rect.right-rect.left)+"x"+(rect.bottom-rect.top));

        binding.custParmValId.setMovementMethod(new ScrollingMovementMethod());

        binding.custParmValId.setText(AppConfig.instance.getCustomParams().toString());

        if (AppConfig.getNamespace(this).equalsIgnoreCase(AppConfig.NAMESPACE.SYNC_SCHEDULE.toString())){
            binding.syncRadioId.setChecked(true);
        }else if (AppConfig.getNamespace(this).equalsIgnoreCase(AppConfig.NAMESPACE.LIVE.toString())){
            binding.liveRadioId.setChecked(true);
        }

        if (AppConfig.instance.getEnvironmentType() ==  1){
            binding.serverEnvProdRadioId.setChecked(true);
        }else {
            binding.serverEnvLocalRadioId.setChecked(true);
        }
    }
}
