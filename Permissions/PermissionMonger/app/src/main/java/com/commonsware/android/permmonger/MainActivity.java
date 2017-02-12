/***
 Copyright (c) 2015 CommonsWare, LLC
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

package com.commonsware.android.permmonger;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
  private static final String[] INITIAL_PERMS={
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.READ_CONTACTS
  };
  private static final String[] CAMERA_PERMS={
    Manifest.permission.CAMERA
  };
  private static final String[] CONTACTS_PERMS={
      Manifest.permission.READ_CONTACTS
  };
  private static final String[] LOCATION_PERMS={
      Manifest.permission.ACCESS_FINE_LOCATION
  };
  private static final int INITIAL_REQUEST=1337;
  private static final int CAMERA_REQUEST=INITIAL_REQUEST+1;
  private static final int CONTACTS_REQUEST=INITIAL_REQUEST+2;
  private static final int LOCATION_REQUEST=INITIAL_REQUEST+3;
  private TextView location;
  private TextView camera;
  private TextView internet;
  private TextView contacts;
  private TextView storage;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    location=(TextView)findViewById(R.id.location_value);
    camera=(TextView)findViewById(R.id.camera_value);
    internet=(TextView)findViewById(R.id.internet_value);
    contacts=(TextView)findViewById(R.id.contacts_value);
    storage=(TextView)findViewById(R.id.storage_value);

    if (!canAccessLocation() || !canAccessContacts()) {
      requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();

    updateTable();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.actions, menu);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case R.id.camera:
        if (canAccessCamera()) {
          doCameraThing();
        }
        else {
          requestPermissions(CAMERA_PERMS, CAMERA_REQUEST);
        }
        return(true);

      case R.id.contacts:
        if (canAccessContacts()) {
          doContactsThing();
        }
        else {
          requestPermissions(CONTACTS_PERMS, CONTACTS_REQUEST);
        }
        return(true);

      case R.id.location:
        if (canAccessLocation()) {
          doLocationThing();
        }
        else {
          requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
        }
        return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    updateTable();

    switch(requestCode) {
      case CAMERA_REQUEST:
        if (canAccessCamera()) {
          doCameraThing();
        }
        else {
          bzzzt();
        }
        break;

      case CONTACTS_REQUEST:
        if (canAccessContacts()) {
          doContactsThing();
        }
        else {
          bzzzt();
        }
        break;

      case LOCATION_REQUEST:
        if (canAccessLocation()) {
          doLocationThing();
        }
        else {
          bzzzt();
        }
        break;
    }
  }

  private void updateTable() {
    location.setText(String.valueOf(canAccessLocation()));
    camera.setText(String.valueOf(canAccessCamera()));
    internet.setText(String.valueOf(hasPermission(Manifest.permission.INTERNET)));
    contacts.setText(String.valueOf(canAccessContacts()));
    storage.setText(String.valueOf(hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)));
  }

  private boolean canAccessLocation() {
    return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
  }

  private boolean canAccessCamera() {
    return(hasPermission(Manifest.permission.CAMERA));
  }

  private boolean canAccessContacts() {
    return(hasPermission(Manifest.permission.READ_CONTACTS));
  }

  private boolean hasPermission(String perm) {
    return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(perm));
  }

  private void bzzzt() {
    Toast.makeText(this, R.string.toast_bzzzt, Toast.LENGTH_LONG).show();
  }

  private void doCameraThing() {
    Toast.makeText(this, R.string.toast_camera, Toast.LENGTH_SHORT).show();
  }

  private void doContactsThing() {
    Toast.makeText(this, R.string.toast_contacts, Toast.LENGTH_SHORT).show();
  }

  private void doLocationThing() {
    Toast.makeText(this, R.string.toast_location, Toast.LENGTH_SHORT).show();
  }
}
