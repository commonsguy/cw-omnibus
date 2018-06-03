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

package com.commonsware.android.design.til;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class TILTest {
  private static final String URL="https://commonsware.com";

  @Rule
  public final IntentsTestRule<LaunchDemo> main=
    new IntentsTestRule<>(LaunchDemo.class, true);

  @Test
  public void til() {
    onView(withTILHint("URL"))
      .perform(typeText(URL), closeSoftKeyboard());

    Instrumentation.ActivityResult result=
      new Instrumentation.ActivityResult(Activity.RESULT_CANCELED,
        null);

    intending(hasAction(Intent.ACTION_VIEW)).respondWith(result);

    onView(withId(R.id.browse)).perform(click());

    intended(allOf(hasAction(Intent.ACTION_VIEW), hasData(URL)));
  }

  private Matcher<View> withTILHint(CharSequence text) {
    return(new TILHintMatcher(is(text)));
  }

  private static class TILHintMatcher
    extends BoundedMatcher<View, TextInputEditText> {
    private final Matcher<CharSequence> textMatcher;

    TILHintMatcher(Matcher<CharSequence> textMatcher) {
      super(TextInputEditText.class);

      this.textMatcher=textMatcher;
    }

    @Override
    protected boolean matchesSafely(TextInputEditText item) {
      return(textMatcher.matches(item.getHint()));
    }

    @Override
    public void describeTo(Description description) {
      description.appendText("with hint: ");
      textMatcher.describeTo(description);
    }
  }
}
