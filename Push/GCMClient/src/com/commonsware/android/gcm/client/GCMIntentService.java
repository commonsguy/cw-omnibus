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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {
  public GCMIntentService() {
    super(MainActivity.SENDER_ID);
  }
  
  @Override
  protected void onRegistered(Context ctxt, String regId) {
    Log.d(getClass().getSimpleName(), "onRegistered: " + regId);
    Toast.makeText(this, regId, Toast.LENGTH_LONG).show();
  }

  @Override
  protected void onUnregistered(Context ctxt, String regId) {
    Log.d(getClass().getSimpleName(), "onUnregistered: " + regId);
  }

  @Override
  protected void onMessage(Context ctxt, Intent message) {
    Bundle extras=message.getExtras();

    for (String key : extras.keySet()) {
      Log.d(getClass().getSimpleName(),
            String.format("onMessage: %s=%s", key,
                          extras.getString(key)));
    }
  }

  @Override
  protected void onError(Context ctxt, String errorMsg) {
    Log.d(getClass().getSimpleName(), "onError: " + errorMsg);
  }

  @Override
  protected boolean onRecoverableError(Context ctxt, String errorMsg) {
    Log.d(getClass().getSimpleName(), "onRecoverableError: " + errorMsg);
    
    return(true);
  }
}
