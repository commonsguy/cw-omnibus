package com.commonsware.android.gcm.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {
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
}
