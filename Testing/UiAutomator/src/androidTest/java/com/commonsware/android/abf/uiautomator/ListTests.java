/***
  Copyright (c) 2013-2015 CommonsWare, LLC
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

package com.commonsware.android.abf.uiautomator;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ListTests {
  private static final String[] items= { "lorem", "ipsum", "dolor",
      "sit", "amet", "consectetuer", "adipiscing", "elit", "morbi",
      "vel", "ligula", "vitae", "arcu", "aliquet", "mollis", "etiam",
      "vel", "erat", "placerat", "ante", "porttitor", "sodales",
      "pellentesque", "augue", "purus" };
  private UiDevice device;

  @Before
  public void setUp() throws UiObjectNotFoundException {
    device=UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    openActivity();
  }

  @After
  public void tearDown() {
    device.pressBack();
    device.pressBack();
  }

  @Test
  public void testContents() throws UiObjectNotFoundException {
    UiScrollable words=
        new UiScrollable(
                         new UiSelector().className("android.widget.ListView"));

    words.setAsVerticalList();

    for (String s : items) {
      Assert.assertNotNull("Could not find " + s,
          words.getChildByText(new UiSelector().className("android.widget.TextView"),
              s));
    }
  }

  @Test
  public void testAdd() throws UiObjectNotFoundException {
    UiObject add=device.findObject(new UiSelector().text("Word"));

    add.setText("snicklefritz");
    device.pressEnter();

    UiScrollable words=
        new UiScrollable(
                         new UiSelector().className("android.widget.ListView"));

    words.setAsVerticalList();

    Assert.assertNotNull("Could not find snicklefritz",
        words.getChildByText(new UiSelector().className("android.widget.TextView"),
            "snicklefritz"));
  }

  private void openActivity() throws UiObjectNotFoundException {
    device.pressHome();

    UiObject allAppsButton=
        device.findObject(new UiSelector().description("Apps"));

    allAppsButton.clickAndWaitForNewWindow();

    UiObject appsTab=device.findObject(new UiSelector().text("Apps"));

    appsTab.click();

    UiScrollable appViews=
        new UiScrollable(new UiSelector().scrollable(true));

    appViews.setAsHorizontalList();

    UiObject ourApp=
        appViews.getChildByText(new UiSelector().className("android.widget.TextView"),
                                "Action Bar Fragment Demo");

    ourApp.clickAndWaitForNewWindow();

    UiObject appValidation=
        device.findObject(new UiSelector().packageName("com.commonsware.android.abf"));

    Assert.assertTrue("Could not open test app", appValidation.exists());
  }
}
