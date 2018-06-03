/***
  Copyright (c) 2016 CommonsWare, LLC
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

package com.commonsware.android.unittest;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;

@RunWith(MockitoJUnitRunner.class)
public class SOUnitTest {
  private CountDownLatch responseLatch;
  private SOQuestions questions;
  @Mock StackOverflowInterface mockSO;

  @Before
  public void setUp() {
    responseLatch=new CountDownLatch(1);
  }

  @Test(timeout=30000)
  public void fetchQuestions() throws InterruptedException {
    doAnswer(invocation -> {
      SOQuestions fakeQuestions=new SOQuestions();

      fakeQuestions.items=new ArrayList<>();

      Item fakeItem=new Item();

      fakeItem.link="https://commonsware.com";
      fakeItem.title="How Do I Fake It to Make It?";
      fakeQuestions.items.add(fakeItem);

      Callback<SOQuestions> realCB=
        (Callback<SOQuestions>)invocation.getArguments()[1];

      realCB.success(fakeQuestions, null);

      return(null);
    }).when(mockSO).questions(eq("android"), any(Callback.class));

    mockSO.questions("android", new Callback<SOQuestions>() {
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
    Assert.assertEquals(1, questions.items.size());

    for (Item item : questions.items) {
      Assert.assertNotNull(item.title);
      Assert.assertNotNull(item.link);
    }
  }
}