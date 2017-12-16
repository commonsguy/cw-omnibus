/***
 Copyright (c) 2017 CommonsWare, LLC
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

package com.commonsware.android.bluetooth.rxecho;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.Toast;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class MainActivity extends AbstractPermissionActivity
  implements RosterFragment.Contract {
  static final int REQUEST_ENABLE_BLUETOOTH=1234;
  private static final String[] PERMS={ACCESS_COARSE_LOCATION};
  private RosterFragment roster;

  @Override
  protected String[] getDesiredPermissions() {
    return(PERMS);
  }

  @Override
  protected void onPermissionDenied() {
    Toast.makeText(this, R.string.msg_away, Toast.LENGTH_LONG).show();
    finish();
  }

  @Override
  protected void onReady() {
    Fragment f=getSupportFragmentManager().findFragmentById(android.R.id.content);

    if (f==null) {
      roster=new RosterFragment();

      getSupportFragmentManager()
        .beginTransaction()
        .add(android.R.id.content, roster)
        .commit();
    }
    else if (f instanceof RosterFragment) {
      roster=(RosterFragment)f;
    }

    getSupportFragmentManager().addOnBackStackChangedListener(() -> {
        if (getSupportFragmentManager().getBackStackEntryCount()==0) {
          setTitle(R.string.app_name);
        }
      });
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode==REQUEST_ENABLE_BLUETOOTH && roster!=null) {
      roster.enableBluetooth(true);
    }
  }

  @Override
  public void showDevice(BluetoothDevice device) {
    setTitle(getString(R.string.app_name)+" "+device.getAddress());

    getSupportFragmentManager()
      .beginTransaction()
      .hide(roster)
      .add(android.R.id.content, DeviceFragment.newInstance(device))
      .addToBackStack(null)
      .commit();
  }
}
