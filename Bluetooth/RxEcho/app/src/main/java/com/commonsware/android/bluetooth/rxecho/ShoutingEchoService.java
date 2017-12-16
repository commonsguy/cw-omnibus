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

package com.commonsware.android.bluetooth.rxecho;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.arch.lifecycle.MutableLiveData;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.github.davidmoten.rx2.Bytes;
import com.github.ivbaranov.rxbluetooth.RxBluetooth;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.UUID;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ShoutingEchoService extends Service {
  private static final String TAG="RxEcho";
  static final UUID SERVICE_ID=
      UUID.fromString("20c6de08-2cf5-4ca2-96af-cb0a45055d37");
  static final MutableLiveData<Status> STATUS=new MutableLiveData<>();
  private static final String CHANNEL_WHATEVER="channel_whatever";
  private static final String ACTION_STOP="stop";
  private RxBluetooth rxBluetooth;
  private Disposable connectionSub;
  private BluetoothSocket socket;

  @Override
  public void onCreate() {
    super.onCreate();

    rxBluetooth=new RxBluetooth(getApplicationContext());
    acceptConnections();

    NotificationManager mgr=
      (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O &&
      mgr.getNotificationChannel(CHANNEL_WHATEVER)==null) {
      mgr.createNotificationChannel(new NotificationChannel(CHANNEL_WHATEVER,
        "Whatever", NotificationManager.IMPORTANCE_DEFAULT));
    }

    startForeground(1338, buildForegroundNotification());

    STATUS.postValue(Status.IS_RUNNING);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (ACTION_STOP.equals(intent.getAction())) {
      stopSelf();
    }

    return(START_NOT_STICKY);
  }

  @Override
  public void onDestroy() {
    if (connectionSub!=null) {
      connectionSub.dispose();
    }

    disconnect();
    stopForeground(true);
    STATUS.postValue(Status.NOT_RUNNING);

    super.onDestroy();
  }

  @Override
  public IBinder onBind(Intent intent) {
    throw new UnsupportedOperationException("Um, no. Just... no.");
  }

  private void disconnect() {
    if (socket!=null) {
      try {
        socket.close();
      }
      catch (IOException e) {
        Log.e(TAG, "Exception from Bluetooth", e);
      }
    }
  }

  private Notification buildForegroundNotification() {
    NotificationCompat.Builder b=
      new NotificationCompat.Builder(this, CHANNEL_WHATEVER);

    b.setOngoing(true)
      .setContentTitle(getString(R.string.msg_foreground))
      .setSmallIcon(R.drawable.ic_stat_ping)
      .addAction(android.R.drawable.ic_media_pause, getString(R.string.msg_stop),
        buildStopPendingIntent());

    return(b.build());
  }

  private PendingIntent buildStopPendingIntent() {
    Intent i=new Intent(this, getClass()).setAction(ACTION_STOP);

    return(PendingIntent.getService(this, 0, i, 0));
  }

  private void acceptConnections() {
    connectionSub=rxBluetooth
      .observeBluetoothSocket(getString(R.string.app_name), SERVICE_ID)
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.computation())
      .subscribe(this::operateServer,
        throwable -> Log.e(getClass().getSimpleName(),
          "Exception from Bluetooth", throwable));
  }

  private void operateServer(BluetoothSocket socket) throws IOException {
    disconnect();
    this.socket=socket;
    acceptConnections();

    final PrintWriter out=
      new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

    Bytes.from(socket.getInputStream())
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.computation())
      .subscribe(bytes -> {
        out.print(new String(bytes).toUpperCase());
        out.flush();
      }, throwable -> out.close());
  }

  static class Status {
    static final Status IS_RUNNING=new Status(true);
    static final Status NOT_RUNNING=new Status(false);
    final boolean isRunning;

    private Status(boolean isRunning) {
      this.isRunning=isRunning;
    }
  }
}
