/***
  Copyright (c) 2008-2016 CommonsWare, LLC
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

import android.view.KeyEvent;
import android.widget.ListView;
import com.commonsware.android.abf.ActionBarFragmentActivity;
import com.commonsware.android.abf.BuildConfig;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants=BuildConfig.class, sdk=16)
public class DemoActivityTest {
  private ListView list=null;

  @Before
  public void setUp() throws Exception {
    ActionBarFragmentActivity activity=
      Robolectric.setupActivity(ActionBarFragmentActivity.class);

    list=(ListView)activity.findViewById(android.R.id.list);
  }

  @Test
  public void listCount() {
    Assert.assertEquals(25, list.getAdapter().getCount());
  }
}