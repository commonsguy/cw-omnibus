package com.commonsware.android.databind.basic;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.widget.LinearLayout;
import android.widget.ListView;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static junit.framework.TestCase.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class UiAutoTest {
  private UiDevice device;

  @Rule
  public final ActivityTestRule<MainActivity> main
    =new ActivityTestRule(MainActivity.class, true);

  @Before
  public void setUp() {
    device=UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
  }

  @After
  public void tearDown() {
    device.pressHome();
  }

  @Test
  public void testContents() throws UiObjectNotFoundException {
    UiScrollable items=
      new UiScrollable(new UiSelector().className(ListView.class.getCanonicalName()));

    items.setAsVerticalList();

    UiObject firstRow=items
      .getChildByInstance(new UiSelector().className(LinearLayout.class.getCanonicalName()),
        0);

    firstRow.clickAndWaitForNewWindow();

    UiObject urlBar=device.findObject(new UiSelector().textContains("https://stackoverflow.com/"));

    assertNotNull(urlBar);
  }
}
