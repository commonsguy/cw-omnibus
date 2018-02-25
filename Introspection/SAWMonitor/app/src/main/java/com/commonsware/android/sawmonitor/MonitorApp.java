/***
 Copyright (c) 2018 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.sawmonitor;

import android.app.Application;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;

public class MonitorApp extends Application
  implements SharedPreferences.OnSharedPreferenceChangeListener {
  static final String PREF_ENABLED="enabled";
  private SharedPreferences prefs;
  private PackageManager pm;

  @Override
  public void onCreate() {
    super.onCreate();

    pm=getPackageManager();
    prefs=PreferenceManager.getDefaultSharedPreferences(this);
    prefs.registerOnSharedPreferenceChangeListener(this);
    updateState();
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                        String key) {
    if (PREF_ENABLED.equals(key)) {
      updateState();
    }
  }

  private void updateState() {
    boolean state=prefs.getBoolean(PREF_ENABLED, false);

    updateReceiver(state);

    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
      updateJobs(state);
    }
  }

  private void updateReceiver(boolean state) {
    ComponentName cn=
      new ComponentName(this, PackageReceiver.class);
    int componentState=state ?
      PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
      PackageManager.COMPONENT_ENABLED_STATE_DISABLED;

    pm.setComponentEnabledSetting(cn, componentState,
      PackageManager.DONT_KILL_APP);
  }

  private void updateJobs(boolean state) {
    if (state) {
      SAWDetector.scheduleJobs(this);
    }
    else {
      SAWDetector.cancelJobs(this);
    }
  }
}
