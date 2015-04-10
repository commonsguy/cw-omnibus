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

package com.commonsware.android.envdump;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.pm.FeatureInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

public class MainActivity extends Activity {
  private static final String TAG="EnvDump";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    logBuildValues();
    logSystemFeatures();
    logActivityManagerStuff();
    logDisplayMetrics();
    logConfiguration();
  }

  private void logBuildValues() {
    Log.d(TAG, "Build.VERSION.SDK_INT="+Build.VERSION.SDK_INT);

    Log.d(TAG, "Build.BRAND="+Build.BRAND);
    Log.d(TAG, "Build.DEVICE="+Build.DEVICE);
    Log.d(TAG, "Build.DISPLAY="+Build.DISPLAY);
    Log.d(TAG, "Build.HARDWARE="+Build.HARDWARE);
    Log.d(TAG, "Build.ID="+Build.ID);
    Log.d(TAG, "Build.MANUFACTURER="+Build.MANUFACTURER);
    Log.d(TAG, "Build.MODEL="+Build.MODEL);
    Log.d(TAG, "Build.PRODUCT="+Build.PRODUCT);
    Log.d(TAG, "Build.PRODUCT="+Build.PRODUCT);

    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
      StringBuilder buf=new StringBuilder();

      for (String abi : Build.SUPPORTED_ABIS) {
        if (buf.length() > 0) {
          buf.append(',');
        }

        buf.append(abi);
      }

      Log.d(TAG, "Build.SUPPORTED_APIS=" + buf);
    }
    else {
      Log.d(TAG, "Build.CPU_API="+Build.CPU_ABI);
      Log.d(TAG, "Build.CPU_API2="+Build.CPU_ABI2);
    }
  }

  private void logSystemFeatures() {
    for (FeatureInfo feature :
        getPackageManager().getSystemAvailableFeatures()) {
      Log.d(TAG, "System Feature: "+feature.name);
    }
  }

  private void logActivityManagerStuff() {
    ActivityManager mgr=(ActivityManager)getSystemService(ACTIVITY_SERVICE);

    Log.d(TAG, "heap limit="+mgr.getMemoryClass());
    Log.d(TAG, "large-heap limit="+mgr.getLargeMemoryClass());
  }

  private void logDisplayMetrics() {
    DisplayMetrics dm=new DisplayMetrics();

    getWindowManager().getDefaultDisplay().getMetrics(dm);

    Log.d(TAG, "DisplayMetrics.densityDpi="+dm.densityDpi);
    Log.d(TAG, "DisplayMetrics.xdpi="+dm.xdpi);
    Log.d(TAG, "DisplayMetrics.ydpi="+dm.ydpi);
    Log.d(TAG, "DisplayMetrics.scaledDensity="+dm.scaledDensity);
    Log.d(TAG, "DisplayMetrics.widthPixels="+dm.widthPixels);
    Log.d(TAG, "DisplayMetrics.heightPixels="+dm.heightPixels);
  }

  private void logConfiguration() {
    Configuration cfg=getResources().getConfiguration();

    Log.d(TAG, "Configuration.densityDpi="+cfg.densityDpi);
    Log.d(TAG, "Configuration.fontScale="+cfg.fontScale);
    Log.d(TAG, "Configuration.hardKeyboardHidden="+cfg.hardKeyboardHidden);
    Log.d(TAG, "Configuration.keyboard="+cfg.keyboard);
    Log.d(TAG, "Configuration.keyboardHidden="+cfg.keyboardHidden);
    Log.d(TAG, "Configuration.locale="+cfg.locale);
    Log.d(TAG, "Configuration.mcc="+cfg.mcc);
    Log.d(TAG, "Configuration.mnc="+cfg.mnc);
    Log.d(TAG, "Configuration.navigation="+cfg.navigation);
    Log.d(TAG, "Configuration.navigationHidden="+cfg.navigationHidden);
    Log.d(TAG, "Configuration.orientation="+cfg.orientation);
    Log.d(TAG, "Configuration.screenHeightDp="+cfg.screenHeightDp);
    Log.d(TAG, "Configuration.screenWidthDp="+cfg.screenWidthDp);
    Log.d(TAG, "Configuration.touchscreen="+cfg.touchscreen);
  }
}