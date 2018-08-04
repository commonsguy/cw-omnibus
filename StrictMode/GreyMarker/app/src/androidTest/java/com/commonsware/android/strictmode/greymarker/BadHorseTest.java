package com.commonsware.android.strictmode.greymarker;

import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class BadHorseTest {
  @Test
  public void closeButonId() throws IllegalAccessException,
    NoSuchFieldException, ClassNotFoundException {
    assertNotEquals(0, BadHorse.getCloseButtonId());
  }
}
