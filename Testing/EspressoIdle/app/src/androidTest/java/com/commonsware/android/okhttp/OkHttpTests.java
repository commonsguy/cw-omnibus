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

package com.commonsware.android.okhttp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.AdapterView;
import com.jakewharton.espresso.OkHttp3IdlingResource;
import junit.framework.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class OkHttpTests {
  @Rule
  public final ActivityTestRule<MainActivity> main
    =new ActivityTestRule(MainActivity.class, true);

  private static final String URL=
    "https://wares.commonsware.com/test.json";
  private static final String EXPECTED="{\"Hello\": \"world\"}";

  @Test
  public void syncTest() throws IOException {
    OkHttpClient client=new OkHttpClient.Builder().build();
    Request request=new Request.Builder().url(URL).build();
    Response response=client.newCall(request).execute();

    Assert.assertEquals(EXPECTED, response.body().string());
  }

  @Test
  public void unreliableAsyncTest() {
    onView(withId(android.R.id.list))
      .check(new AdapterCountAssertion(100));
  }

  @Test
  public void moreReliableAsyncTest() {
    IdlingResource idleWild=
      OkHttp3IdlingResource.create("okhttp3",
        main.getActivity().getOkHttpClient());

    IdlingRegistry.getInstance().register(idleWild);

    try {
      onView(withId(android.R.id.list))
        .check(new AdapterCountAssertion(100));
    }
    finally {
      IdlingRegistry.getInstance().unregister(idleWild);
    }
  }

  static class AdapterCountAssertion implements ViewAssertion {
    private final int count;

    AdapterCountAssertion(int count) {
      this.count=count;
    }

    @Override
    public void check(View view,
                      NoMatchingViewException noViewFoundException) {
      Assert.assertTrue(view instanceof AdapterView);
      Assert.assertEquals(count,
        ((AdapterView)view).getAdapter().getCount());
    }
  }}
