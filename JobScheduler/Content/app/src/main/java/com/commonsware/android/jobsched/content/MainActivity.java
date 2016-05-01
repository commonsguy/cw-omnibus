/***
  Copyright (c) 2008-2016 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.jobsched.content;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;
import static android.Manifest.permission.READ_CONTACTS;

public class MainActivity extends Activity {
  private static final String[] PERMS_ALL={
    READ_CONTACTS
  };
  private static final int RESULT_PERMS_INITIAL=1339;
  private static final String STATE_IN_PERMISSION=
    "com.commonsware.android.jobsched.content.inPermission";
  private boolean isInPermission=false;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (savedInstanceState!=null) {
      isInPermission=
        savedInstanceState.getBoolean(STATE_IN_PERMISSION, false);
    }

    if (!isInPermission) {
      if (hasPermission(READ_CONTACTS)) {
        configureJob();
      }
      else {
        isInPermission=true;
        ActivityCompat.requestPermissions(this, PERMS_ALL,
          RESULT_PERMS_INITIAL);
      }
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
    boolean sadTrombone=true;

    isInPermission=false;

    if (requestCode==RESULT_PERMS_INITIAL) {
      if (hasPermission(READ_CONTACTS)) {
        configureJob();
        sadTrombone=false;
      }
    }

    if (sadTrombone) {
      Toast.makeText(this, R.string.msg_no_perm,
        Toast.LENGTH_LONG).show();
    }
  }

  private void configureJob() {
    Toast.makeText(this, R.string.msg_add,
      Toast.LENGTH_LONG).show();
    DemoJobService.schedule(this);
    finish();
  }

  private boolean hasPermission(String perm) {
    return(ContextCompat.checkSelfPermission(this, perm)==
      PackageManager.PERMISSION_GRANTED);
  }
}
