/***
  Copyright (c) 2013 CommonsWare, LLC
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

package com.commonsware.android.videorecord;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import java.io.File;

public class MainActivity extends Activity {
  private static final int REQUEST_ID=1337;
  private Uri result=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    File video=
        new File(
                 Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
                 "sample.mp4");

    if (video.exists()) {
      video.delete();
    }

    Intent i=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

    result=Uri.fromFile(video);
    i.putExtra(MediaStore.EXTRA_OUTPUT, result);
    i.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

    startActivityForResult(i, REQUEST_ID);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
                                  Intent data) {
    if (requestCode == REQUEST_ID && resultCode == RESULT_OK) {
      Intent view=
          new Intent(Intent.ACTION_VIEW).setDataAndType(result,
                                                        "video/mp4");

      startActivity(view);
    }

    finish();
  }
}
