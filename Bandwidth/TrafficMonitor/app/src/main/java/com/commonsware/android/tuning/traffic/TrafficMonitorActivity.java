/***
  Copyright (c) 2008-2011 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _Tuning Android Applications_
    https://commonsware.com/AndTuning
*/

package com.commonsware.android.tuning.traffic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class TrafficMonitorActivity extends Activity {
  TextView latest_rx=null;
  TextView latest_tx=null;
  TextView previous_rx=null;
  TextView previous_tx=null;
  TextView delta_rx=null;
  TextView delta_tx=null;
  TrafficSnapshot latest=null;
  TrafficSnapshot previous=null;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    
    latest_rx=(TextView)findViewById(R.id.latest_rx);
    latest_tx=(TextView)findViewById(R.id.latest_tx);
    previous_rx=(TextView)findViewById(R.id.previous_rx);
    previous_tx=(TextView)findViewById(R.id.previous_tx);
    delta_rx=(TextView)findViewById(R.id.delta_rx);
    delta_tx=(TextView)findViewById(R.id.delta_tx);
    
    takeSnapshot(null);
  }
  
  public void takeSnapshot(View v) {
    previous=latest;
    latest=new TrafficSnapshot(this);
    
    latest_rx.setText(String.valueOf(latest.device.rx));
    latest_tx.setText(String.valueOf(latest.device.tx));
    
    if (previous!=null) {
      previous_rx.setText(String.valueOf(previous.device.rx));
      previous_tx.setText(String.valueOf(previous.device.tx));
      
      delta_rx.setText(String.valueOf(latest.device.rx-previous.device.rx));
      delta_tx.setText(String.valueOf(latest.device.tx-previous.device.tx));
    }
    
    ArrayList<String> log=new ArrayList<String>();
    HashSet<Integer> intersection=new HashSet<Integer>(latest.apps.keySet());
    
    if (previous!=null) {
      intersection.retainAll(previous.apps.keySet());
    }
    
    for (Integer uid : intersection) {
      TrafficRecord latest_rec=latest.apps.get(uid);
      TrafficRecord previous_rec=
            (previous==null ? null : previous.apps.get(uid));
      
      emitLog(latest_rec.tag, latest_rec, previous_rec, log);
    }
    
    Collections.sort(log);
    
    for (String row : log) {
      Log.d("TrafficMonitor", row);
    }
  }
  
  private void emitLog(CharSequence name, TrafficRecord latest_rec,
                        TrafficRecord previous_rec,
                        ArrayList<String> rows) {
    if (latest_rec.rx>-1 || latest_rec.tx>-1) {
      StringBuilder buf=new StringBuilder(name);
      
      buf.append("=");
      buf.append(String.valueOf(latest_rec.rx));
      buf.append(" received");
      
      if (previous_rec!=null) {
        buf.append(" (delta=");
        buf.append(String.valueOf(latest_rec.rx-previous_rec.rx));
        buf.append(")");
      }
      
      buf.append(", ");
      buf.append(String.valueOf(latest_rec.tx));
      buf.append(" sent");
      
      if (previous_rec!=null) {
        buf.append(" (delta=");
        buf.append(String.valueOf(latest_rec.tx-previous_rec.tx));
        buf.append(")");
      }
      
      rows.add(buf.toString());
    }
  }
}