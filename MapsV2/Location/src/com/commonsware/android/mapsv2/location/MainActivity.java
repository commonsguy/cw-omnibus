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

package com.commonsware.android.mapsv2.location;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import java.util.ArrayList;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AbstractMapActivity implements
    OnNavigationListener, OnInfoWindowClickListener, LocationSource,
    LocationListener {
  private static final String STATE_NAV="nav";
  private static final int[] MAP_TYPE_NAMES= { R.string.normal,
      R.string.hybrid, R.string.satellite, R.string.terrain };
  private static final int[] MAP_TYPES= { GoogleMap.MAP_TYPE_NORMAL,
      GoogleMap.MAP_TYPE_HYBRID, GoogleMap.MAP_TYPE_SATELLITE,
      GoogleMap.MAP_TYPE_TERRAIN };
  private GoogleMap map=null;
  private OnLocationChangedListener mapLocationListener=null;
  private LocationManager locMgr=null;
  private Criteria crit=new Criteria();

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

      map.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
      map.setOnInfoWindowClickListener(this);

      locMgr=(LocationManager)getSystemService(LOCATION_SERVICE);
      crit.setAccuracy(Criteria.ACCURACY_FINE);

      map.setMyLocationEnabled(true);
      map.getUiSettings().setMyLocationButtonEnabled(false);
    }
  }

  @Override
  public void onResume() {
    super.onResume();

    locMgr.requestLocationUpdates(0L, 0.0f, crit, this, null);
    map.setLocationSource(this);
  }

  @Override
  public void onPause() {
    map.setLocationSource(null);
    locMgr.removeUpdates(this);

    super.onPause();
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

  @Override
  public void activate(OnLocationChangedListener listener) {
    this.mapLocationListener=listener;
  }

  @Override
  public void deactivate() {
    this.mapLocationListener=null;
  }

  @Override
  public void onLocationChanged(Location location) {
    if (mapLocationListener != null) {
      mapLocationListener.onLocationChanged(location);

      LatLng latlng=
          new LatLng(location.getLatitude(), location.getLongitude());
      CameraUpdate cu=CameraUpdateFactory.newLatLng(latlng);

      map.animateCamera(cu);
    }
  }

  @Override
  public void onProviderDisabled(String provider) {
    // unused
  }

  @Override
  public void onProviderEnabled(String provider) {
    // unused
  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {
    // unused
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
