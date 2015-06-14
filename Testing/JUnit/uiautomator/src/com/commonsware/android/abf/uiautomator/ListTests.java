/***
  Copyright (c) 2013 CommonsWare, LLC
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

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

public class ListTests extends UiAutomatorTestCase {
  private static final String[] items= { "lorem", "ipsum", "dolor",
      "sit", "amet", "consectetuer", "adipiscing", "elit", "morbi",
      "vel", "ligula", "vitae", "arcu", "aliquet", "mollis", "etiam",
      "vel", "erat", "placerat", "ante", "porttitor", "sodales",
      "pellentesque", "augue", "purus" };

  @Override
  public void setUp() throws UiObjectNotFoundException {
    openActivity();
  }

  @Override
  public void tearDown() {
    getUiDevice().pressBack();
    getUiDevice().pressBack();
  }

  public void testContents() throws UiObjectNotFoundException {
    UiScrollable words=
        new UiScrollable(
                         new UiSelector().className("android.widget.ListView"));

    words.setAsVerticalList();

    for (String s : items) {
      assertNotNull("Could not find " + s,
                    words.getChildByText(new UiSelector().className("android.widget.TextView"),
                                         s));
    }
  }

  public void testAdd() throws UiObjectNotFoundException {
    UiObject add=new UiObject(new UiSelector().text("Word"));

    add.setText("snicklefritz");
    getUiDevice().pressEnter();

    UiScrollable words=
        new UiScrollable(
                         new UiSelector().className("android.widget.ListView"));

    words.setAsVerticalList();

    assertNotNull("Could not find snicklefritz",
                  words.getChildByText(new UiSelector().className("android.widget.TextView"),
                                       "snicklefritz"));
  }

  private void openActivity() throws UiObjectNotFoundException {
    getUiDevice().pressHome();

    UiObject allAppsButton=
        new UiObject(new UiSelector().description("Apps"));

    allAppsButton.clickAndWaitForNewWindow();

    UiObject appsTab=new UiObject(new UiSelector().text("Apps"));

    appsTab.click();

    UiScrollable appViews=
        new UiScrollable(new UiSelector().scrollable(true));

    appViews.setAsHorizontalList();

    UiObject ourApp=
        appViews.getChildByText(new UiSelector().className("android.widget.TextView"),
                                "Action Bar Fragment Demo");

    ourApp.clickAndWaitForNewWindow();

    UiObject appValidation=
        new UiObject(
                     new UiSelector().packageName("com.commonsware.android.abf"));

    assertTrue("Could not open test app", appValidation.exists());
  }
}
