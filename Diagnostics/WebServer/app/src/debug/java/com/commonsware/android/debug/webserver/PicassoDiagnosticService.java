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
 http://commonsware.com/Android
 */

package com.commonsware.android.debug.webserver;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.util.Log;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.StatsSnapshot;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PicassoDiagnosticService extends Service {
  private AsyncHttpServer server;
  private Handlebars handlebars;
  private Template t;

  @Override
  public void onCreate() {
    super.onCreate();

    handlebars=new Handlebars(new AssetTemplateLoader(getAssets()));
    server=new AsyncHttpServer();

    try {
      t=handlebars.compile("picasso.hbs");
      server.get("/", new MainRequestCallback());
      server.get("/stop", new StopRequestCallback());
      server.listen(4999);
    }
    catch (IOException e) {
      Log.e(getClass().getSimpleName(),
          "Exception starting Web server", e);
    }
  }

  @Override
  public void onDestroy() {
    server.stop();
    AsyncServer.getDefault().stop(); // no, really, I mean stop

    super.onDestroy();
  }

  @Override
  public IBinder onBind(Intent intent) {
    return(null);
  }

  private class MainRequestCallback implements HttpServerRequestCallback {
    @Override
    public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
      try {
        StatsSnapshot ss=Picasso
            .with(PicassoDiagnosticService.this)
            .getSnapshot();
        String formattedTime=DateUtils.formatDateTime(PicassoDiagnosticService.this,
            ss.timeStamp,
            DateUtils.FORMAT_SHOW_TIME);

        Context ctxt=Context
            .newBuilder(ss)
            .combine("formattedTime", formattedTime)
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

  private class StopRequestCallback implements HttpServerRequestCallback {
    @Override
    public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
      response.send("Goodbye, cruel world!");
      stopSelf();
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
}
