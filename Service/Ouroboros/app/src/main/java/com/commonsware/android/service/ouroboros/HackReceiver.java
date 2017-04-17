package com.commonsware.android.service.ouroboros;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class HackReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    Intent wrapped=intent.getParcelableExtra(Intent.EXTRA_INTENT);

    context.startService(wrapped);
  }
}
