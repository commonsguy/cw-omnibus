/***
  Copyright (c) 2012 CommonsWare, LLC
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

package com.commonsware.android.mapsv2.nooyawk;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AbstractMapActivity implements
    OnNavigationListener {
  private static final String STATE_NAV="nav";
  private static final int[] MAP_TYPE_NAMES= { R.string.normal,
      R.string.hybrid, R.string.satellite, R.string.terrain };
  private static final int[] MAP_TYPES= { GoogleMap.MAP_TYPE_NORMAL,
      GoogleMap.MAP_TYPE_HYBRID, GoogleMap.MAP_TYPE_SATELLITE,
      GoogleMap.MAP_TYPE_TERRAIN };
  private GoogleMap map=null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (readyToGo()) {
      setContentView(R.layout.activity_main);

      MapFragment mapFrag=
          (MapFragment)getFragmentManager().findFragmentById(R.id.map);

      initListNav();

      map=mapFrag.getMap();

      if (savedInstanceState == null) {
        CameraUpdate center=
            CameraUpdateFactory.newLatLng(new LatLng(40.76793169992044,
                                                     -73.98180484771729));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

        map.moveCamera(center);
        map.animateCamera(zoom);
      }
    }
  }

  @Override
  public boolean onNavigationItemSelected(int itemPosition, long itemId) {
    map.setMapType(MAP_TYPES[itemPosition]);

    return(true);
  }

  @Override
  public void onSaveInstanceState(Bundle state) {
    super.onSaveInstanceState(state);

    state.putInt(STATE_NAV, getActionBar().getSelectedNavigationIndex());
  }

  @Override
  public void onRestoreInstanceState(Bundle state) {
    super.onRestoreInstanceState(state);

    getActionBar().setSelectedNavigationItem(state.getInt(STATE_NAV));
  }

  private void initListNav() {
    ArrayList<String> items=new ArrayList<String>();
    ArrayAdapter<String> nav=null;
    ActionBar bar=getActionBar();

    for (int type : MAP_TYPE_NAMES) {
      items.add(getString(type));
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      nav=
          new ArrayAdapter<String>(
                                   bar.getThemedContext(),
                                   android.R.layout.simple_spinner_item,
                                   items);
    }
    else {
      nav=
          new ArrayAdapter<String>(
                                   this,
                                   android.R.layout.simple_spinner_item,
                                   items);
    }

    nav.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
    bar.setListNavigationCallbacks(nav, this);
  }
}
