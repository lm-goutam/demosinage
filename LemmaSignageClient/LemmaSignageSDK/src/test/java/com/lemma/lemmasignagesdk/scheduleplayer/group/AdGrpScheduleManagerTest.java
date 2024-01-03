package com.lemma.lemmasignagesdk.scheduleplayer.group;

import com.lemma.lemmasignagesdk.scedule.scheduleplayer.Schedule;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.group.AdGrpScheduleManager;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.CountDownLatch;

@RunWith(RobolectricTestRunner.class)
public class AdGrpScheduleManagerTest extends TestCase {

    public final CountDownLatch latch = new CountDownLatch(1);

    @Test
    public void testFetchSchedule() throws InterruptedException {

        AdGrpScheduleManager manager = new AdGrpScheduleManager();
        manager.fetchSchedule(null, new AdGrpScheduleManager.CompletionCallback<Schedule>() {
            @Override
            public void onComplete(Error error, Schedule obj) {
                Assert.assertNotNull(obj);
                Assert.assertNull(error);
                latch.countDown();
            }
        });

        latch.await();
    }
}