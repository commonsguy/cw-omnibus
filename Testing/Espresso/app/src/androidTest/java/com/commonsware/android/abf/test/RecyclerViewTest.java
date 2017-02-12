/***
  Copyright (c) 2015-2016 CommonsWare, LLC
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

package com.commonsware.android.abf.test;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.commonsware.android.abf.rv.MainActivity;
import junit.framework.Assert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class RecyclerViewTest {
  @Rule public final ActivityTestRule<MainActivity> main
      =new ActivityTestRule(MainActivity.class, true);

  @Test
  public void listCount() {
    onView(Matchers.<View>instanceOf(RecyclerView.class))
      .check(new AdapterCountAssertion(25));
  }

  @Test
  public void scrollToBottom() {
    onView(withClassName(is(RecyclerView.class.getCanonicalName())))
      .perform(scrollToPosition(24))
      .check(matches(anything()));
  }

  static class AdapterCountAssertion implements ViewAssertion {
    private final int count;

    AdapterCountAssertion(int count) {
      this.count=count;
    }

    @Override
    public void check(View view,
                      NoMatchingViewException noViewFoundException) {
      Assert.assertTrue(view instanceof RecyclerView);
      Assert.assertEquals(count,
        ((RecyclerView)view).getAdapter().getItemCount());
    }
  }
}