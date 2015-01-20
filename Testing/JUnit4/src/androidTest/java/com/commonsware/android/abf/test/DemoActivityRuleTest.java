/***
	Copyright (c) 2015 CommonsWare, LLC
	Licensed under the Apache License, Version 2.0 (the "License"); you may not
	use this file except in compliance with the License. You may obtain a copy
	of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
	by applicable law or agreed to in writing, software distributed under the
	License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
	OF ANY KIND, either express or implied. See the License for the specific
	language governing permissions and limitations under the License.
	
	From _The Busy Coder's Guide to Android Development_
		http://commonsware.com/Android
 */

package com.commonsware.android.abf.test;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ListView;
import com.commonsware.android.abf.ActionBarFragmentActivity;
import com.jakewharton.test.ActivityRule;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.lang.reflect.Field;

@RunWith(AndroidJUnit4.class)
public class DemoActivityRuleTest {
  private ListView list=null;
  @Rule public final ActivityRule<ActionBarFragmentActivity> main
      =new ActivityRule(ActionBarFragmentActivity.class);

  @Before
  public void init() {
    list=(ListView)main.get().findViewById(android.R.id.list);
  }

  @Test
  public void listCount() {
    Assert.assertEquals(25, list.getAdapter().getCount());
  }

  @Test
  public void keyEvents() {
    sendKeys("4*DPAD_DOWN");
    Assert.assertEquals(4, list.getSelectedItemPosition());
  }

  // following cloned from AOSP with slight modifications
  // Copyright (C) 2007 The Android Open Source Project

  /**
   * Sends a series of key events through instrumentation and waits for idle. The sequence
   * of keys is a string containing the key names as specified in KeyEvent, without the
   * KEYCODE_ prefix. For instance: sendKeys("DPAD_LEFT A B C DPAD_CENTER"). Each key can
   * be repeated by using the N* prefix. For instance, to send two KEYCODE_DPAD_LEFT, use
   * the following: sendKeys("2*DPAD_LEFT").
   *
   * @param keysSequence The sequence of keys.
   */
  public void sendKeys(String keysSequence) {
    final String[] keys = keysSequence.split(" ");
    final int count = keys.length;

    final Instrumentation instrumentation = main.instrumentation();

    for (int i = 0; i < count; i++) {
      String key = keys[i];
      int repeater = key.indexOf('*');

      int keyCount;
      try {
        keyCount = repeater == -1 ? 1 : Integer.parseInt(key.substring(0, repeater));
      } catch (NumberFormatException e) {
        Log.w("ActivityTestCase", "Invalid repeat count: " + key);
        continue;
      }

      if (repeater != -1) {
        key = key.substring(repeater + 1);
      }

      for (int j = 0; j < keyCount; j++) {
        try {
          final Field keyCodeField = KeyEvent.class.getField("KEYCODE_" + key);
          final int keyCode = keyCodeField.getInt(null);
          try {
            instrumentation.sendKeyDownUpSync(keyCode);
          } catch (SecurityException e) {
            // Ignore security exceptions that are now thrown
            // when trying to send to another app, to retain
            // compatibility with existing tests.
          }
        } catch (NoSuchFieldException e) {
          Log.w("ActivityTestCase", "Unknown keycode: KEYCODE_" + key);
          break;
        } catch (IllegalAccessException e) {
          Log.w("ActivityTestCase", "Unknown keycode: KEYCODE_" + key);
          break;
        }
      }
    }

    instrumentation.waitForIdleSync();
  }
}