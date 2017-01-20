/***
  Copyright (c) 2013-2017 CommonsWare, LLC
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

package com.commonsware.android.webkit.bridge;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebMessage;
import android.webkit.WebMessagePort;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;

public class MainActivity extends Activity implements SensorEventListener {
  private static final String URL="file:///android_asset/index.html";
  private static final String THIS_IS_STUPID="https://commonsware.com";
  private SensorManager mgr;
  private Sensor light;
  private WebView wv;
  private final JSInterface jsInterface=new JSInterface();
  private WebMessagePort port;

  @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    mgr=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
    light=mgr.getDefaultSensor(Sensor.TYPE_LIGHT);

    wv=(WebView)findViewById(R.id.webkit);
    wv.getSettings().setJavaScriptEnabled(true);
    wv.addJavascriptInterface(jsInterface, "LIGHT_SENSOR");

    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
      try {
        String html=slurp(getAssets().open("index.html"));

        wv.loadDataWithBaseURL(THIS_IS_STUPID, html, "text/html", "UTF-8",
          null);
        wv.setWebViewClient(new WebViewClient() {
          @Override
          public void onPageFinished(WebView view, String url) {
            initPort();
          }
        });
      }
      catch (IOException e) {
        Log.e(getClass().getSimpleName(), "Could not read asset", e);
      }
    }
    else {
      wv.loadUrl(URL);
    }
  }

  @Override
  protected void onStart() {
    super.onStart();

    mgr.registerListener(this, light, SensorManager.SENSOR_DELAY_UI);
  }

  @Override
  protected void onStop() {
    mgr.unregisterListener(this);

    super.onStop();
  }

  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {
    float lux=sensorEvent.values[0];

    jsInterface.updateLux(lux);

    String js=String.format(Locale.US, "update_lux(%f)", lux);

    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
      // postLux();
    }
    else if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT) {
      wv.evaluateJavascript(js, null);
    }
    else {
      wv.loadUrl("javascript:"+js);
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int i) {
    // unused
  }

  @TargetApi(Build.VERSION_CODES.M)
  private void initPort() {
    final WebMessagePort[] channel=wv.createWebMessageChannel();

    port=channel[0];
    port.setWebMessageCallback(new WebMessagePort.WebMessageCallback() {
      @Override
      public void onMessage(WebMessagePort port, WebMessage message) {
        postLux();
      }
    });

    wv.postWebMessage(new WebMessage("", new WebMessagePort[]{channel[1]}),
          Uri.parse(THIS_IS_STUPID));
  }

  @TargetApi(Build.VERSION_CODES.M)
  private void postLux() {
    port.postMessage(new WebMessage(jsInterface.getLux()));
  }

  // based on http://stackoverflow.com/a/309718/115145

  private static String slurp(final InputStream is)
    throws IOException {
    final char[] buffer=new char[8192];
    final StringBuilder out=new StringBuilder();
    final Reader in=new InputStreamReader(is, "UTF-8");
    int rsz=in.read(buffer, 0, buffer.length);

    while (rsz>0) {
      out.append(buffer, 0, rsz);
      rsz=in.read(buffer, 0, buffer.length);
    }

    return(out.toString());
  }

  private static class JSInterface {
    float lux=0.0f;

    private void updateLux(float lux) {
      this.lux=lux;
    }

    @JavascriptInterface
    public String getLux() {
      return(String.format(Locale.US, "{\"lux\": %f}", lux));
    }
  }
}