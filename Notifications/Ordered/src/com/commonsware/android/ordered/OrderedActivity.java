/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Advanced Android Development_
    http://commonsware.com/AdvAndroid
*/


package com.commonsware.android.ordered;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import java.util.Date;

public class OrderedActivity extends Activity {
  private Button notice=null;
  private AlarmManager mgr=null;
  private PendingIntent pi=null;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    
    notice=(Button)findViewById(R.id.notice);
    
    ((NotificationManager)getSystemService(NOTIFICATION_SERVICE))
                                                      .cancelAll();
    
    mgr=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
    
    Intent i=new Intent(this, NoticeService.class);
    
    pi=PendingIntent.getService(this, 0, i, 0);
    
    cancelAlarm(null);
    
    mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                      SystemClock.elapsedRealtime()+1000,
                      5000,
                      pi);
  }
  
  @Override
  public void onResume() {
    super.onResume();
    
    IntentFilter filter=new IntentFilter(NoticeService.BROADCAST);
    
    filter.setPriority(2);
    registerReceiver(onNotice, filter);
  }
  
  @Override
  public void onPause() {
    super.onPause();
    
    unregisterReceiver(onNotice);
  }
  
  public void cancelAlarm(View v) {
    mgr.cancel(pi);
  }
  
  private BroadcastReceiver onNotice=new BroadcastReceiver() {
    public void onReceive(Context ctxt, Intent i) {
      notice.setText(new Date().toString());
      abortBroadcast();
    }
  };
}