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

package com.commonsware.android.gcm.client;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
  static final String SENDER_ID="this is so fake"; // change
                                                   // me!
  private RegisterTask task=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    GCMRegistrarCompat.checkDevice(this);

    if (BuildConfig.DEBUG) {
      GCMRegistrarCompat.checkManifest(this);
    }
  }

  @Override
  public void onDestroy() {
    if (task != null) {
      task.cancel(false);
    }

    super.onDestroy();
  }

  public void onClick(View v) {
    final String regId=GCMRegistrarCompat.getRegistrationId(this);

    if (regId.length() == 0) {
      task=new RegisterTask(this);
      task.execute(SENDER_ID);
    }
    else {
      Log.d(getClass().getSimpleName(), "Existing registration: "
          + regId);
      Toast.makeText(this, regId, Toast.LENGTH_LONG).show();
    }
  }

  private static class RegisterTask extends
      GCMRegistrarCompat.BaseRegisterTask {

    RegisterTask(Context context) {
      super(context);
    }

    @Override
    public void onPostExecute(String regid) {
      Log.d(getClass().getSimpleName(), "registered as: " + regid);
      Toast.makeText(context, regid, Toast.LENGTH_LONG).show();
    }
  }
}
