/***
 Copyright (c) 2017 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.notify.channel;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import java.util.ArrayList;

public class MainActivity extends Activity {
  private static final String GROUP_UPDATES="group_updates";
  private static final String GROUP_PROMO="group_promo";
  private static final String CHANNEL_CONTENT="channel_content";
  private static final String CHANNEL_BATTLE="channel_battle";
  private static final String CHANNEL_COINS="channel_coins";
  private static final int NOTIF_ID_CONTENT=1337;
  private static final int NOTIF_ID_BATTLE=NOTIF_ID_CONTENT+1;
  private static final int NOTIF_ID_COINS=NOTIF_ID_CONTENT+2;
  private NotificationManager mgr;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mgr=getSystemService(NotificationManager.class);

    if (mgr.getNotificationChannel(CHANNEL_CONTENT)==null) {
      initGroups();
      initContentChannel();
      initBattleChannel();
      initCoinsChannel();
    }

    setContentView(R.layout.activity_main);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.actions, menu);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId()==R.id.settings) {
      Intent i=new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);

      i.putExtra(Settings.EXTRA_CHANNEL_ID, CHANNEL_BATTLE);
      i.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
      startActivity(i);
    }

    return super.onOptionsItemSelected(item);
  }

  public void raiseContent(View view) {
    Notification n=new Notification.Builder(MainActivity.this, CHANNEL_CONTENT)
      .setContentTitle(getString(R.string.notif_content_title))
      .setContentText(getString(R.string.notify_content_text))
      .setSmallIcon(android.R.drawable.stat_sys_warning)
      .build();

    mgr.notify(NOTIF_ID_CONTENT, n);
  }

  public void raiseBattle(View view) {
    Notification n=new Notification.Builder(MainActivity.this, CHANNEL_BATTLE)
      .setContentTitle(getString(R.string.notif_battle_title))
      .setContentText(getString(R.string.notif_battle_text))
      .setSmallIcon(android.R.drawable.stat_sys_warning)
      .setBadgeIconType(Notification.BADGE_ICON_SMALL)
      .setColor(Color.RED)
      .setColorized(true)
      .build();

    mgr.notify(NOTIF_ID_BATTLE, n);
  }

  public void raiseCoins(View view) {
    Notification n=new Notification.Builder(MainActivity.this, CHANNEL_COINS)
      .setContentTitle(getString(R.string.notif_coins_title))
      .setContentText(getString(R.string.notif_coins_text))
      .setSmallIcon(android.R.drawable.stat_sys_warning)
      .setTimeoutAfter(5000)
      .build();

    mgr.notify(NOTIF_ID_COINS, n);
  }

  private void initGroups() {
    ArrayList<NotificationChannelGroup> groups=new ArrayList<>();

    groups.add(new NotificationChannelGroup(GROUP_UPDATES,
      getString(R.string.group_name_updates)));
    groups.add(new NotificationChannelGroup(GROUP_PROMO,
      getString(R.string.group_name_promo)));

    mgr.createNotificationChannelGroups(groups);
  }

  private void initContentChannel() {
    NotificationChannel channel=
      new NotificationChannel(CHANNEL_CONTENT,
        getString(R.string.channel_name_content),
        NotificationManager.IMPORTANCE_LOW);

    channel.setGroup(GROUP_UPDATES);
    mgr.createNotificationChannel(channel);
  }

  private void initBattleChannel() {
    NotificationChannel channel=
      new NotificationChannel(CHANNEL_BATTLE,
        getString(R.string.channel_name_battle),
        NotificationManager.IMPORTANCE_HIGH);

    channel.setGroup(GROUP_UPDATES);
    channel.setShowBadge(true);
    mgr.createNotificationChannel(channel);
  }

  private void initCoinsChannel() {
    NotificationChannel channel=
      new NotificationChannel(CHANNEL_COINS,
        getString(R.string.channel_name_coins),
        NotificationManager.IMPORTANCE_DEFAULT);

    channel.setGroup(GROUP_PROMO);
    mgr.createNotificationChannel(channel);
  }
}
