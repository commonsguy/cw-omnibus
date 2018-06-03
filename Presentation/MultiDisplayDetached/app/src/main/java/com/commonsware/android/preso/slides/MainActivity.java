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
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import com.commonsware.cwac.crossport.design.widget.TabLayout;
import org.greenrobot.eventbus.EventBus;
import static com.commonsware.android.preso.slides.ControlReceiver.EXTRA_DELTA;
import static com.commonsware.android.preso.slides.ControlReceiver.EXTRA_STOP;

public class MainActivity extends Activity
  implements TabLayout.OnTabSelectedListener, DisplayManager.DisplayListener {
  private static final String CHANNEL_WHATEVER="channel_whatever";
  private static final int PI_NEXT=1234;
  private static final int PI_PREVIOUS=PI_NEXT+1;
  private static final int PI_STOP=PI_NEXT+2;
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
        showNotification();
        finish();

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
    EventBus
      .getDefault()
      .postSticky(new PresentationActivity.SlidePositionEvent(tab.getPosition()));
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

  private void showNotification() {
    NotificationManager mgr=
      (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O &&
      mgr.getNotificationChannel(CHANNEL_WHATEVER)==null) {
      mgr.createNotificationChannel(new NotificationChannel(CHANNEL_WHATEVER,
        "Whatever", NotificationManager.IMPORTANCE_DEFAULT));
    }

    NotificationCompat.Builder b=
      new NotificationCompat.Builder(this, CHANNEL_WHATEVER)
        .setOngoing(true)
        .setContentTitle("Presentation!")
        .setSmallIcon(android.R.drawable.stat_notify_more)
        .addAction(android.R.drawable.ic_media_previous,
          getString(R.string.action_previous), buildPreviousPendingIntent())
        .addAction(android.R.drawable.ic_media_next,
          getString(R.string.action_next), buildNextPendingIntent())
        .addAction(android.R.drawable.ic_media_pause,
          getString(R.string.action_stop), buildStopPendingIntent());

    mgr.notify(1337, b.build());
  }

  private PendingIntent buildPreviousPendingIntent() {
    Intent i=new Intent(this, ControlReceiver.class).putExtra(EXTRA_DELTA, -1);

    return(PendingIntent.getBroadcast(this, PI_PREVIOUS, i, 0));
  }

  private PendingIntent buildNextPendingIntent() {
    Intent i=new Intent(this, ControlReceiver.class).putExtra(EXTRA_DELTA, 1);

    return(PendingIntent.getBroadcast(this, PI_NEXT, i, 0));
  }

  private PendingIntent buildStopPendingIntent() {
    Intent i=new Intent(this, ControlReceiver.class).putExtra(EXTRA_STOP, true);

    return(PendingIntent.getBroadcast(this, PI_STOP, i, 0));
  }
}