package com.commonsware.empublite;

import info.juanmendez.android.utils.Trace;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class InstallReceiver extends BroadcastReceiver {
  private static final int NOTIFY_ID=1337;

  @Override
  public void onReceive(Context ctxt, Intent i) {
    NotificationCompat.Builder builder=
        new NotificationCompat.Builder(ctxt);
    Intent toLaunch=new Intent(ctxt, EmPubLiteActivity.class);
    PendingIntent pi=PendingIntent.getActivity(ctxt, 0, toLaunch, 0);

    builder.setAutoCancel(true).setContentIntent(pi)
           .setContentTitle(ctxt.getString(R.string.update_complete))
           .setContentText(ctxt.getString(R.string.update_desc))
           .setSmallIcon(android.R.drawable.stat_sys_download_done)
           .setTicker(ctxt.getString(R.string.update_complete))
           .setWhen(System.currentTimeMillis());

    NotificationManager mgr=
        ((NotificationManager)ctxt.getSystemService(Context.NOTIFICATION_SERVICE));

    mgr.notify(NOTIFY_ID, builder.build());
  }
}
