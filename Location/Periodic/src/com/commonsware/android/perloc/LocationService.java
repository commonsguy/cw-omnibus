/***
  Copyright (c) 2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Advanced Android Development_
    http://commonsware.com/AdvAndroid
*/

package com.commonsware.android.perloc;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LocationService extends Service implements
    LocationListener, Runnable {
  private static final int PERIOD_SECONDS=15;
  private LocationManager mgr=null;
  private ScheduledThreadPoolExecutor executor=
      new ScheduledThreadPoolExecutor(1);
  private boolean isScheduled=false;

  @Override
  public void onCreate() {
    super.onCreate();

    executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);

    mgr=(LocationManager)getSystemService(LOCATION_SERVICE);
    mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    mgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                               this);
  }

  @Override
  public void onDestroy() {
    mgr.removeUpdates(this);
    executor.shutdown();

    super.onDestroy();
  }

  @Override
  public IBinder onBind(Intent arg0) {
    return(null);
  }

  @Override
  public void onLocationChanged(Location location) {
    if (!isScheduled) {
      isScheduled=true;
      executor.scheduleAtFixedRate(this, 0, PERIOD_SECONDS,
                                   TimeUnit.SECONDS);
    }
  }

  @Override
  public void onProviderDisabled(String provider) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onProviderEnabled(String provider) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {
    // TODO Auto-generated method stub

  }

  @Override
  public void run() {
    Location loc=getBestLocation();

    if (loc == null) {
      Log.wtf(getClass().getSimpleName(),
              "Got null for getBestLocation()");
    }
    else {
      Log.i(getClass().getSimpleName(),
            String.format("%s %f:%f (%f meters)", loc.getProvider(),
                          loc.getLatitude(), loc.getLongitude(),
                          loc.getAccuracy()));
    }
  }

  private Location getBestLocation() {
    Location gps=mgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    Location network=
        mgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

    // start off by handling cases where we only have one

    if (gps == null) {
      return(network);
    }

    if (network == null) {
      return(gps);
    }

    Location older=(gps.getTime() < network.getTime() ? gps : network);
    Location newer=(gps == older ? network : gps);

    // older and less accurate fixes suck

    if (older.getAccuracy() <= newer.getAccuracy()) {
      return(newer);
    }

    // if older is within error radius of newer, assume
    // not moving and go with older (since has better
    // accuracy, else would have been caught by previous
    // condition)

    // ideally, this would really be "if the odds of
    // the older being within the error radius of the
    // newer are higher than 50%", taking into account
    // the older one's accuracy as well -- the
    // implementation
    // of this is left as an exercise for the reader

    if (newer.distanceTo(older) < newer.getAccuracy()) {
      return(older);
    }

    // if all else fails, choose the newer one -- the device
    // is probably moving, and so we are better off with the
    // newer fix, even if less accurate

    return(newer);
  }
}
