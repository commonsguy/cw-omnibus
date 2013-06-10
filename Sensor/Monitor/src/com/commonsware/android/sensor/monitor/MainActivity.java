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
    http://commonsware.com/Android
 */

package com.commonsware.android.sensor.monitor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class MainActivity extends SherlockFragmentActivity implements
    SensorsFragment.Contract, SensorEventListener {
  private SensorManager mgr=null;
  private SensorLogFragment log=null;
  private SlidingPaneLayout panes=null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mgr=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
    log=
        (SensorLogFragment)getSupportFragmentManager().findFragmentById(R.id.log);

    panes=(SlidingPaneLayout)findViewById(R.id.panes);
    panes.openPane();
  }

  @Override
  public void onPause() {
    mgr.unregisterListener(this);
    super.onPause();
  }

  @Override
  public void onBackPressed() {
    if (panes.isOpen()) {
      super.onBackPressed();
    }
    else {
      panes.openPane();
    }
  }

  @Override
  public List<Sensor> getSensorList() {
    List<Sensor> result=
        new ArrayList<Sensor>(mgr.getSensorList(Sensor.TYPE_ALL));

    Collections.sort(result, new Comparator<Sensor>() {
      @Override
      public int compare(final Sensor a, final Sensor b) {
        return(a.toString().compareTo(b.toString()));
      }
    });

    return(result);
  }

  @Override
  public void onSensorSelected(Sensor s) {
    mgr.unregisterListener(this);
    mgr.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
    log.init(isXYZ(s));
    panes.closePane();
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
    // unused
  }

  @Override
  public void onSensorChanged(SensorEvent e) {
    log.add(e);
  }

  private boolean isXYZ(Sensor s) {
    switch (s.getType()) {
      case Sensor.TYPE_ACCELEROMETER:
      case Sensor.TYPE_GRAVITY:
      case Sensor.TYPE_GYROSCOPE:
      case Sensor.TYPE_LINEAR_ACCELERATION:
      case Sensor.TYPE_MAGNETIC_FIELD:
        return(true);
    }

    return(false);
  }
}
