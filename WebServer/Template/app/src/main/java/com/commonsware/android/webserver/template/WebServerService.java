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

package com.commonsware.android.webserver.template;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.io.AbstractTemplateLoader;
import com.github.jknack.handlebars.io.StringTemplateSource;
import com.github.jknack.handlebars.io.TemplateSource;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import de.greenrobot.event.EventBus;

public class WebServerService extends Service {
  private AsyncHttpServer server;
  private Handlebars handlebars;
  private Template t;

  @Override
  public void onCreate() {
    super.onCreate();

    handlebars=new Handlebars(new AssetTemplateLoader(getAssets()));

    try {
      t=handlebars.compile("demo.hbs");
      server=new AsyncHttpServer();
      server.get("/demo", new TemplateRequestCallback());
      server.get("/.*", new AssetRequestCallback());
      server.listen(4999);

      raiseReadyEvent();
      foregroundify();
    }
    catch (IOException e) {
      Log.e(getClass().getSimpleName(),
        "Exception starting Web server", e);
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
    server.stop();
    AsyncServer.getDefault().stop(); // no, really, I mean stop

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
              "http://"+addr.getHostAddress()+":4999");
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

        response.sendStream(afd.createInputStream(),
          afd.getLength());
      }
      catch (IOException e) {
        handle404(response, path);
      }
    }

    private void handle404(AsyncHttpServerResponse response,
                           String path) {
      Log.e(getClass().getSimpleName(),
        "Invalid URL: "+path);
      response.code(404);
      response.end();
    }
  }

  private class TemplateRequestCallback implements HttpServerRequestCallback {
    @Override
    public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
      try {
        DisplayMetrics metrics=new DisplayMetrics();
        WindowManager wmgr=(WindowManager)getSystemService(WINDOW_SERVICE);

        wmgr.getDefaultDisplay().getMetrics(metrics);

        Context ctxt=Context
          .newBuilder(metrics)
          .resolver(FieldValueResolver.INSTANCE)
          .build();

        response.send(t.apply(ctxt));
        ctxt.destroy();
      }
      catch (IOException e) {
        Log.e(getClass().getSimpleName(),
          "Exception serving Web page", e);
      }
    }
  }

  private static class AssetTemplateLoader
    extends AbstractTemplateLoader {
    private final AssetManager mgr;

    AssetTemplateLoader(AssetManager mgr) {
      this.mgr=mgr;
    }

    @Override
    public TemplateSource sourceAt(String s) throws IOException {
      return(new StringTemplateSource(s, slurp(mgr.open(s))));
    }
  }

  // inspired by http://stackoverflow.com/a/309718/115145

  public static String slurp(final InputStream is) throws IOException {
    final char[] buffer=new char[1024];
    final StringBuilder out=new StringBuilder();
    final InputStreamReader in=new InputStreamReader(is, "UTF-8");

    while (true) {
      int rsz=in.read(buffer, 0, buffer.length);
      if (rsz < 0)
        break;
      out.append(buffer, 0, rsz);
    }

    return(out.toString());
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
