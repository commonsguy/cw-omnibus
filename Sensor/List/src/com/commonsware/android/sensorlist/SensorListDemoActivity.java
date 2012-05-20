package com.commonsware.android.sensorlist;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class SensorListDemoActivity extends SherlockFragmentActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
                                 .add(android.R.id.content,
                                      new SensorListFragment())
                                 .commit();
    }
  }
}