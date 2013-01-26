package com.commonsware.android.mapsv2.sherlock;

import android.os.Bundle;
import android.util.Log;
import com.actionbarsherlock.app.SherlockMapFragment;

public class MyMapFragment extends SherlockMapFragment {
  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    if (getMap() != null) {
      Log.d(getClass().getSimpleName(), "Map ready for use!");
    }
  }
}
