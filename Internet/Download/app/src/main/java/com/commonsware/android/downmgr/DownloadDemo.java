/***
  Copyright (c) 2008-2017 CommonsWare, LLC
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

package com.commonsware.android.downmgr;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Toast;

public class DownloadDemo extends AbstractPermissionActivity {

  @Override
  protected String[] getDesiredPermissions() {
    return(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE});
  }

  @Override
  protected void onPermissionDenied() {
    Toast
      .makeText(this, R.string.msg_sorry, Toast.LENGTH_LONG)
      .show();
    finish();
  }

  @Override
  public void onReady(Bundle savedInstanceState) {
    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                                .detectNetwork()
                                .penaltyDeath()
                                .build());
    
    if (getSupportFragmentManager().findFragmentById(android.R.id.content)==null) {
      getSupportFragmentManager().beginTransaction()
                                 .add(android.R.id.content,
                                      new DownloadFragment()).commit();
    }
  }

  public void viewLog() {
    startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
  }
}
