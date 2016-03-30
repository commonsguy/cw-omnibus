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

package com.commonsware.android.anti.camera;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class MainActivity extends Activity {
  private static int RESULT_CAPTURE=2343;
  private static final String[] PROJECTION=
    {MediaStore.Images.Media.DATA};
  private static final String FILENAME="photo.jpeg";
  private ImageView photo;
  private Button btnView;
  private File photoCopy;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    photo=(ImageView)findViewById(R.id.photo);
    btnView=(Button)findViewById(R.id.view);
  }

  public void capturePhoto(View v) {
    startActivityForResult(
      new Intent(MediaStore.ACTION_IMAGE_CAPTURE),
      RESULT_CAPTURE);
  }

  public void viewPhoto(View v) {
    Intent i=new Intent(Intent.ACTION_VIEW);

    i.setDataAndType(Uri.fromFile(photoCopy), "image/jpeg");
    startActivity(i);
  }

  @Override
  protected void onActivityResult(int requestCode,
                                  int resultCode,
                                  Intent data) {
    if (requestCode==RESULT_CAPTURE) {
      if (resultCode==RESULT_OK) {
        Uri photoUri=data.getData();

        photo.setImageURI(photoUri);
        savePhotoInfoToDatabase(photoUri);

        File photoPath=new File(getPath(photoUri));

        photoCopy=new File(getExternalCacheDir(), FILENAME);

        try {
          copy(photoPath, photoCopy);
        }
        catch (IOException e) {
          Toast.makeText(this, R.string.msg_failure,
            Toast.LENGTH_LONG).show();
          Log.e(getClass().getSimpleName(),
            getString(R.string.msg_failure), e);
        }

        btnView.setEnabled(true);
      }
    }
  }

  private String getPath(Uri photoUri) {
    Cursor c=managedQuery(photoUri, PROJECTION, null, null, null);
    int dataColumn=
      c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

    c.moveToFirst();

    return(c.getString(dataColumn));
  }

  private void savePhotoInfoToDatabase(Uri photoUri) {
    /*
      For the purposes of this sample app, assume that this
      method does what the name suggests: inserts a row in some
      SQLite database, containing the Uri and other info, such as
      the current date and time. Assume that this method also
      does its database I/O on a background thread.
     */
  }

  private static void copy(File src, File dest)
    throws IOException {
    dest.getParentFile().mkdirs();

    FileChannel srcChannel=null;
    FileChannel destChannel=null;

    try {
      srcChannel=new FileInputStream(src).getChannel();
      destChannel=new FileOutputStream(dest).getChannel();
      destChannel.transferFrom(srcChannel, 0, srcChannel.size());
    }
    finally {
      if (srcChannel!=null) {
        srcChannel.close();
      }

      if (destChannel!=null) {
        destChannel.close();
      }
    }
  }
}
