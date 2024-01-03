package com.lemma.lemmasignagesdk.scedule.scheduleplayer;

import android.graphics.Rect;

import com.lemma.lemmasignagesdk.common.LMLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class Schedule {

    public String tracker;
    ArrayList<ScheduleAdItem> scheduleItems = new ArrayList<>();
    ArrayList<ScheduleAdItemGrp> scheduleAdItemGrps = new ArrayList<>();
    private ArrayList<CustomLayoutList> customLayouts = new ArrayList<>();
    private String checkSum;

    public static String getGrpId(ArrayList<CustomLayoutList> customLayouts,
                                  ScheduleAdItem item) {

        for (CustomLayoutList list : customLayouts) {
            for (CustomLayout layout : list.customLayouts) {
                if (layout.creativeId.equalsIgnoreCase(item.getCreativeId()) &&
                        layout.lineItemId.equalsIgnoreCase(item.lineItemId)) {
                    item.frame = layout.frame;
                    return list.id;
                }
            }
        }
        return UUID.randomUUID().toString();
    }

    public static Schedule parse(String response) {
        Schedule schedule = new Schedule();

        ArrayList<ScheduleAdItem> scheduleItems = new ArrayList();
        try {

            JSONObject obj = new JSONObject(response);

            JSONObject dataObj = obj.getJSONObject("data");
            JSONArray scheduleArray = dataObj.getJSONArray("schedule");

            String customLayoutKey = "custom_layout";
            if (dataObj.has(customLayoutKey)){
                JSONArray customLayoutArray = dataObj.getJSONArray("custom_layout");
                schedule.customLayouts = CustomLayoutList.parseArray(customLayoutArray);
            }

            for (int i = 0; i < scheduleArray.length(); i++) {

                JSONObject scheduleObj = (JSONObject) scheduleArray.get(i);
                ScheduleAdItem item = ScheduleAdItem.parse(scheduleObj);
                item.setTemporaryGroupId(getGrpId(schedule.customLayouts, item));

                if (item != null) {
                    scheduleItems.add(item);
                }
            }
            schedule.tracker = dataObj.getString("trk");
            schedule.checkSum = dataObj.getString("chk_Sum");
            schedule.scheduleItems = scheduleItems;

            // TODO: refactor
            schedule.scheduleAdItemGrps = schedule.adGroups();

        } catch (Exception e) {
            LMLog.e(e.getLocalizedMessage());
        }
        return schedule;
    }

    public ArrayList<ScheduleAdItem> getScheduleItems() {
        return scheduleItems;
    }

    public ArrayList<ScheduleAdItemGrp> getScheduleAdItemGrps() {
        return scheduleAdItemGrps;
    }

    public String getCheckSum() {
        return checkSum;
    }

    private CustomLayoutList mentionedInLayout(ScheduleAdItem item) {
        for (CustomLayoutList list : customLayouts) {
            if (mentionedInLayout(list.customLayouts, item)) {
                return list;
            }
        }
        return null;
    }

    private boolean mentionedInLayout(ArrayList<CustomLayout> list, ScheduleAdItem item) {
        for (CustomLayout layout : list) {
            if (layout.creativeId != item.creative ||
                    layout.lineItemId != item.lineItemId) {
                return true;
            }
        }
        return false;
    }

    /* test
    static Schedule parse(String response) {
        Schedule schedule = new Schedule();

        ArrayList<ScheduleAdItem> scheduleItems = new ArrayList();
        try {
            JSONObject obj = new JSONObject(response);
            JSONObject dataObj = obj.getJSONObject("data");

            JSONArray scheduleArray = dataObj.getJSONArray("schedule");

            for (int j = 0; j < 20; j++) {

                for (int i = 0; i < scheduleArray.length(); i++) {

                    JSONObject scheduleObj = (JSONObject) scheduleArray.get(i);
                    ScheduleAdItem item = ScheduleAdItem.parse(scheduleObj);

                    if (item != null) {
                        scheduleItems.add(item);
                    }
                }
            }
            schedule.tracker = dataObj.getString("trk");

            ArrayList<Date> dates = testDates();
            for (int j = 0; j < scheduleItems.size(); j++) {
                ScheduleAdItem item = scheduleItems.get(j);
                item.startTime = dates.get(j);
            }

            schedule.scheduleItems = scheduleItems;

        } catch (JSONException e) {
            LMLog.e(TAG, e.getLocalizedMessage());
        }


        return schedule;

    }
    */

    public ArrayList<ScheduleAdItemGrp> adGroups() {

        ArrayList<ScheduleAdItemGrp> grps = new ArrayList<>();
        ScheduleAdItem prevItem = null;
        ScheduleAdItemGrp preGrp = null;
        for (ScheduleAdItem item : scheduleItems) {

            if (prevItem != null && prevItem.getTemporaryGroupId().equalsIgnoreCase(item.getTemporaryGroupId())) {
                ScheduleAdItemGrp grp = grps.get(grps.size() - 1);
                grp.add(item);
            } else {
                ScheduleAdItemGrp grp = new ScheduleAdItemGrp();
                grp.add(item);
                grps.add(grp);
            }

            prevItem = item;

        }
        return grps;
    }

    public Error validate() {
        if (scheduleItems.isEmpty()) {
            return new Error("No schedule items in response");
        }

//        ScheduleAdItem prevItem = scheduleItems.get(0);
//        for (int i=1; i<scheduleItems.size(); ++i) {
//            Integer duration =  prevItem.duration;
//            Date date = prevItem.startTime;
//            Date nextStartTime = LMUtils.dateByAddingSeconds(date, duration);
//            ScheduleAdItem item = scheduleItems.get(i);
//            if (!nextStartTime.equals(item.startTime)){
//                return new Error("Inconsistent schedule");
//            }
//        }
        return null;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                ", \nscheduleItems.count=" + scheduleItems.size() +
                ", \nscheduleAdItemGrps.count=" + scheduleAdItemGrps.size() +
                ", \ncustomLayouts=" + customLayouts +
                ", \ncheckSum='" + checkSum + '\'' +
                '}';
    }

    public ArrayList<ScheduleAdItem> getAds(Integer start, Integer len) {
        if (scheduleItems.size() > (start + len)) {
            return (ArrayList) new ArrayList(scheduleItems.subList(start, start + len));
        }

        if (scheduleItems.size() > start) {
            return (ArrayList) new ArrayList(scheduleItems.subList(start, scheduleItems.size()));
        }

        return new ArrayList();
    }

//    static private ArrayList<Date> testDates() {
//
//        ArrayList<Date> dates = new ArrayList();
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.MINUTE, -5);
//        calendar.set(Calendar.MILLISECOND, 0);
//        calendar.set(Calendar.SECOND, 0);
//
//        Date lastDate = calendar.getTime();
//        System.out.println("Current Date = " + lastDate);
//        dates.add(lastDate);
//
//        for (int i = 0; i < 60; i++) {
//
//            Calendar aCalendar = Calendar.getInstance();
//            aCalendar.setTime(lastDate);
//            aCalendar.add(Calendar.SECOND, 15);
//            lastDate = aCalendar.getTime();
//            dates.add(lastDate);
//        }
//        return dates;
//    }

    public ArrayList<ScheduleAdItemGrp> getAdGroups(Integer start, Integer len) {
        if (scheduleAdItemGrps.size() > (start + len)) {
            return (ArrayList) new ArrayList(scheduleAdItemGrps.subList(start, start + len));
        }

        if (scheduleAdItemGrps.size() > start) {
            return (ArrayList) new ArrayList(scheduleAdItemGrps.subList(start, scheduleAdItemGrps.size()));
        }
        return new ArrayList();
    }

    public static class CustomLayoutList {
        private final ArrayList<CustomLayout> customLayouts;
        private final String id;
        public CustomLayoutList(ArrayList<CustomLayout> customLayouts) {
            this.customLayouts = customLayouts;
            id = UUID.randomUUID().toString();
        }

        public static CustomLayoutList parse(JSONArray customLayoutList) throws JSONException {
            ArrayList list = new ArrayList();
            for (int j = 0; j < customLayoutList.length(); j++) {
                JSONObject layoutObj = customLayoutList.getJSONObject(j);
                CustomLayout layout = CustomLayout.parse(layoutObj);
                if (layout != null) {
                    list.add(layout);
                }
            }
            return new CustomLayoutList(list);
        }

        public static ArrayList<CustomLayoutList> parseArray(JSONArray customLayoutArray) throws JSONException {
            ArrayList customLayouts = new ArrayList();
            for (int i = 0; i < customLayoutArray.length(); i++) {
                JSONArray customLayoutContentArray = customLayoutArray.getJSONArray(i);
                CustomLayoutList customLayoutList = CustomLayoutList.parse(customLayoutContentArray);
                customLayouts.add(customLayoutList);
            }
            return customLayouts;
        }

        @Override
        public String toString() {
            return "CustomLayoutList{" +
                    "customLayouts=" + customLayouts +
                    '}';
        }
    }

    public static class CustomLayout {
        private String lineItemId;
        private String creativeId;
        private Rect frame;

        public static CustomLayout parse(JSONObject obj) throws JSONException {
            CustomLayout customLayout = new CustomLayout();
            customLayout.lineItemId = obj.getString("LineItemId");
            customLayout.creativeId = obj.getString("CreativeId");
            Rect rect = new Rect(obj.getInt("XStart"),
                    obj.getInt("YStart"),
                    obj.getInt("XEnd"),
                    obj.getInt("YEnd"));
            customLayout.frame = rect;
            return customLayout;
        }

        @Override
        public String toString() {
            return "CustomLayout{" +
                    "lineItemId='" + lineItemId + '\'' +
                    ", creativeId='" + creativeId + '\'' +
                    ", frame=" + frame +
                    '}';
        }
    }

}
