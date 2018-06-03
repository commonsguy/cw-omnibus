/***
  Copyright (c) 2013 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  Covered in detail in the book _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.sensor.monitor;

import android.annotation.SuppressLint;
import android.support.v4.app.ListFragment;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class SensorLogFragment extends ListFragment implements
    SensorEventListener {
  private SensorLogAdapter adapter=null;
  private boolean isXYZ=false;

  @Override
  public void onViewCreated(View view, Bundle state) {
    super.onViewCreated(view, state);

    getListView().setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
    // unused
  }

  @Override
  public void onSensorChanged(SensorEvent e) {
    Float[] values=new Float[3];

    values[0]=e.values[0];
    values[1]=e.values.length>1 ? e.values[1] : 0.0f;
    values[2]=e.values.length>2 ? e.values[2] : 0.0f;

    adapter.add(values);
  }

  void init(boolean isXYZ) {
    this.isXYZ=isXYZ;
    adapter=new SensorLogAdapter(this);
    setListAdapter(adapter);
  }

  private class SensorLogAdapter extends ArrayAdapter<Float[]> {
    SensorLogAdapter(SensorLogFragment sensorLogFragment) {
      super(sensorLogFragment.getActivity(),
            android.R.layout.simple_list_item_1,
            new ArrayList<Float[]>());
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView row=
          (TextView)super.getView(position, convertView, parent);
      String content=null;
      Float[] values=getItem(position);

      if (isXYZ) {
        content=
            String.format("%7.3f / %7.3f / %7.3f / %7.3f",
                          values[0],
                          values[1],
                          values[2],
                          Math.sqrt(values[0] * values[0] + values[1]
                              * values[1] + values[2] * values[2]));
      }
      else {
        content=String.format("%7.3f", values[0]);
      }

      row.setText(content);

      return(row);
    }
  }
}
