/***
  Copyright (c) 2013-2017 CommonsWare, LLC
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

package com.commonsware.android.preso.slides;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import com.commonsware.cwac.crossport.design.widget.TabLayout;
import org.greenrobot.eventbus.EventBus;

public class MainActivity extends Activity
  implements TabLayout.OnTabSelectedListener, DisplayManager.DisplayListener {
  private ViewPager pager;
  private SlidesAdapter adapter;
  private DisplayManager dm;
  private MenuItem presoItem;
  private Display presoDisplay;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    pager=(ViewPager)findViewById(R.id.pager);
    adapter=new SlidesAdapter(this);
    pager.setAdapter(adapter);

    TabLayout tabs=(TabLayout)findViewById(R.id.tabs);

    tabs.setupWithViewPager(pager);
    tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
    tabs.addOnTabSelectedListener(this);

    if (iCanHazO()) {
      dm=getSystemService(DisplayManager.class);
      dm.registerDisplayListener(this, null);
      checkForPresentationDisplays();
    }
  }

  @Override
  protected void onDestroy() {
    if (dm!=null) {
      dm.unregisterDisplayListener(this);
    }

    super.onDestroy();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity_actions, menu);
    presoItem=menu.findItem(R.id.present);
    checkForPresentationDisplays();

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.present:
        Intent i=
          new Intent(this, PresentationActivity.class)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        Bundle opts=ActivityOptions
          .makeBasic()
          .setLaunchDisplayId(presoDisplay.getDisplayId())
          .toBundle();

        startActivity(i, opts);

        break;

      case R.id.first:
        pager.setCurrentItem(0);
        break;

      case R.id.last:
        pager.setCurrentItem(adapter.getCount() - 1);
        break;
    }

    return(super.onOptionsItemSelected(item));
  }

  @Override
  public void onTabSelected(TabLayout.Tab tab) {
    int position=tab.getPosition();
    int resourceId=adapter.getPageResource(position);

    EventBus
      .getDefault()
      .postSticky(new SlideChangedEvent(resourceId));
  }

  @Override
  public void onTabUnselected(TabLayout.Tab tab) {
    // unused
  }

  @Override
  public void onTabReselected(TabLayout.Tab tab) {
    // unused
  }

  @Override
  public void onDisplayAdded(int i) {
    checkForPresentationDisplays();
  }

  @Override
  public void onDisplayRemoved(int i) {
    checkForPresentationDisplays();
  }

  @Override
  public void onDisplayChanged(int i) {
    checkForPresentationDisplays();
  }

  private void checkForPresentationDisplays() {
    if (dm!=null && presoItem!=null) {
      Display[] displays=
        dm.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);

      if (displays.length>0) {
        presoItem.setEnabled(true);
        presoDisplay=displays[0];
      }
      else {
        presoItem.setEnabled(false);
        presoDisplay=null;
      }
    }
  }

  public static boolean iCanHazO() {
    return(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O);
  }

  static class SlideChangedEvent {
    final int resourceId;

    SlideChangedEvent(int resourceId) {
      this.resourceId=resourceId;
    }
  }
}