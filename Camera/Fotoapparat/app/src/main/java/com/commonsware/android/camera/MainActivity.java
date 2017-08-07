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

package com.commonsware.android.camera;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AbstractPermissionActivity {
  private static final String[] PERMS_ALL={
    CAMERA
  };
  private static final int RESULT_PICTURE_TAKEN=1337;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
  }

  @Override
  public void onReady() {
    // unused
  }

  @Override
  protected String[] getDesiredPermissions() {
    return(PERMS_ALL);
  }

  @Override
  protected void onPermissionDenied() {
    Toast.makeText(this, R.string.msg_no_perm, Toast.LENGTH_LONG).show();
    finish();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
                                  Intent data) {
    if (requestCode==RESULT_PICTURE_TAKEN && resultCode==RESULT_OK) {
      Toast.makeText(this, R.string.msg_pic_taken, Toast.LENGTH_LONG).show();
    }
    else {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

  public void takePicture(View v) {
    CameraActivity.takePhoto(this, RESULT_PICTURE_TAKEN);
  }
}
