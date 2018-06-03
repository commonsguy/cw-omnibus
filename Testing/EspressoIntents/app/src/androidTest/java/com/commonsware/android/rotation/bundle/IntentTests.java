/***
 Copyright (c) 2016 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.rotation.bundle;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.provider.ContactsContract;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.LinearLayout;
import junit.framework.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class IntentTests {
  @Rule
  public final IntentsTestRule<RotationBundleDemo> main
    =new IntentsTestRule(RotationBundleDemo.class, true);

  @Test
  public void canceledPick() {
    Instrumentation.ActivityResult result=
      new Instrumentation.ActivityResult(Activity.RESULT_CANCELED,
        null);

    intending(hasAction(Intent.ACTION_PICK)).respondWith(result);

    onView(withId(R.id.pick)).perform(click());

    intended(allOf(
      hasAction(Intent.ACTION_PICK),
      hasData(ContactsContract.Contacts.CONTENT_URI)));

    onView(withId(R.id.view)).check(matches(not(isEnabled())));
  }

  @Test
  public void stubPick() {
    Instrumentation.ActivityResult result=
      new Instrumentation.ActivityResult(Activity.RESULT_OK,
        new Intent(null, ContactsContract.Contacts.CONTENT_URI));

    intending(hasAction(Intent.ACTION_PICK)).respondWith(result);

    onView(withId(R.id.pick)).perform(click());

    intended(allOf(
      hasAction(Intent.ACTION_PICK),
      hasData(ContactsContract.Contacts.CONTENT_URI)));

    onView(withId(R.id.view)).check(matches(isEnabled()));
  }

  // inspired by http://stackoverflow.com/a/35139887/115145

  @Test
  public void recreate() {
    stubPick();

    InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
      @Override
      public void run() {
        main.getActivity().recreate();
      }
    });

    onView(withId(R.id.view)).check(matches(isEnabled()));
  }

  @Test
  public void orientation() {
    int original=testOrientation();

    rotate();

    int postRotate=testOrientation();

    Assert.assertFalse("orientation changed", original==postRotate);
  }

  private int testOrientation() {
    int orientation=getOrientation();

    if (orientation==Configuration.ORIENTATION_LANDSCAPE) {
      onView(withId(R.id.content))
        .check(new OrientationAssertion(LinearLayout.HORIZONTAL));
    }
    else {
      onView(withId(R.id.content))
        .check(new OrientationAssertion(LinearLayout.VERTICAL));
    }

    return(orientation);
  }

  // following methods inspired by
  // http://blog.sqisland.com/2015/10/espresso-save-and-restore-state.html

  private int getOrientation() {
    return(InstrumentationRegistry
      .getTargetContext()
      .getResources()
      .getConfiguration()
      .orientation);
  }

  private void rotate() {
    int target=
      (getOrientation()==Configuration.ORIENTATION_LANDSCAPE ?
        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT :
        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

    main.getActivity().setRequestedOrientation(target);
  }

  static class OrientationAssertion implements ViewAssertion {
    private final int orientation;

    OrientationAssertion(int orientation) {
      this.orientation=orientation;
    }

    @Override
    public void check(View view,
                      NoMatchingViewException noViewFoundException) {
      Assert.assertTrue(view instanceof LinearLayout);
      Assert.assertEquals(orientation,
        ((LinearLayout)view).getOrientation());
    }
  }
}
