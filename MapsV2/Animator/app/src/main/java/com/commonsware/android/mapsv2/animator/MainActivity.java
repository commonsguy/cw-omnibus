/***
  Copyright (c) 2012-13 CommonsWare, LLC
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

package com.commonsware.android.mapsv2.animator;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.os.Bundle;
import android.util.Property;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

public class MainActivity extends AbstractMapActivity implements
    OnMapReadyCallback {
  private static final LatLng PENN_STATION=new LatLng(40.749972,
                                                      -73.992319);
  private static final LatLng LINCOLN_CENTER=
      new LatLng(40.76866299974387, -73.98268461227417);
  private static final LatLngBounds bounds=
      new LatLngBounds.Builder().include(LINCOLN_CENTER)
                                .include(PENN_STATION).build();
  private Marker markerToAnimate=null;
  private LatLng nextAnimationEnd=PENN_STATION;
  private boolean needsInit=false;
  private GoogleMap map=null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (readyToGo()) {
      setContentView(R.layout.activity_main);

      MapFragment mapFrag=
          (MapFragment)getFragmentManager().findFragmentById(R.id.map);

      if (savedInstanceState == null) {
        needsInit=true;
      }

      mapFrag.getMapAsync(this);
    }
  }

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
    markerToAnimate=
        addMarker(map, LINCOLN_CENTER.latitude,
                  LINCOLN_CENTER.longitude, R.string.lincoln_center,
                  R.string.lincoln_center_snippet);
    addMarker(map, 40.765136435316755, -73.97989511489868,
              R.string.carnegie_hall, R.string.practice_x3);
    addMarker(map, 40.70686417491799, -74.01572942733765,
              R.string.downtown_club, R.string.heisman_trophy);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.animate, menu);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.animate) {
      animateMarker();

      return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  private Marker addMarker(GoogleMap map, double lat, double lon,
                           int title, int snippet) {
    return(map.addMarker(new MarkerOptions().position(new LatLng(lat,
                                                                 lon))
                                            .title(getString(title))
                                            .snippet(getString(snippet))));
  }

  private void animateMarker() {
    map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 48));

    Property<Marker, LatLng> property=
        Property.of(Marker.class, LatLng.class, "position");
    ObjectAnimator animator=
        ObjectAnimator.ofObject(markerToAnimate, property,
                                new LatLngEvaluator(), nextAnimationEnd);
    animator.setDuration(2000);
    animator.start();

    if (nextAnimationEnd == LINCOLN_CENTER) {
      nextAnimationEnd=PENN_STATION;
    }
    else {
      nextAnimationEnd=LINCOLN_CENTER;
    }
  }

  private static class LatLngEvaluator implements TypeEvaluator<LatLng> {
    @Override
    public LatLng evaluate(float fraction, LatLng startValue,
                           LatLng endValue) {
      return(SphericalUtil.interpolate(startValue, endValue, fraction));
    }
  }
}
