/***
 Copyright (c) 2014-2016 CommonsWare, LLC
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

package com.commonsware.android.messaging;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;
import java.util.Stack;

public class RemoteInputReceiver extends BroadcastReceiver {
  private static final String CHANNEL_WHATEVER="channel_whatever";
  static final int NOTIFY_ID=1337;
  static final String EXTRA_INPUT="input";
  static final Stack<Message> MESSAGES=new Stack<>();
  static final long INITIAL_TIMESTAMP=System.currentTimeMillis();

  static NotificationCompat.Builder buildNotification(Context ctxt) {
    NotificationManager mgr=
      (NotificationManager)ctxt.getSystemService(Context.NOTIFICATION_SERVICE);

    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O &&
      mgr.getNotificationChannel(CHANNEL_WHATEVER)==null) {
      mgr.createNotificationChannel(new NotificationChannel(CHANNEL_WHATEVER,
        "Whatever", NotificationManager.IMPORTANCE_DEFAULT));
    }

    Intent i=new Intent(ctxt, RemoteInputReceiver.class);
    PendingIntent pi=
      PendingIntent.getBroadcast(ctxt, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

    RemoteInput remoteInput=
      new RemoteInput.Builder(RemoteInputReceiver.EXTRA_INPUT)
        .setLabel(ctxt.getString(R.string.talk))
        .build();

    NotificationCompat.Action remoteAction=
      new NotificationCompat.Action.Builder(
        android.R.drawable.ic_btn_speak_now,
        ctxt.getString(R.string.talk),
        pi).addRemoteInput(remoteInput).build();

    NotificationCompat.MessagingStyle style=
      new NotificationCompat.MessagingStyle("Me")
        .setConversationTitle("A Fake Chat");

    style.addMessage("Want to chat?", INITIAL_TIMESTAMP, "Somebody");

    for (Message msg : MESSAGES) {
      style.addMessage(msg.text, msg.timestamp,
        style.getUserDisplayName());
    }

    NotificationCompat.Builder builder=
      new NotificationCompat.Builder(ctxt, CHANNEL_WHATEVER)
        .setSmallIcon(
          android.R.drawable.stat_sys_download_done)
        .setContentTitle(ctxt.getString(R.string.title))
        .setStyle(style)
        .addAction(remoteAction);

    return(builder);
  }

  @Override
  public void onReceive(Context ctxt, Intent i) {
    Bundle input=RemoteInput.getResultsFromIntent(i);

    if (input!=null) {
      CharSequence text=input.getCharSequence(EXTRA_INPUT);

      if (text!=null) {
        MESSAGES.push(new Message(text));
      }
      else {
        Log.e(getClass().getSimpleName(), "No voice response speech");
      }
    }
    else {
      Log.e(getClass().getSimpleName(), "No voice response Bundle");
    }

    NotificationManagerCompat
      .from(ctxt)
      .notify(RemoteInputReceiver.NOTIFY_ID,
        buildNotification(ctxt).build());
  }

  private static class Message {
    final CharSequence text;
    final long timestamp;

    Message(CharSequence text) {
      this.text=text;
      timestamp=System.currentTimeMillis();
    }
  }
}
