/***
  Copyright (c) 2012-15 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  Covered in detail in the book _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.tablayout;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import com.commonsware.cwac.crossport.design.widget.TabLayout;

public class MainActivity extends FragmentActivity {
  private SampleAdapter adapter;
  private TabLayout tabs;
  private ViewPager pager;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    pager=findViewById(R.id.pager);
    adapter=new SampleAdapter(this, getSupportFragmentManager());
    pager.setAdapter(adapter);

    tabs=findViewById(R.id.tabs);
    tabs.setupWithViewPager(pager);
    tabs.setTabMode(TabLayout.MODE_FIXED);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.actions, menu);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId()==R.id.fixed) {
      item.setChecked(!item.isChecked());

      if (item.isChecked()) {
        adapter.setPageCount(3);
        tabs.setTabMode(TabLayout.MODE_FIXED);
      }
      else {
        adapter.setPageCount(10);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
      }

      adapter.notifyDataSetChanged();

      if (pager.getCurrentItem()>=3) {
        pager.setCurrentItem(2);
      }

      return(true);
    }

    return(super.onOptionsItemSelected(item));
  }
}