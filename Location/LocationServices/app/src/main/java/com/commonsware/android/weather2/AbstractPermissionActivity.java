/***
 Copyright (c) 2015-2017 CommonsWare, LLC
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

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import java.util.ArrayList;

abstract public class AbstractPermissionActivity
  extends FragmentActivity {
  abstract protected String[] getDesiredPermissions();
  abstract protected void onPermissionDenied();
  abstract protected void onReady(Bundle state);

  private static final int REQUEST_PERMISSION=61125;
  private static final String STATE_IN_PERMISSION="inPermission";
  private boolean isInPermission=false;
  private Bundle state;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    this.state=savedInstanceState;

    if (state!=null) {
      isInPermission=state.getBoolean(STATE_IN_PERMISSION, false);
    }

    if (hasAllPermissions(getDesiredPermissions())) {
      onReady(state);
    }
    else if (!isInPermission) {
      isInPermission=true;

      ActivityCompat
        .requestPermissions(this,
          netPermissions(getDesiredPermissions()),
          REQUEST_PERMISSION);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String[] permissions,
                                         int[] grantResults) {
    isInPermission=false;

    if (requestCode==REQUEST_PERMISSION) {
      if (hasAllPermissions(getDesiredPermissions())) {
        onReady(state);
      }
      else {
        onPermissionDenied();
      }
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putBoolean(STATE_IN_PERMISSION, isInPermission);
  }

  private boolean hasAllPermissions(String[] perms) {
    for (String perm : perms) {
      if (!hasPermission(perm)) {
        return(false);
      }
    }

    return(true);
  }

  private boolean hasPermission(String perm) {
    return(ContextCompat.checkSelfPermission(this, perm)==
      PackageManager.PERMISSION_GRANTED);
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
}
