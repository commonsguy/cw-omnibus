/***
  Copyright (c) 2008-2014 CommonsWare, LLC
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

package com.commonsware.android.battmon;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BatteryFragment extends Fragment {
  private ProgressBar bar=null;
  private ImageView status=null;
  private TextView level=null;
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                           Bundle savedInstanceState) {
    View result=inflater.inflate(R.layout.batt, parent, false);

    bar=(ProgressBar)result.findViewById(R.id.bar);
    status=(ImageView)result.findViewById(R.id.status);
    level=(TextView)result.findViewById(R.id.level);

    return(result);
  }

  @Override
  public void onResume() {
    super.onResume();

    IntentFilter f=new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

    getActivity().registerReceiver(onBattery, f);
  }

  @Override
  public void onPause() {
    getActivity().unregisterReceiver(onBattery);

    super.onPause();
  }

  BroadcastReceiver onBattery=new BroadcastReceiver() {
    public void onReceive(Context context, Intent intent) {
      int pct=
          100 * intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 1)
              / intent.getIntExtra(BatteryManager.EXTRA_SCALE, 1);

      bar.setProgress(pct);
      level.setText(String.valueOf(pct));

      switch (intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)) {
        case BatteryManager.BATTERY_STATUS_CHARGING:
          status.setImageResource(R.drawable.charging);
          break;

        case BatteryManager.BATTERY_STATUS_FULL:
          int plugged=
              intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

          if (plugged == BatteryManager.BATTERY_PLUGGED_AC
              || plugged == BatteryManager.BATTERY_PLUGGED_USB) {
            status.setImageResource(R.drawable.full);
          }
          else {
            status.setImageResource(R.drawable.unplugged);
          }
          break;

        default:
          status.setImageResource(R.drawable.unplugged);
          break;
      }
    }
  };
}