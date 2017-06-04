package com.commonsware.android.service.ouroboros;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class HackReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    Log.e(getClass().getSimpleName(), "HackReceiver onReceive()");

    Intent wrapped=intent.getParcelableExtra(Intent.EXTRA_INTENT);

    context.startService(wrapped);
  }
}
