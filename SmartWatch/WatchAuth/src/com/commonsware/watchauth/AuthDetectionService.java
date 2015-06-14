/***
  Copyright (c) 2012 CommonsWare, LLC
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

package com.commonsware.watchauth;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class AuthDetectionService extends Service {
  static final String CMD_UNLOCK="com.commonsware.watchauth.CMD_UNLOCK";
  static final String CMD_VALIDATE=
      "com.commonsware.watchauth.CMD_VALIDATE";
  private Timeout timeout=null;
  private int timeoutSeconds=0;

  @Override
  public void onCreate() {
    super.onCreate();

    SharedPreferences prefs=
        PreferenceManager.getDefaultSharedPreferences(this);
    timeoutSeconds=Integer.parseInt(prefs.getString("timeout", "60"));
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (CMD_UNLOCK.equals(intent.getAction())) {
      timeout=new Timeout();
      timeout.start();
    }
    else if (CMD_VALIDATE.equals(intent.getAction())) {
      synchronized(this) {
        if (timeout != null) {
          timeout.interrupt();
        }
      }
    }

    return(START_REDELIVER_INTENT);
  }

  @Override
  public IBinder onBind(Intent arg0) {
    return(null);
  }

  class Timeout extends Thread {
    @Override
    public void run() {
      SystemClock.sleep(timeoutSeconds * 1000);

      synchronized(AuthDetectionService.this) {
        if (!isInterrupted()) {
          DevicePolicyManager mgr=
              (DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);

          mgr.lockNow();
          timeout=null;
          stopSelf();
        }
      }
    }
  }
}
