package com.commonsware.android.sensorlist;

import android.app.Activity;
import android.os.Bundle;

public class SensorListDemoActivity extends Activity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getFragmentManager().findFragmentById(android.R.id.content) == null) {
      getFragmentManager().beginTransaction()
                          .add(android.R.id.content,
                               new SensorListFragment()).commit();
    }
  }
}
