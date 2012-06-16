package com.commonsware.android.anim;

import android.test.ActivityInstrumentationTestCase;

/**
 * This is a simple framework for a test of an Application. See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.commonsware.android.anim.SlidingPanelDemoTest \
 * com.commonsware.android.anim.tests/android.test.InstrumentationTestRunner
 */
public class SlidingPanelDemoTest extends ActivityInstrumentationTestCase<SlidingPanelDemo> {

    public SlidingPanelDemoTest() {
        super("com.commonsware.android.anim", SlidingPanelDemo.class);
    }

}