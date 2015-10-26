/***
 Copyright (c) 2015 CommonsWare, LLC
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


package com.commonsware.android.webserver.secure;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import de.greenrobot.event.EventBus;

public class WebServerService extends Service {
  private static final int MAX_IDLE_TIME_SECONDS=60;
  private static final int MAX_SEQUENTIAL_INVALID_REQUESTS=10;
  private AsyncHttpServer server;
  private SecureRandom rng=new SecureRandom();
  private String rootPath;
  private ScheduledExecutorService timer=
    Executors.newSingleThreadScheduledExecutor();
  private ScheduledFuture<?> timeoutFuture;
  private int invalidRequestCount=0;

  @Override
  public void onCreate() {
    super.onCreate();

    ConnectivityManager mgr=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
    NetworkInfo ni=mgr.getActiveNetworkInfo();

    if (ni==null || ni.getType()==ConnectivityManager.TYPE_MOBILE) {
      EventBus.getDefault().post(new ServerStartRejectedEvent());
      stopSelf();
    }
    else {
      rootPath=
        "/"+new BigInteger(20, rng).toString(24).toUpperCase();

      server=new AsyncHttpServer();
      server.get("/.*", new AssetRequestCallback());
      server.listen(4999);

      raiseReadyEvent();
      foregroundify();
      timeoutFuture=timer.schedule(onTimeout,
        MAX_IDLE_TIME_SECONDS, TimeUnit.SECONDS);
    }
  }

  @Override
  public int onStartCommand(Intent i, int flags, int startId) {
    return(START_NOT_STICKY);
  }

  @Override
  public void onDestroy() {
    EventBus.getDefault().removeAllStickyEvents();
    EventBus.getDefault().postSticky(new ServerStoppedEvent());

    if (server!=null) {
      server.stop();
      AsyncServer.getDefault().stop(); // no, really, I mean stop
    }

    super.onDestroy();
  }

  @Override
  public IBinder onBind(Intent intent) {
    throw new UnsupportedOperationException("Go away");
  }

  private void raiseReadyEvent() {
    ServerStartedEvent event=new ServerStartedEvent();

    try {
      for (Enumeration<NetworkInterface> enInterfaces=
           NetworkInterface.getNetworkInterfaces();
           enInterfaces.hasMoreElements(); ) {
        NetworkInterface ni=enInterfaces.nextElement();

        for (Enumeration<InetAddress> enAddresses=
             ni.getInetAddresses();
             enAddresses.hasMoreElements(); ) {
          InetAddress addr=enAddresses.nextElement();

          if (addr instanceof Inet4Address) {
            event.addUrl(
              "http://"+addr.getHostAddress()+":4999"+rootPath+"/");
          }
        }
      }
    }
    catch (SocketException ex) {
      ex.printStackTrace();
    }

    EventBus.getDefault().removeAllStickyEvents();
    EventBus.getDefault().postSticky(event);
  }

  private void foregroundify() {
    NotificationCompat.Builder b=
      new NotificationCompat.Builder(this);
    Intent iActivity=new Intent(this, MainActivity.class);
    PendingIntent piActivity=
      PendingIntent.getActivity(this, 0, iActivity, 0);
    Intent iReceiver=new Intent(this, StopReceiver.class);
    PendingIntent piReceiver=
      PendingIntent.getBroadcast(this, 0, iReceiver, 0);

    b.setAutoCancel(true)
      .setDefaults(Notification.DEFAULT_ALL)
      .setContentTitle(getString(R.string.app_name))
      .setContentIntent(piActivity)
      .setSmallIcon(R.mipmap.ic_launcher)
      .setTicker(getString(R.string.app_name))
      .addAction(R.drawable.ic_stop_white_24dp,
        getString(R.string.notify_stop),
        piReceiver);

    startForeground(1337, b.build());
  }

  private void trackInvalidRequests() {
    invalidRequestCount++;

    if (invalidRequestCount>MAX_SEQUENTIAL_INVALID_REQUESTS) {
      stopSelf();
    }
  }

  private Runnable onTimeout=new Runnable() {
    @Override
    public void run() {
      stopSelf();
    }
  };

  private class AssetRequestCallback
    implements HttpServerRequestCallback {
    private final AssetManager assets;

    AssetRequestCallback() {
      assets=getAssets();
    }

    @Override
    public void onRequest(AsyncHttpServerRequest request,
                          AsyncHttpServerResponse response) {
      String path=request.getPath();

      try {
        if (path.startsWith(rootPath)) {
          path=path.substring(rootPath.length()+1);
        }
        else {
          handle404(response, path, null);
          return;
        }

        if (path.length()==0 || "/".equals(path)) {
          path="index.html";
        }
        else if (path.startsWith("/")) {
          path=path.substring(1);
        }

        AssetFileDescriptor afd=getAssets().openFd(path);

        response.sendStream(afd.createInputStream(),
          afd.getLength());
        timeoutFuture.cancel(false);
        timeoutFuture=timer.schedule(onTimeout,
          MAX_IDLE_TIME_SECONDS, TimeUnit.SECONDS);
        invalidRequestCount=0;
      }
      catch (IOException e) {
        handle404(response, path, e);
      }
    }

    private void handle404(AsyncHttpServerResponse response,
                           String path, Exception e) {
      Log.e(getClass().getSimpleName(), "Invalid URL: "+path, e);
      response.code(404);
      response.end();
      trackInvalidRequests();
    }
  }

  static class ServerStartedEvent {
    private ArrayList<String> urls=new ArrayList<String>();

    void addUrl(String url) {
      urls.add(url);
    }

    ArrayList<String> getUrls() {
      return (urls);
    }
  }

  static class ServerStoppedEvent {

  }

  static class ServerStartRejectedEvent {

  }
}
