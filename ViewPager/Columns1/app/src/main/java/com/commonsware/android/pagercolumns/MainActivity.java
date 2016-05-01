/***
  Copyright (c) 2012-2013 CommonsWare, LLC
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

package com.commonsware.android.pagercolumns;

import android.app.Activity;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class MainActivity extends Activity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    ViewPager pager=(ViewPager)findViewById(R.id.pager);

    if (pager==null) {
      if (getFragmentManager().findFragmentById(R.id.editor1)==null) {
        FragmentPagerAdapter adapter=buildAdapter();

        getFragmentManager().beginTransaction()
                                   .add(R.id.editor1,
                                        adapter.getItem(0))
                                   .add(R.id.editor2,
                                        adapter.getItem(1))
                                   .add(R.id.editor3,
                                        adapter.getItem(2)).commit();
      }
    }
    else {
      pager.setAdapter(buildAdapter());
    }
  }

  private FragmentPagerAdapter buildAdapter() {
    return(new SampleAdapter(this, getFragmentManager()));
  }
}