/***
 Copyright (c) 2015 CommonsWare, LLC
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

package com.commonsware.android.andcorder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class RecorderService extends Service {
  private static final String CHANNEL_WHATEVER="channel_whatever";
  private static final int NOTIFY_ID=9906;
  static final String EXTRA_RESULT_CODE="resultCode";
  static final String EXTRA_RESULT_INTENT="resultIntent";
  static final String ACTION_RECORD=
    BuildConfig.APPLICATION_ID+".RECORD";
  static final String ACTION_STOP=
    BuildConfig.APPLICATION_ID+".STOP";
  static final String ACTION_SHUTDOWN=
    BuildConfig.APPLICATION_ID+".SHUTDOWN";
  private boolean isForeground=false;
  private int resultCode;
  private Intent resultData;
  private boolean recordOnNextStart=false;
  private RecordingSession session=null;

  @Override
  public int onStartCommand(Intent i, int flags, int startId) {
    if (i.getAction()==null) {
      resultCode=i.getIntExtra(EXTRA_RESULT_CODE, 1337);
      resultData=i.getParcelableExtra(EXTRA_RESULT_INTENT);

      if (recordOnNextStart) {
        startRecorder();
      }

      foregroundify(!recordOnNextStart);
      recordOnNextStart=false;
    }
    else if (ACTION_RECORD.equals(i.getAction())) {
      if (resultData!=null) {
        foregroundify(false);
        startRecorder();
      }
      else {
        Intent ui=
          new Intent(this, MainActivity.class)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(ui);
        recordOnNextStart=true;
      }
    }
    else if (ACTION_STOP.equals(i.getAction())) {
      foregroundify(true);
      stopRecorder();
    }
    else if (ACTION_SHUTDOWN.equals(i.getAction())) {
      stopSelf();
    }

    return(START_NOT_STICKY);
  }

  @Override
  public void onDestroy() {
    stopRecorder();
    stopForeground(true);

    super.onDestroy();
  }

  @Override
  public IBinder onBind(Intent intent) {
    throw new IllegalStateException("go away");
  }

  private void foregroundify(boolean showRecord) {
    NotificationManager mgr=
      (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O &&
      mgr.getNotificationChannel(CHANNEL_WHATEVER)==null) {
      mgr.createNotificationChannel(new NotificationChannel(CHANNEL_WHATEVER,
        "Whatever", NotificationManager.IMPORTANCE_DEFAULT));
    }

    NotificationCompat.Builder b=
      new NotificationCompat.Builder(this, CHANNEL_WHATEVER);

    b.setAutoCancel(true)
      .setDefaults(Notification.DEFAULT_ALL);

    b.setContentTitle(getString(R.string.app_name))
      .setSmallIcon(R.mipmap.ic_launcher)
      .setTicker(getString(R.string.app_name));

    if (showRecord) {
      b.addAction(R.drawable.ic_videocam_white_24dp,
        getString(R.string.notify_record), buildPendingIntent(ACTION_RECORD));
    }
    else {
      b.addAction(R.drawable.ic_stop_white_24dp,
        getString(R.string.notify_stop), buildPendingIntent(ACTION_STOP));
    }

    b.addAction(R.drawable.ic_eject_white_24dp,
      getString(R.string.notify_shutdown), buildPendingIntent(ACTION_SHUTDOWN));

    if (isForeground) {
      mgr.notify(NOTIFY_ID, b.build());
    }
    else {
      startForeground(NOTIFY_ID, b.build());
      isForeground=true;
    }
  }

  private PendingIntent buildPendingIntent(String action) {
    Intent i=new Intent(this, getClass());

    i.setAction(action);

    return(PendingIntent.getService(this, 0, i, 0));
  }

  synchronized private void startRecorder() {
    if (session==null) {
      MediaProjectionManager mgr=
        (MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);
      MediaProjection projection=
        mgr.getMediaProjection(resultCode, resultData);

      session=
        new RecordingSession(this, new RecordingConfig(this),
          projection);
      session.start();
    }
  }

  synchronized private void stopRecorder() {
    if (session!=null) {
      session.stop();
      session=null;
    }
  }
}
