package com.lemma.lemmasignagesdk.scheduleplayer;

import com.lemma.lemmasignagesdk.TestData;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.Schedule;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.ScheduleAdItemGrp;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;

@RunWith(RobolectricTestRunner.class)
public class ScheduleTest {

    @Test
    public void test_Parse_Empty_schedules() {
        String input = "{\n" +
                "    \"status\": 200,\n" +
                "    \"data\": {\n" +
                "        \"dur\": 0,\n" +
                "        \"trk\": \"https://track.lemmatechnologies.com/lemma/impr?ts=20191126132228&iid=99d73717-1021-11ea-b09c-02cc2a1e6ba0&pid=0&agid=0&aid=0&sid=${SITE_ID}&at=3&szid=2&did=178&cid=539&lid=1923&crid=4557&dur=15&gid=535559&aud=0&cp=0.001000&pp=0.000010&lf=0.000000&if=0.000000&impr=1.000000&bt=1&ic=0.000010&sec=0\",\n" +
                "        \"chk_Sum\": \"0d9f70468f6f14ffb6de1a6bc4ca696c80bbec2cb476a4b912f468be967b9a99\"\n" +
                "    },\n" +
                "    \"error\": \"\"\n" +
                "}";
        Schedule a = Schedule.parse(input);
        Error error = a.validate();
        Assert.assertNotNull(error);
    }

    @Test
    public void test_InConsistent_schedules() {
        String input = TestData.getInconsistentScheduleData();
        Schedule a = Schedule.parse(input);
        ArrayList<ScheduleAdItemGrp> agrps = a.adGroups();
        Error error = a.validate();
        Assert.assertNotNull(error);
    }
}