package com.commonsware.android.launchalot;

import android.test.ActivityInstrumentationTestCase;

/**
 * This is a simple framework for a test of an Application. See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.commonsware.android.launchalot.LaunchalotTest \
 * com.commonsware.android.launchalot.tests/android.test.InstrumentationTestRunner
 */
public class LaunchalotTest extends ActivityInstrumentationTestCase<Launchalot> {

    public LaunchalotTest() {
        super("com.commonsware.android.launchalot", Launchalot.class);
    }

}