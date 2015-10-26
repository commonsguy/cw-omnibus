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

package com.commonsware.andprojector;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.view.WindowManager;
import com.commonsware.android.webserver.WebServerService;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import java.io.ByteArrayInputStream;
import java.util.concurrent.atomic.AtomicReference;

public class ProjectorService extends WebServerService {
  static final String EXTRA_RESULT_CODE="resultCode";
  static final String EXTRA_RESULT_INTENT="resultIntent";
  static final int VIRT_DISPLAY_FLAGS=
      DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY |
      DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
  private MediaProjection projection;
  private VirtualDisplay vdisplay;
  final private HandlerThread handlerThread=new HandlerThread(getClass().getSimpleName(),
      android.os.Process.THREAD_PRIORITY_BACKGROUND);
  private Handler handler;
  private AtomicReference<byte[]> latestPng=new AtomicReference<byte[]>();
  private MediaProjectionManager mgr;
  private WindowManager wmgr;
  private ImageTransmogrifier it;

  @Override
  public void onCreate() {
    super.onCreate();

    mgr=(MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);
    wmgr=(WindowManager)getSystemService(WINDOW_SERVICE);

    handlerThread.start();
    handler=new Handler(handlerThread.getLooper());
  }

  @Override
  public int onStartCommand(Intent i, int flags, int startId) {
    projection=
        mgr.getMediaProjection(i.getIntExtra(EXTRA_RESULT_CODE, -1),
            (Intent)i.getParcelableExtra(EXTRA_RESULT_INTENT));

    it=new ImageTransmogrifier(this);

    MediaProjection.Callback cb=new MediaProjection.Callback() {
      @Override
      public void onStop() {
        vdisplay.release();
      }
    };

    vdisplay=projection.createVirtualDisplay("andprojector",
        it.getWidth(), it.getHeight(),
        getResources().getDisplayMetrics().densityDpi,
        VIRT_DISPLAY_FLAGS, it.getSurface(), null, handler);
    projection.registerCallback(cb, handler);

    return(START_NOT_STICKY);
  }

  @Override
  public void onDestroy() {
    projection.stop();

    super.onDestroy();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);

    ImageTransmogrifier newIt=new ImageTransmogrifier(this);

    if (newIt.getWidth()!=it.getWidth() ||
      newIt.getHeight()!=it.getHeight()) {
      ImageTransmogrifier oldIt=it;

      it=newIt;
      vdisplay.resize(it.getWidth(), it.getHeight(),
        getResources().getDisplayMetrics().densityDpi);
      vdisplay.setSurface(it.getSurface());

      oldIt.close();
    }
  }

  @Override
  protected boolean configureRoutes(AsyncHttpServer server) {
    serveWebSockets("/ss", null);

    server.get(getRootPath()+"/screen/.*",
      new ScreenshotRequestCallback());

    return(true);
  }

  @Override
  protected int getPort() {
    return(4999);
  }

  @Override
  protected int getMaxIdleTimeSeconds() {
    return(120);
  }

  @Override
  protected int getMaxSequentialInvalidRequests() {
    return(10);
  }

  WindowManager getWindowManager() {
    return(wmgr);
  }

  Handler getHandler() {
    return(handler);
  }

  void updateImage(byte[] newPng) {
    latestPng.set(newPng);

    for (WebSocket socket : getWebSockets()) {
      socket.send("screen/"+Long.toString(SystemClock.uptimeMillis()));
    }
  }

  @Override
  protected void buildForegroundNotification(NotificationCompat.Builder b) {
    Intent iActivity=new Intent(this, MainActivity.class);
    PendingIntent piActivity=PendingIntent.getActivity(this, 0,
      iActivity, 0);

    b.setContentTitle(getString(R.string.app_name))
        .setContentIntent(piActivity)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setTicker(getString(R.string.app_name));
  }

  private class ScreenshotRequestCallback
      implements HttpServerRequestCallback {
    @Override
    public void onRequest(AsyncHttpServerRequest request,
                          AsyncHttpServerResponse response) {
      response.setContentType("image/png");

      byte[] png=latestPng.get();
      ByteArrayInputStream bais=new ByteArrayInputStream(png);

      response.sendStream(bais, png.length);
    }
  }
}
