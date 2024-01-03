package com.lemma.lemmasignagesdk.scedule.scheduleplayer;

import android.net.Uri;

import com.lemma.lemmasignagesdk.common.LMUtils;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.common.ScheduleAd;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.common.ScheduleAdGrp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class Utils {

    static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    public static ScheduleAd fallbackForAd(ScheduleAd wa,
                                           Uri mediaUri) {

        ScheduleAdItem scheduleAdItem = new ScheduleAdItem();
        scheduleAdItem.itemType = "video/mp4";
        scheduleAdItem.startTime = wa.getScheduleTime();
        scheduleAdItem.duration = wa.getDuration();
        ScheduleAd scheduleAd = ScheduleAd.newBuilder()
                .withAdItem(scheduleAdItem)
                .withIsFallbackAd(true)
                .withLocalUriString(mediaUri.toString())
                .build();
        return scheduleAd;
    }

    public static ScheduleAd freshFallbackAd(Uri mediaUri) {
        ScheduleAdItem scheduleAdItem = new ScheduleAdItem();
        scheduleAdItem.itemType = "video/mp4";
        scheduleAdItem.startTime = LMUtils.getCurrentTime();
        scheduleAdItem.duration = 15;
        ScheduleAd scheduleAd = ScheduleAd.newBuilder()
                .withAdItem(scheduleAdItem)
                .withIsFallbackAd(true)
                .withLocalUriString(mediaUri.toString())
                .build();
        scheduleAd.setScheduleAdType(ScheduleAd.Type.VIDEO);
        return scheduleAd;
    }

    public static ScheduleAdGrp freshFallbackAdGrp(Uri mediaUri) {
        ScheduleAd scheduleAd = freshFallbackAd(mediaUri);
        ArrayList<ScheduleAd> scheduleAds = new ArrayList() {{
            add(scheduleAd);
        }};
        return ScheduleAdGrp.builder().setScheduleAds(scheduleAds).build();
    }

    public static String updatedScheduleTracker(String url,
                                                ScheduleAdItem scheduleAdItem) {

        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        sdf.setTimeZone(timeZone);
        String ts = sdf.format(new Date());

        HashMap<String, Object> map = scheduleAdItem.getRawMapForTracker();
        map.put("ts", ts);
        String updatedUrl = LMUtils.replaceUriParameter(url, map);
        return updatedUrl;
    }

    public static boolean isURL(String input) {
        if (input == null) {
            return false;
        }
        return input.startsWith("http") || input.startsWith("file") || input.startsWith("android.resource:");
    }

    public static boolean isImageType(String type) {
        return (type.equalsIgnoreCase("image/jpeg") ||
                type.equalsIgnoreCase("image/png") ||
                type.equalsIgnoreCase("image/jpg"));
    }


    public static boolean isVastAd(ScheduleAd.Type scheduleItemType) {
        return (scheduleItemType == ScheduleAd.Type.VAST_IMAGE ||
                scheduleItemType == ScheduleAd.Type.VAST_VIDEO ||
                scheduleItemType == ScheduleAd.Type.WEB);
    }

    public static boolean eligibleForReplacement(Schedule existing, Schedule newSchedule) {
        if (existing.getCheckSum() != null && newSchedule.getCheckSum() != null) {
            return !existing.getCheckSum().equalsIgnoreCase(newSchedule.getCheckSum());
        }
        return true;
    }

}
