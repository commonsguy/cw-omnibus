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

package com.commonsware.android.mapsv2.taps;

import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AbstractMapActivity implements
    OnMapReadyCallback, OnInfoWindowClickListener,
    OnMarkerClickListener {
  private boolean needsInit=false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (readyToGo()) {
      setContentView(R.layout.activity_main);

      SupportMapFragment mapFrag=
          (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

      if (savedInstanceState == null) {
        needsInit=true;
      }

      mapFrag.getMapAsync(this);
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
        R.string.un, R.string.united_nations, 45);
    addMarker(map, 40.76866299974387, -73.98268461227417,
        R.string.lincoln_center,
        R.string.lincoln_center_snippet, 135);
    addMarker(map, 40.765136435316755, -73.97989511489868,
        R.string.carnegie_hall, R.string.practice_x3, 225);
    addMarker(map, 40.70686417491799, -74.01572942733765,
        R.string.downtown_club, R.string.heisman_trophy, 315);

    map.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
    map.setOnInfoWindowClickListener(this);
    map.setOnMarkerClickListener(this);
  }

  @Override
  public void onInfoWindowClick(Marker marker) {
    Toast.makeText(this, marker.getTitle(), Toast.LENGTH_LONG).show();
  }

  @Override
  public boolean onMarkerClick(Marker marker) {
    Toast.makeText(this, marker.getTitle(), Toast.LENGTH_LONG).show();

    return(false);
  }

  private void addMarker(GoogleMap map, double lat, double lon,
                         int title, int snippet, int hue) {
    map.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
                                     .title(getString(title))
                                     .snippet(getString(snippet))
                                     .icon(BitmapDescriptorFactory.defaultMarker(hue)));
  }
}
