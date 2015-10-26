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


package com.commonsware.android.webserver.websockets;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import de.greenrobot.event.EventBus;

public class WebServerService extends Service implements Runnable {
  private AsyncHttpServer server;
  final private ArrayList<WebSocket> sockets=new ArrayList<WebSocket>();
  final private ScheduledExecutorService timer=
    Executors.newSingleThreadScheduledExecutor();

  @Override
  public void onCreate() {
    super.onCreate();

    server=new AsyncHttpServer();
    server.websocket("/ss", new WebSocketClientCallback());
    server.get("/.*", new AssetRequestCallback());
    server.listen(4999);

    raiseStartedEvent();
    foregroundify();

    timer.scheduleAtFixedRate(this, 3000, 3000, TimeUnit.MILLISECONDS);
  }

  @Override
  public int onStartCommand(Intent i, int flags, int startId) {
    return(START_NOT_STICKY);
  }

  @Override
  public void onDestroy() {
    timer.shutdownNow();
    EventBus.getDefault().removeAllStickyEvents();
    EventBus.getDefault().postSticky(new ServerStoppedEvent());
    server.stop();
    AsyncServer.getDefault().stop(); // no, really, I mean stop

    super.onDestroy();
  }

  @Override
  public IBinder onBind(Intent intent) {
    throw new UnsupportedOperationException("Go away");
  }

  @Override
  public void run() {
    for (WebSocket socket : sockets) {
      socket.send(new Date().toString());
    }
  }

  private void raiseStartedEvent() {
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
              "http://"+addr.getHostAddress()+":4999");
          }
        }
      }
    }
    catch (SocketException e) {
      Log.e(getClass().getSimpleName(), "Exception in IP addresses", e);
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
        if (path.length()==0 || "/".equals(path)) {
          path="index.html";
        }
        else if (path.startsWith("/")) {
          path=path.substring(1);
        }

        AssetFileDescriptor afd=getAssets().openFd(path);

        response.sendStream(afd.createInputStream(), afd.getLength());
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
    }
  }

  private class WebSocketClientCallback
    implements AsyncHttpServer.WebSocketRequestCallback {
    @Override
    public void onConnected(final WebSocket ws,
                            AsyncHttpServerRequest request) {
      sockets.add(ws);

      ws.setClosedCallback(new CompletedCallback() {
        @Override
        public void onCompleted(Exception ex) {
          if (ex!=null) {
            Log.e(getClass().getSimpleName(),
              "Exception with WebSocket", ex);
          }

          sockets.remove(ws);
        }
      });
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
}
