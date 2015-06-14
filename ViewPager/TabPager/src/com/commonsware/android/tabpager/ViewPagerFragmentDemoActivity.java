/***
  Copyright (c) 2012 CommonsWare, LLC
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

package com.commonsware.android.tabpager;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public class ViewPagerFragmentDemoActivity extends
    Activity implements ActionBar.TabListener,
    OnPageChangeListener {
  private static final String KEY_POSITION="position";
  private ViewPager pager=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    pager=(ViewPager)findViewById(R.id.pager);
    pager.setAdapter(new SampleAdapter(getFragmentManager()));
    pager.setOnPageChangeListener(this);

    ActionBar bar=getActionBar();
    bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

    for (int i=0; i < 10; i++) {
      bar.addTab(bar.newTab()
                    .setText("Editor #" + String.valueOf(i + 1))
                    .setTabListener(this).setTag(i));
    }
  }

  @Override
  public void onRestoreInstanceState(Bundle state) {
    super.onRestoreInstanceState(state);
    
    pager.setCurrentItem(state.getInt(KEY_POSITION));
  }

  @Override
  public void onSaveInstanceState(Bundle state) {
    super.onSaveInstanceState(state);
    
    state.putInt(KEY_POSITION, pager.getCurrentItem());
  }

  @Override
  public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
    Integer position=(Integer)tab.getTag();

    pager.setCurrentItem(position);
  }

  @Override
  public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    // no-op
  }

  @Override
  public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    // no-op
  }

  @Override
  public void onPageScrollStateChanged(int arg0) {
    // no-op
  }

  @Override
  public void onPageScrolled(int arg0, float arg1, int arg2) {
    // no-op
  }

  @Override
  public void onPageSelected(int position) {
    getActionBar().setSelectedNavigationItem(position);
  }
}