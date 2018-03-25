/***
  Copyright (c) 2008-2015 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  Covered in detail in the book _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.weather2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.tasks.Task;

public class WeatherDemo extends AbstractPermissionActivity {
  private static final int REQUEST_RESOLUTION=61124;
  private static final String STATE_IN_RESOLUTION="inResolution";
  private static final String[] PERMS=
    {Manifest.permission.ACCESS_FINE_LOCATION};
  private WeatherFragment fragment;
  private boolean isInResolution=false;

  @Override
  protected void onReady(Bundle state) {
    if (state!=null) {
      isInResolution=state.getBoolean(STATE_IN_RESOLUTION, false);
    }

    fragment=
      (WeatherFragment)getSupportFragmentManager().findFragmentById(android.R.id.content);

    if (fragment==null) {
      fragment=new WeatherFragment();
      getSupportFragmentManager().beginTransaction()
        .add(android.R.id.content, fragment).commit();
    }

    if (!isInResolution) {
      isInResolution=true;
      LocationSettingsRequest request=new LocationSettingsRequest.Builder()
        .addLocationRequest(LocationRequest.create())
        .build();
      LocationServices.getSettingsClient(this)
        .checkLocationSettings(request)
        .addOnCompleteListener(this::handleSettingsResponse);
    }
  }

  @Override
  protected String[] getDesiredPermissions() {
    return(PERMS);
  }

  @Override
  protected void onPermissionDenied() {
    Toast
      .makeText(this, R.string.msg_no_perm, Toast.LENGTH_LONG)
      .show();
    finish();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putBoolean(STATE_IN_RESOLUTION, isInResolution);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
                                  Intent data) {
    if (requestCode==REQUEST_RESOLUTION) {
      isInResolution=false;

      if (resultCode==RESULT_OK) {
        findLocation();
      }
      else {
        unavailable();
      }
    }
    else {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

  private void handleSettingsResponse(Task<LocationSettingsResponse> task) {
    try {
      LocationSettingsResponse response=task.getResult(ApiException.class);
      LocationSettingsStates states=response.getLocationSettingsStates();

      if (states.isLocationPresent() && states.isLocationUsable()) {
        findLocation();
      }
      else {
        unavailable();
      }
    }
    catch (ApiException e) {
      copeWithFailure(e);
    }
  }

  @SuppressLint("MissingPermission")
  private void findLocation() {
    FusedLocationProviderClient client=
      LocationServices.getFusedLocationProviderClient(this);

    client.getLastLocation()
      .addOnCompleteListener(this, this::useResult)
      .addOnFailureListener(this, this::copeWithFailure);
  }

  private void useResult(Task<Location> task) {
    if (task.getResult()==null) {
      Toast
        .makeText(this, R.string.msg_no_location, Toast.LENGTH_LONG)
        .show();
      finish();
    }
    else {
      fragment.fetchForecast(task.getResult());
    }
  }

  private void copeWithFailure(Exception e) {
    if (e instanceof ResolvableApiException) {
      try {
        ((ResolvableApiException)e).startResolutionForResult(this, REQUEST_RESOLUTION);
        return;
      }
      catch (IntentSender.SendIntentException e1) {
        e=e1;
      }
    }

    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    Log.e(getClass().getSimpleName(), "Exception getting location", e);
    finish();
  }

  private void unavailable() {
    Toast.makeText(this, R.string.msg_not_avail, Toast.LENGTH_LONG)
      .show();
    finish();
  }
}
