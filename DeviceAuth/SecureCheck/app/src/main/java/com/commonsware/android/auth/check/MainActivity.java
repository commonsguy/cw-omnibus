/***
 Copyright (c) 2018 CommonsWare, LLC
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

package com.commonsware.android.auth.check;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
  private static final int REQUEST_CODE=1337;
  private KeyguardManager mgr;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mgr=(KeyguardManager)getSystemService(KEYGUARD_SERVICE);

    ImageView device=findViewById(R.id.device);
    ImageView keyguard=findViewById(R.id.keyguard);

    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
      if (mgr.isDeviceSecure()) {
        device.setImageResource(R.drawable.ic_lock_black_24dp);
      }
      else {
        device.setImageResource(R.drawable.ic_lock_open_black_24dp);
      }
    }
    else {
      device.setImageResource(R.drawable.ic_help_black_24dp);
    }

    if (mgr.isKeyguardSecure()) {
      keyguard.setImageResource(R.drawable.ic_lock_black_24dp);
    }
    else {
      keyguard.setImageResource(R.drawable.ic_lock_open_black_24dp);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
      getMenuInflater().inflate(R.menu.actions, menu);
    }

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId()==R.id.auth) {
      authenticate();

      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
                                  Intent data) {
    if (requestCode==REQUEST_CODE) {
      if (resultCode==RESULT_OK) {
        Toast.makeText(this, "Authenticated!", Toast.LENGTH_SHORT).show();
      }
      else {
        Toast.makeText(this, "WE ARE UNDER ATTACK!", Toast.LENGTH_SHORT).show();
      }
    }
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private void authenticate() {
    Intent i=mgr.createConfirmDeviceCredentialIntent("title", "description");

    if (i==null) {
      Toast.makeText(this, "No authentication required!", Toast.LENGTH_SHORT).show();
    }
    else {
      startActivityForResult(i, REQUEST_CODE);
    }
  }
}
