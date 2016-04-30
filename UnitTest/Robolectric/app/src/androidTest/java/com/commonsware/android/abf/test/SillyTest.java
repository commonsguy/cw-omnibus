/***
  Copyright (c) 2008-2015 CommonsWare, LLC
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

package com.commonsware.android.abf.test;

import android.support.test.runner.AndroidJUnit4;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SillyTest {
  @BeforeClass
  static public void doThisFirstOnlyOnce() {
    // do initialization here, run once for all SillyTest tests
  }

  @Before
  public void doThisFirst() {
    // do initialization here, run on every test method
  }

  @After
  public void doThisLast() {
    // do termination here, run on every test method
  }

  @AfterClass
  static public void doThisLastOnlyOnce() {
    // do termination here, run once for all SillyTest tests
  }

  @Test
  public void thisIsReallySilly() {
    Assert.assertEquals("bit got flipped by cosmic rays", 1, 1);
  }
}