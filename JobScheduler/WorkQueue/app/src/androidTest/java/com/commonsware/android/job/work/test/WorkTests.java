/***
 Copyright (c) 2017 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.job.work.test;

import android.app.job.JobInfo;
import android.app.job.JobWorkItem;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.commonsware.android.job.work.WorkService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class WorkTests {
  private static final String URL=
    "https://commonsware.com/Android/Android-1_0-CC.pdf";
  private static final String EXPECTED_HASH_HEX=
    "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
  private CountDownLatch latch;
  private Exception e=null;
  private HashSet<Integer> workIndices=new HashSet<>();

  @Test
  public void testWork() throws Exception {
    Random r=new Random();
    int firstBatchCount=4+r.nextInt(4);
    int secondBatchCount=4+r.nextInt(4);

    latch=new CountDownLatch(firstBatchCount+secondBatchCount);

    EventSink sink=new EventSink();

    EventBus.getDefault().register(sink);

    try {
      JobInfo jobInfo=null;
      ArrayList<JobWorkItem> items=new ArrayList<>();

      for (int i=0;i<firstBatchCount;i++) {
        items.add(WorkService.buildWorkItem(i, URL));
      }

      jobInfo=WorkService.enqueueWork(InstrumentationRegistry.getTargetContext(),
        jobInfo, items);

      SystemClock.sleep(1000);

      items.clear();

      for (int i=0;i<secondBatchCount;i++) {
        items.add(WorkService.buildWorkItem(i+firstBatchCount, URL));
      }

      WorkService.enqueueWork(InstrumentationRegistry.getTargetContext(),
        jobInfo, items);

      latch.await(firstBatchCount+secondBatchCount, TimeUnit.SECONDS);

      if (e!=null) {
        throw e;
      }
    }
    finally {
      EventBus.getDefault().unregister(sink);
    }

    assertEquals(firstBatchCount+secondBatchCount, workIndices.size());
  }

  private class EventSink {
    @Subscribe(threadMode =ThreadMode.ASYNC)
    public void onWorkResult(WorkService.Result result) {
      workIndices.add(result.workIndex);

      if (result.e!=null) {
        WorkTests.this.e=result.e;
      }
      else  {
        String hash=result.hash.hex();

        if (!EXPECTED_HASH_HEX.equals(hash)) {
          WorkTests.this.e=
            new IllegalStateException(String.format("Expected hash of %s, received %s",
              EXPECTED_HASH_HEX, hash));
        }
      }

      latch.countDown();
    }
  }
}
