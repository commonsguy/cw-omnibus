/***
 Copyright (c) 2008-2016 CommonsWare, LLC
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

package com.commonsware.android.camcon;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import java.io.File;

public class CameraContentDemoActivity extends Activity {
  private static final String EXTRA_FILENAME=
    "com.commonsware.android.camcon.EXTRA_FILENAME";
  private static final String FILENAME="CameraContentDemo.jpeg";
  private static final int CONTENT_REQUEST=1337;
  private File output=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    if (savedInstanceState==null) {
      File dir=
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

      dir.mkdirs();
      output=new File(dir, FILENAME);
    }
    else {
      output=(File)savedInstanceState.getSerializable(EXTRA_FILENAME);
    }

    if (output.exists()) {
      output.delete();
    }

    i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));

    startActivityForResult(i, CONTENT_REQUEST);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putSerializable(EXTRA_FILENAME, output);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
                                  Intent data) {
    if (requestCode == CONTENT_REQUEST) {
      if (resultCode == RESULT_OK) {
        Intent i=new Intent(Intent.ACTION_VIEW);
        
        i.setDataAndType(Uri.fromFile(output), "image/jpeg");
        startActivity(i);
        finish();
      }
    }
  }
}