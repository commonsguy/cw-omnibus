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

package com.commonsware.android.mapsv2.poly;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import java.util.ArrayList;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends AbstractMapActivity implements
    OnNavigationListener, OnInfoWindowClickListener {
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

      addMarker(map, 40.748963847316034, -73.96807193756104,
                R.string.un, R.string.united_nations);
      addMarker(map, 40.76866299974387, -73.98268461227417,
                R.string.lincoln_center,
                R.string.lincoln_center_snippet);
      addMarker(map, 40.765136435316755, -73.97989511489868,
                R.string.carnegie_hall, R.string.practice_x3);
      addMarker(map, 40.70686417491799, -74.01572942733765,
                R.string.downtown_club, R.string.heisman_trophy);

      PolylineOptions line=
          new PolylineOptions().add(new LatLng(40.70686417491799,
                                               -74.01572942733765),
                                    new LatLng(40.76866299974387,
                                               -73.98268461227417),
                                    new LatLng(40.765136435316755,
                                               -73.97989511489868),
                                    new LatLng(40.748963847316034,
                                               -73.96807193756104))
                               .width(5).color(Color.RED);

      map.addPolyline(line);

      PolygonOptions area=
          new PolygonOptions().add(new LatLng(40.748429, -73.984573),
                                   new LatLng(40.753393, -73.996311),
                                   new LatLng(40.758393, -73.992705),
                                   new LatLng(40.753484, -73.980882))
                              .strokeColor(Color.BLUE);

      map.addPolygon(area);

      map.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
      map.setOnInfoWindowClickListener(this);
    }
  }

  @Override
  public boolean onNavigationItemSelected(int itemPosition, long itemId) {
    map.setMapType(MAP_TYPES[itemPosition]);

    return(true);
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    super.onSaveInstanceState(savedInstanceState);

    savedInstanceState.putInt(STATE_NAV,
                              getActionBar().getSelectedNavigationIndex());
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);

    getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_NAV));
  }

  @Override
  public void onInfoWindowClick(Marker marker) {
    Toast.makeText(this, marker.getTitle(), Toast.LENGTH_LONG).show();
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

  private void addMarker(GoogleMap map, double lat, double lon,
                         int title, int snippet) {
    map.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
                                     .title(getString(title))
                                     .snippet(getString(snippet)));
  }
}
