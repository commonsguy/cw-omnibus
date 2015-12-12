/***
 Copyright (c) 2015 CommonsWare, LLC
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

package com.commonsware.android.andcorder;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;

public class MainActivity extends Activity {
  private static final int REQUEST_SCREENCAST=59706;
  private MediaProjectionManager mgr;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mgr=(MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);

    startActivityForResult(mgr.createScreenCaptureIntent(),
      REQUEST_SCREENCAST);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode==REQUEST_SCREENCAST) {
      if (resultCode==RESULT_OK) {
        Intent i=
            new Intent(this, RecorderService.class)
                .putExtra(RecorderService.EXTRA_RESULT_CODE, resultCode)
                .putExtra(RecorderService.EXTRA_RESULT_INTENT, data);

        startService(i);
      }
    }

    finish();
  }
}
