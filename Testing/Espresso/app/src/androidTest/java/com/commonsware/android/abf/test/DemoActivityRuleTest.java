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
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.commonsware.android.abf.ActionBarFragmentActivity;
import junit.framework.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
public class DemoActivityRuleTest {
  @Rule public final ActivityTestRule<ActionBarFragmentActivity> main
      =new ActivityTestRule(ActionBarFragmentActivity.class, true);

  @Test
  public void listCount() {
    onView(withId(android.R.id.list))
      .check(new AdapterCountAssertion(25));
  }

  @Test
  public void keyEvents() {
    onView(withId(android.R.id.list))
      .perform(pressKey(KeyEvent.KEYCODE_DPAD_DOWN),
        pressKey(KeyEvent.KEYCODE_DPAD_DOWN),
        pressKey(KeyEvent.KEYCODE_DPAD_DOWN),
        pressKey(KeyEvent.KEYCODE_DPAD_DOWN))
      .check(new ListSelectionAssertion(3));
  }

  @Test
  public void scrollToBottom() {
    onData(anything())
      .inAdapterView(withId(android.R.id.list))
      .atPosition(24)
      .check(matches(withText("purus")));
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
  }

  static class ListSelectionAssertion implements ViewAssertion {
    private final int position;

    ListSelectionAssertion(int position) {
      this.position=position;
    }

    @Override
    public void check(View view,
                      NoMatchingViewException noViewFoundException) {
      Assert.assertTrue(view instanceof ListView);
      Assert.assertEquals(position,
        ((ListView)view).getSelectedItemPosition());
    }
  }
}