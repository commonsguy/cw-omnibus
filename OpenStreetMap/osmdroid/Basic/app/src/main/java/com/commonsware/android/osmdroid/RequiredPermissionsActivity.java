/***
  Copyright (c) 2016 CommonsWare, LLC
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

package com.commonsware.android.osmdroid;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import java.util.ArrayList;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

abstract public class RequiredPermissionsActivity extends Activity {
  abstract protected String[] getRequiredPermissions();
  abstract protected void createForRealz();

  private static final String STATE_IN_PERMISSION=
    RequiredPermissionsActivity.class.getCanonicalName()+".STATE_IN_PERMISSION";
  private static final int REQUEST_PERMS=1337;
  private boolean isInPermission=false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (savedInstanceState!=null) {
      isInPermission=
        savedInstanceState.getBoolean(STATE_IN_PERMISSION, false);
    }

    if (hasPermissions(getRequiredPermissions())) {
      createForRealz();
    }
    else if (!isInPermission) {
      isInPermission=true;
      ActivityCompat.requestPermissions(this,
        netPermissions(getRequiredPermissions()),
        REQUEST_PERMS);
    }
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
      if (hasPermissions(getRequiredPermissions())) {
        createForRealz();
      }
      else {
        Toast
          .makeText(this, R.string.msg_no_perm, Toast.LENGTH_LONG)
          .show();
        finish();
      }
    }
  }

  private String[] netPermissions(String[] wanted) {
    ArrayList<String> result=new ArrayList<String>();

    for (String perm : wanted) {
      if (!hasPermission(perm)) {
        result.add(perm);
      }
    }

    return(result.toArray(new String[result.size()]));
  }

  private boolean hasPermission(String perm) {
    return(ContextCompat.checkSelfPermission(this, perm)==
      PackageManager.PERMISSION_GRANTED);
  }

  private boolean hasPermissions(String... perms) {
    boolean result=true;

    for (String perm : perms) {
      result=result && hasPermission(perm);
    }

    return(result);
  }
}
