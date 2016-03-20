/***
 Copyright (c) 2014-2016 CommonsWare, LLC
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

package com.commonsware.android.remoteinput;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;

public class RemoteInputReceiver extends BroadcastReceiver {
  static final int NOTIFY_ID=1337;
  static final String EXTRA_INPUT="input";

  static NotificationCompat.Builder buildNotificationBase(Context ctxt) {
    NotificationCompat.Builder builder=
      new NotificationCompat.Builder(ctxt)
        .setSmallIcon(
          android.R.drawable.stat_sys_download_done)
        .setContentTitle(ctxt.getString(R.string.title));

    return(builder);
  }

  @Override
  public void onReceive(Context ctxt, Intent i) {
    Bundle input=RemoteInput.getResultsFromIntent(i);

    if (input!=null) {
      CharSequence speech=input.getCharSequence(EXTRA_INPUT);

      if (speech!=null) {
        Log.d(getClass().getSimpleName(), speech.toString());
      }
      else {
        Log.e(getClass().getSimpleName(), "No voice response speech");
      }
    }
    else {
      Log.e(getClass().getSimpleName(), "No voice response Bundle");
    }

    NotificationCompat.Builder builder=
      buildNotificationBase(ctxt);

    NotificationManagerCompat
      .from(ctxt)
      .notify(RemoteInputReceiver.NOTIFY_ID, builder.build());
  }
}
