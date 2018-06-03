/***
  Copyright (c) 2013 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  Covered in detail in the book _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.sensor.monitor;

import android.annotation.TargetApi;
import android.support.v4.app.FragmentActivity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends FragmentActivity implements
    SensorsFragment.Contract {
  private SensorManager mgr=null;
  private SensorLogFragment log=null;
  private SlidingPaneLayout panes=null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mgr=(SensorManager)getSystemService(Context.SENSOR_SERVICE);

    setContentView(R.layout.activity_main);

    log=
        (SensorLogFragment)getSupportFragmentManager().findFragmentById(R.id.log);

    panes=findViewById(R.id.panes);
    panes.openPane();
  }

  @Override
  public void onStop() {
    mgr.unregisterListener(log);
    super.onStop();
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
    List<Sensor> unfiltered=
      new ArrayList<>(mgr.getSensorList(Sensor.TYPE_ALL));
    List<Sensor> result=new ArrayList<>();

    for (Sensor s : unfiltered) {
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT
          || !isTriggerSensor(s)) {
        result.add(s);
      }
    }

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
    mgr.unregisterListener(log);
    mgr.registerListener(log, s, SensorManager.SENSOR_DELAY_NORMAL);
    log.init(isXYZ(s));
    panes.closePane();
  }

  @TargetApi(Build.VERSION_CODES.KITKAT)
  private boolean isXYZ(Sensor s) {
    switch (s.getType()) {
      case Sensor.TYPE_ACCELEROMETER:
      case Sensor.TYPE_GRAVITY:
      case Sensor.TYPE_GYROSCOPE:
      case Sensor.TYPE_LINEAR_ACCELERATION:
      case Sensor.TYPE_MAGNETIC_FIELD:
      case Sensor.TYPE_ROTATION_VECTOR:
        return(true);
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
      if (s.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR
          || s.getType() == Sensor.TYPE_GYROSCOPE_UNCALIBRATED
          || s.getType() == Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED) {
        return(true);
      }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      if (s.getType() == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) {
        return(true);
      }
    }

    return(false);
  }

  @TargetApi(Build.VERSION_CODES.KITKAT)
  private boolean isTriggerSensor(Sensor s) {
    int[] triggers=
        { Sensor.TYPE_SIGNIFICANT_MOTION, Sensor.TYPE_STEP_DETECTOR,
            Sensor.TYPE_STEP_COUNTER };

    return(Arrays.binarySearch(triggers, s.getType()) >= 0);
  }
}
