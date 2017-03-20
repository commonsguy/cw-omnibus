/***
 Copyright (c) 2008-2017 CommonsWare, LLC
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

package com.commonsware.android.camcon;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.widget.Toast;
import java.io.File;
import java.util.List;

public class MainActivity extends Activity {
  private static final String EXTRA_FILENAME=
    "com.commonsware.android.camcon.EXTRA_FILENAME";
  private static final String FILENAME="CameraContentDemo.jpeg";
  private static final int CONTENT_REQUEST=1337;
  private static final String AUTHORITY=
    BuildConfig.APPLICATION_ID+".provider";
  private static final String PHOTOS="photos";
  private File output=null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (savedInstanceState==null) {
      output=new File(new File(getFilesDir(), PHOTOS), FILENAME);

      if (output.exists()) {
        output.delete();
      }
      else {
        output.getParentFile().mkdirs();
      }

      Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      Uri outputUri=FileProvider.getUriForFile(this, AUTHORITY, output);

      i.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);

      if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
        i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
      }
      else if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN) {
        ClipData clip=
          ClipData.newUri(getContentResolver(), "A photo", outputUri);

        i.setClipData(clip);
        i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
      }
      else {
        List<ResolveInfo> resInfoList=
          getPackageManager()
            .queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo resolveInfo : resInfoList) {
          String packageName = resolveInfo.activityInfo.packageName;
          grantUriPermission(packageName, outputUri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
      }

      try {
        startActivityForResult(i, CONTENT_REQUEST);
      }
      catch (ActivityNotFoundException e) {
        Toast.makeText(this, R.string.msg_no_camera, Toast.LENGTH_LONG).show();
        finish();
      }
    }
    else {
      output=(File)savedInstanceState.getSerializable(EXTRA_FILENAME);
    }
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
        Uri outputUri=FileProvider.getUriForFile(this, AUTHORITY, output);

        i.setDataAndType(outputUri, "image/jpeg");
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
          startActivity(i);
        }
        catch (ActivityNotFoundException e) {
          Toast.makeText(this, R.string.msg_no_viewer, Toast.LENGTH_LONG).show();
        }

        finish();
      }
    }
  }
}