/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
*/

package com.commonsware.android.sysevents.battery;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class BatteryMonitor extends Activity {
  private ProgressBar bar=null;
  private ImageView status=null;
  private TextView level=null;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    
    bar=(ProgressBar)findViewById(R.id.bar);
    status=(ImageView)findViewById(R.id.status);
    level=(TextView)findViewById(R.id.level);
  }
  
  @Override
  public void onResume() {
    super.onResume();
    
    /**
     * catches the current status....
     	IntentFilter filter = new IntentFilter( Intent.ACTION_BATTERY_CHANGED);
    	readBattery( registerReceiver(null, filter ) );
     */
    
    
    registerReceiver(onBatteryChanged,
                      new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    
    registerReceiver(connectedReceiver, new IntentFilter( Intent.ACTION_POWER_CONNECTED ));
    registerReceiver(disconnectedReceiver, new IntentFilter( Intent.ACTION_POWER_DISCONNECTED ));
  }
  
  @Override
  public void onPause() {
    super.onPause();
    
    unregisterReceiver(onBatteryChanged);
    unregisterReceiver(disconnectedReceiver);
  }
  
  private void readBattery( Intent intent )
  {
	  int pct=100*intent.getIntExtra("level", 1)/intent.getIntExtra("scale", 1);
      
      bar.setProgress(pct);
      level.setText(String.valueOf(pct));
      
      switch(intent.getIntExtra("status", -1)) {
        case BatteryManager.BATTERY_STATUS_CHARGING:
          status.setImageResource(R.drawable.charging);
          break;
        
        case BatteryManager.BATTERY_STATUS_FULL:
          int plugged=intent.getIntExtra("plugged", -1);
          
          if (plugged==BatteryManager.BATTERY_PLUGGED_AC ||
              plugged==BatteryManager.BATTERY_PLUGGED_USB) {
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
  
  BroadcastReceiver onBatteryChanged=new BroadcastReceiver() {
    public void onReceive(Context context, Intent intent) {
    	BatteryMonitor.this.readBattery(intent);
    }
  };
  
  
  BroadcastReceiver connectedReceiver = new BroadcastReceiver()
	{
		
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Toast.makeText(context, "connected", Toast.LENGTH_SHORT).show();
		}
	};
	
	
	BroadcastReceiver disconnectedReceiver = new BroadcastReceiver()
	{
		
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Toast.makeText(context, "disconnected", Toast.LENGTH_SHORT).show();
		}
	};
}