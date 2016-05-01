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

package com.commonsware.android.envdump;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.pm.FeatureInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {
  private static final String TAG="EnvDump";
  private final StringBuilder buf=new StringBuilder();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    logBuildValues();
    logSystemFeatures();
    logActivityManagerStuff();
    logDisplayMetrics();
    logConfiguration();

    TextView tv=(TextView)findViewById(R.id.text);

    tv.setText(buf.toString());
  }

  private void logBuildValues() {
    log("Build.VERSION.SDK_INT="+Build.VERSION.SDK_INT);

    log("Build.BRAND="+Build.BRAND);
    log("Build.DEVICE="+Build.DEVICE);
    log("Build.DISPLAY="+Build.DISPLAY);
    log("Build.HARDWARE="+Build.HARDWARE);
    log("Build.ID="+Build.ID);
    log("Build.MANUFACTURER="+Build.MANUFACTURER);
    log("Build.MODEL="+Build.MODEL);
    log("Build.PRODUCT="+Build.PRODUCT);

    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
      StringBuilder buf=new StringBuilder();

      for (String abi : Build.SUPPORTED_ABIS) {
        if (buf.length() > 0) {
          buf.append(',');
        }

        buf.append(abi);
      }

      log("Build.SUPPORTED_APIS=" + buf);
    }
    else {
      log("Build.CPU_API="+Build.CPU_ABI);
      log("Build.CPU_API2="+Build.CPU_ABI2);
    }
  }

  private void logSystemFeatures() {
    for (FeatureInfo feature :
        getPackageManager().getSystemAvailableFeatures()) {
      log("System Feature: "+feature.name);
    }
  }

  private void logActivityManagerStuff() {
    ActivityManager mgr=(ActivityManager)getSystemService(ACTIVITY_SERVICE);

    log("heap limit="+mgr.getMemoryClass());
    log("large-heap limit="+mgr.getLargeMemoryClass());
  }

  private void logDisplayMetrics() {
    DisplayMetrics dm=new DisplayMetrics();

    getWindowManager().getDefaultDisplay().getMetrics(dm);

    log("DisplayMetrics.densityDpi="+dm.densityDpi);
    log("DisplayMetrics.xdpi="+dm.xdpi);
    log("DisplayMetrics.ydpi="+dm.ydpi);
    log("DisplayMetrics.scaledDensity="+dm.scaledDensity);
    log("DisplayMetrics.widthPixels="+dm.widthPixels);
    log("DisplayMetrics.heightPixels="+dm.heightPixels);
  }

  private void logConfiguration() {
    Configuration cfg=getResources().getConfiguration();

    log("Configuration.densityDpi="+cfg.densityDpi);
    log("Configuration.fontScale="+cfg.fontScale);
    log("Configuration.hardKeyboardHidden="+cfg.hardKeyboardHidden);
    log("Configuration.keyboard="+cfg.keyboard);
    log("Configuration.keyboardHidden="+cfg.keyboardHidden);
    log("Configuration.locale="+cfg.locale);
    log("Configuration.mcc="+cfg.mcc);
    log("Configuration.mnc="+cfg.mnc);
    log("Configuration.navigation="+cfg.navigation);
    log("Configuration.navigationHidden="+cfg.navigationHidden);
    log("Configuration.orientation="+cfg.orientation);
    log("Configuration.screenHeightDp="+cfg.screenHeightDp);
    log("Configuration.screenWidthDp="+cfg.screenWidthDp);
    log("Configuration.touchscreen="+cfg.touchscreen);
  }
  
  private void log(String msg) {
    Log.d(TAG, msg);
    buf.append(msg);
    buf.append('\n');
  }
}