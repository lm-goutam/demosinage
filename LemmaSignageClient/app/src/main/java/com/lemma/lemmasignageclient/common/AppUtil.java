package com.lemma.lemmasignageclient.common;

import static com.lemma.lemmasignageclient.ui.MainActivity.REQUEST_ID_MULTIPLE_PERMISSIONS;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.lemma.lemmasignageclient.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class AppUtil {

    public static Uri getRawUri(Integer id, Context context) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + File.pathSeparator + File.separator + File.separator
                + context.getPackageName()
                + File.separator
                + id);
    }

    public static void attachToParentAndMatch(ViewGroup view, ViewGroup parentView) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        parentView.addView(view, params);
    }

    public static void attachToParentAndMatch(View view, ViewGroup parentView) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        parentView.addView(view, params);
    }

    public static void launchActivity(Activity parent,
                                      Class activityClass) {

        Intent intent = new Intent(parent, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        parent.startActivity(intent);
    }

    public static Uri getDefaultAdUri(Context context) {
        return getRawUri(R.raw.lemmad, context);
    }

    public static void showMsg(Context context, String msg) {
        new AlertDialog.Builder(context)
                .setTitle("Message")
                .setPositiveButton("ok", null)
                .setMessage(msg)
                .show();
    }

    public static void applyCoordinateToLayout(FrameLayout layout, int left, int top, int width, int height) {

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layout.getLayoutParams();
        params.setMargins(left, top, params.rightMargin, params.bottomMargin);
        params.width = width;
        params.height = height;
        layout.setLayoutParams(params);
    }

    public static void applyCoordinateToLayout(FrameLayout layout, Rect rect) {

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layout.getLayoutParams();
        params.setMargins(rect.left, rect.top, params.rightMargin, params.bottomMargin);
        params.width = rect.right - rect.left;
        params.height = rect.bottom - rect.top;
        layout.setLayoutParams(params);
    }

    public static ProgressDialog showDialog(Context context, String msg) {
        ProgressDialog mDialog = new ProgressDialog(context);
        mDialog.setMessage(msg);
        mDialog.setCancelable(false);
        mDialog.show();
        return mDialog;
    }

    public static void hideDialog(ProgressDialog mDialog, long seconds) {
        if (seconds == 0) {
            mDialog.dismiss();
        } else {
            LMTimer.TimerBuilder.aTimer()
                    .withCallable(new LMTimer.Callable() {
                        @Override
                        public void call(boolean isOnMainThread) {
                            mDialog.dismiss();
                        }
                    })
                    .withCallAfterSeconds(seconds)
                    .build()
                    .start();
        }
    }

    public static boolean isValid(String value) {
        return (Objects.nonNull(value) && !value.isEmpty());
    }

    public static void requestPermissions(Activity context, List<String> listPermissionsNeeded) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.requestPermissions(listPermissionsNeeded
                            .toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
        }
    }

    public static List<String> permissionList(Context context) {

        List<String> listPermissionsNeeded = new ArrayList<>();
//		addIfNotGranted(listPermissionsNeeded,Manifest.permission.READ_EXTERNAL_STORAGE);
//        addIfNotGranted(listPermissionsNeeded, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        addIfNotGranted(context, listPermissionsNeeded, Manifest.permission.ACCESS_COARSE_LOCATION);
        addIfNotGranted(context, listPermissionsNeeded, Manifest.permission.RECEIVE_BOOT_COMPLETED);
//        addIfNotGranted(listPermissionsNeeded, Manifest.permission.INTERNET);
        addIfNotGranted(context, listPermissionsNeeded, Manifest.permission.ACCESS_WIFI_STATE);
        addIfNotGranted(context, listPermissionsNeeded, Manifest.permission.ACCESS_NETWORK_STATE);
        return listPermissionsNeeded;
    }

    public static boolean checkForAllPermissions(Context context) {
        List<String> listPermissionsNeeded = permissionList(context);
        return listPermissionsNeeded.isEmpty();
    }

    private static void addIfNotGranted(Context context, List<String> list, String permission) {

        int isAllowed = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            isAllowed = context.checkSelfPermission(permission);
        }

        if (isAllowed != PackageManager.PERMISSION_GRANTED) {
            list.add(permission);
        }
    }

    public static HashMap<String, String> stringToMap(String jsonString) throws JSONException {
        HashMap<String, String> customParamsMap = new HashMap<>();

        if (!AppUtil.isValid(jsonString)) {
            return customParamsMap;
        }

        JSONObject jsonObject = new JSONObject(jsonString);
        Iterator<String> keys = jsonObject.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            String value = jsonObject.getString(key);
            customParamsMap.put(key, value);
        }
        return customParamsMap;
    }

    public static String paramValueInUrl(String url, String param) {
        Uri uri = Uri.parse(url);
        return uri.getQueryParameter(param);
    }

    public static Error configureAuForLivePlayerMode(String adUnitUrl) {
        String pubId = paramValueInUrl(adUnitUrl, "pid");
        String aid = paramValueInUrl(adUnitUrl, "aid");
        if (isValid(pubId) && isValid(aid)){
            AppConfig.instance.setAdunitId(aid);
            AppConfig.instance.setPublisherId(pubId);
            return null;
        }else {
            return new Error("Unable to get Pub & Ad unit id");
        }
    }

    public static String scheduleAdsAPI() {
        Uri uri = new Uri.Builder().scheme("https")
                .authority(AppConfig.instance.getDomain())
                .path(AppConfig.instance.getScheduledAdAPIPath())
                .build();
        return uri.toString();
    }

    public static String serveAdsAPI() {
        Uri uri = new Uri.Builder().scheme("https")
                .authority(AppConfig.instance.getDomain())
                .path(AppConfig.instance.getServeAdAPIPath())
                .build();
        return uri.toString();
    }
}