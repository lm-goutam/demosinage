package com.lemma.lemmasignagesdk.scedule.scheduleplayer;

import java.util.ArrayList;
import java.util.Date;

public class ScheduleAdItemGrp {

    ArrayList<ScheduleAdItem> items = new ArrayList<>();

    public ArrayList<ScheduleAdItem> getItems() {
        return items;
    }

    public Date getStartTime() {
        ScheduleAdItem item = items.get(0);
        return item.startTime;
    }

    public void add(ScheduleAdItem item) {
        items.add(item);
    }
}
