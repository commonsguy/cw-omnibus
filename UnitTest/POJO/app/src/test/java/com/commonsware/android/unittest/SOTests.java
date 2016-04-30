/***
 Copyright (c) 2016 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 From _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.unittest;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.concurrent.CountDownLatch;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SOTests {
  private CountDownLatch responseLatch;
  private SOQuestions questions;

  @Before
  public void setUp() {
    responseLatch=new CountDownLatch(1);
  }

  @Test(timeout=30000)
  public void fetchQuestions() throws InterruptedException {
    RestAdapter restAdapter=
      new RestAdapter.Builder()
        .setEndpoint("https://api.stackexchange.com")
        .build();
    StackOverflowInterface so=
      restAdapter.create(StackOverflowInterface.class);

    so.questions("android", new Callback<SOQuestions>() {
      @Override
      public void success(SOQuestions soQuestions,
                          Response response) {
        questions=soQuestions;
        responseLatch.countDown();
      }

      @Override
      public void failure(RetrofitError error) {
        responseLatch.countDown();
      }
    });

    responseLatch.await();

    Assert.assertNotNull(questions);
    Assert.assertEquals(30, questions.items.size());

    for (Item item : questions.items) {
      Assert.assertNotNull(item.title);
      Assert.assertNotNull(item.link);
    }
  }
}