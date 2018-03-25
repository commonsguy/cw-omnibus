/***
  Copyright (c) 2012 CommonsWare, LLC
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

package com.commonsware.android.mapsv2.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AbstractMapActivity implements
    OnMapReadyCallback, OnInfoWindowClickListener, LocationSource,
    LocationListener {
  private static final String STATE_IN_PERMISSION="inPermission";
  private static final String STATE_AUTO_FOLLOW="autoFollow";
  private static final int REQUEST_PERMS=1337;
  private boolean isInPermission=false;
  private OnLocationChangedListener mapLocationListener=null;
  private LocationManager locMgr=null;
  private Criteria crit=new Criteria();
  private boolean needsInit=false;
  private GoogleMap map=null;
  private boolean autoFollow=true;

  @Override
  protected void onCreate(Bundle state) {
    super.onCreate(state);

    if (state==null) {
      needsInit=true;
    }
    else {
      isInPermission=state.getBoolean(STATE_IN_PERMISSION, false);
      autoFollow=state.getBoolean(STATE_AUTO_FOLLOW, true);
    }

    onCreateForRealz(canGetLocation());
  }

  @SuppressLint("MissingPermission")
  @Override
  public void onMapReady(final GoogleMap map) {
    this.map=map;

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
    locMgr=(LocationManager)getSystemService(LOCATION_SERVICE);
    crit.setAccuracy(Criteria.ACCURACY_FINE);
    follow();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putBoolean(STATE_IN_PERMISSION, isInPermission);
    outState.putBoolean(STATE_AUTO_FOLLOW, autoFollow  );
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
  public void onStart() {
    super.onStart();

    follow();
  }

  @Override
  public void onStop() {
    map.setLocationSource(null);
    locMgr.removeUpdates(this);

    super.onStop();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.actions, menu);
    menu.findItem(R.id.follow).setChecked(autoFollow);

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId()==R.id.follow) {
      item.setChecked(!item.isChecked());
      autoFollow=item.isChecked();
      follow();

      return true;
    }

    return super.onOptionsItemSelected(item);
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

  private void addMarker(GoogleMap map, double lat, double lon,
                         int title, int snippet) {
    map.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
                                     .title(getString(title))
                                     .snippet(getString(snippet)));
  }

  private void onCreateForRealz(boolean canGetLocation) {
    if (canGetLocation) {
      if (readyToGo()) {
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFrag=
          (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

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

  private boolean canGetLocation() {
    return(ContextCompat.checkSelfPermission(this,
      Manifest.permission.ACCESS_FINE_LOCATION)==
      PackageManager.PERMISSION_GRANTED);
  }

  @SuppressLint("MissingPermission")
  private void follow() {
    if (map!=null && locMgr!=null) {
      if (autoFollow) {
        locMgr.requestLocationUpdates(0L, 0.0f, crit, this, null);
        map.setLocationSource(this);
        map.getUiSettings().setMyLocationButtonEnabled(false);
      }
      else {
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setLocationSource(null);
        locMgr.removeUpdates(this);
      }
    }
  }
}
