package com.commonsware.android.gcm.client;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity {
  private static final String SENDER_ID="21221004115";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  public void onClick(View v) {
    GCMRegistrar.checkDevice(this);
    GCMRegistrar.checkManifest(this);

    final String regId=GCMRegistrar.getRegistrationId(this);

    if (regId.length() == 0) {
      GCMRegistrar.register(this, SENDER_ID);
    }
    else {
      Log.d(getClass().getSimpleName(), "Existing registration: "
          + regId);
      Toast.makeText(this, regId, Toast.LENGTH_LONG).show();
    }
  }
}
