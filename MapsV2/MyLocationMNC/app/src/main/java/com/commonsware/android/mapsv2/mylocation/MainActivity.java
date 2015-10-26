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
    https://commonsware.com/Android
 */

package com.commonsware.android.mapsv2.mylocation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AbstractMapActivity implements
    OnMapReadyCallback, OnInfoWindowClickListener,
    OnMyLocationChangeListener {
  private static final String STATE_IN_PERMISSION="inPermission";
  private static final int REQUEST_PERMS=1337;
  private boolean needsInit=false;
  private boolean isInPermission=false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (savedInstanceState==null) {
      needsInit=true;
    }
    else {
      isInPermission=
        savedInstanceState.getBoolean(STATE_IN_PERMISSION, false);
    }

    onCreateForRealz(canGetLocation());
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putBoolean(STATE_IN_PERMISSION, isInPermission);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String[] permissions,
                                         int[] grantResults) {
    isInPermission=false;

    if (requestCode==REQUEST_PERMS) {
      if (canGetLocation()) {
        onCreateForRealz(true);
      }
      else {
        finish(); // denied permission, so we're done
      }
    }
  }

  @Override
  public void onMapReady(final GoogleMap map) {
    if (needsInit) {
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

    map.setMyLocationEnabled(true);
    map.setOnMyLocationChangeListener(this);
  }

  @Override
  public void onInfoWindowClick(Marker marker) {
    Toast.makeText(this, marker.getTitle(), Toast.LENGTH_LONG).show();
  }

  @Override
  public void onMyLocationChange(Location lastKnownLocation) {
    Log.d(getClass().getSimpleName(),
      String.format("%f:%f", lastKnownLocation.getLatitude(),
        lastKnownLocation.getLongitude()));
  }

  private void onCreateForRealz(boolean canGetLocation) {
    if (canGetLocation) {
      if (readyToGo()) {
        setContentView(R.layout.activity_main);

        MapFragment mapFrag=
          (MapFragment)getFragmentManager().findFragmentById(
            R.id.map);

        mapFrag.getMapAsync(this);
      }
    }
    else if (!isInPermission) {
      isInPermission=true;

      ActivityCompat.requestPermissions(this,
        new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
        REQUEST_PERMS);
    }
  }

  private void addMarker(GoogleMap map, double lat, double lon,
                         int title, int snippet) {
    map.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
                                     .title(getString(title))
                                     .snippet(getString(snippet)));
  }

  private boolean canGetLocation() {
    return(ContextCompat.checkSelfPermission(this,
      Manifest.permission.ACCESS_FINE_LOCATION)==
      PackageManager.PERMISSION_GRANTED);
  }
}
