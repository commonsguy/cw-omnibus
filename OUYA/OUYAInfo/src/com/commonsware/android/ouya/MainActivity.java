/***
  Copyright (c) 2013 CommonsWare, LLC
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

package com.commonsware.android.ouya;

import android.app.Activity;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import tv.ouya.console.api.OuyaController;
import tv.ouya.console.api.OuyaFacade;

public class MainActivity extends Activity {
  private TextView lastOnKeyDown=null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    OuyaController.init(this);
    OuyaFacade.getInstance()
              .init(getApplicationContext(), "not-a-UUID");

    if (OuyaFacade.getInstance().isRunningOnOUYAHardware()) {
      setContentView(R.layout.activity_main_ouya);
    }
    else {
      setContentView(R.layout.activity_main);
    }

    Configuration cfg=getResources().getConfiguration();
    DisplayMetrics dm=new DisplayMetrics();
    TextView cfgText=(TextView)findViewById(R.id.configuration);

    getWindowManager().getDefaultDisplay().getMetrics(dm);

    String raw=cfg.toString() + "\n" + dm.toString();

    cfgText.setText(raw.replaceAll(", ", "\n").replaceAll(" ", "\n")
                       .replaceAll("\\{", "").replaceAll("\\}", "")
                       .replaceAll("DisplayMetrics", ""));

    ConnectivityManager mgr=
        (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo=mgr.getActiveNetworkInfo();
    TextView networkText=(TextView)findViewById(R.id.connection_type);

    if (networkInfo == null) {
      networkText.setText("(no connection)");
    }
    else {
      networkText.setText(networkInfo.toString().replaceAll(", ", "\n"));
    }

    lastOnKeyDown=(TextView)findViewById(R.id.last_onkeydown);

    LocationManager loc=
        (LocationManager)getSystemService(LOCATION_SERVICE);

    for (String provider : loc.getAllProviders()) {
      Log.d(getClass().getSimpleName(),
            String.format("%s: %b", provider,
                          loc.isProviderEnabled(provider)));
    }
  }

  @Override
  public boolean onKeyDown(final int keyCode, KeyEvent event) {
    int player=
        OuyaController.getPlayerNumByDeviceId(event.getDeviceId());

    lastOnKeyDown.setText(String.format("%d: %s", player,
                                        convertKeyCode(keyCode)));

    return(super.onKeyDown(keyCode, event));
  }

  private String convertKeyCode(final int keyCode) {
    String result=Integer.toString(keyCode);

    switch (keyCode) {
      case OuyaController.BUTTON_O:
      case KeyEvent.KEYCODE_DPAD_CENTER:
        result="BUTTON_O";
        break;

      case OuyaController.BUTTON_A:
      case KeyEvent.KEYCODE_BACK:
        result="BUTTON_A";
        break;

      case OuyaController.BUTTON_Y:
        result="BUTTON_Y";
        break;

      case OuyaController.BUTTON_U:
        result="BUTTON_U";
        break;

      case OuyaController.BUTTON_L1:
        result="BUTTON_L1 (left bumper)";
        break;

      case OuyaController.BUTTON_L2:
        result="BUTTON_L2 (left trigger)";
        break;

      case OuyaController.BUTTON_R1:
        result="BUTTON_R1 (right bumper)";
        break;

      case OuyaController.BUTTON_R2:
        result="BUTTON_R2 (right trigger)";
        break;

      case OuyaController.BUTTON_MENU:
        result="BUTTON_MENU (short press, system button)";
        break;

      case OuyaController.BUTTON_DPAD_UP:
        result="BUTTON_DPAD_UP";
        break;

      case OuyaController.BUTTON_DPAD_RIGHT:
        result="BUTTON_DPAD_RIGHT";
        break;

      case OuyaController.BUTTON_DPAD_DOWN:
        result="BUTTON_DPAD_DOWN";
        break;

      case OuyaController.BUTTON_DPAD_LEFT:
        result="BUTTON_DPAD_LEFT";
        break;

      case OuyaController.BUTTON_R3:
        result="BUTTON_R3 (right joystick button)";
        break;

      case OuyaController.BUTTON_L3:
        result="BUTTON_L3 (left joystick button)";
        break;
    }

    return(result);
  }
}
