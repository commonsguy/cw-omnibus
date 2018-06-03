/***
  Copyright (c) 2013 CommonsWare, LLC
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

package com.commonsware.android.video.browse;

import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import java.io.File;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AbstractPermissionActivity {
  private static final String[] PERMS={READ_EXTERNAL_STORAGE};

  @Override
  protected String[] getDesiredPermissions() {
    return PERMS;
  }

  @Override
  protected void onPermissionDenied() {
    Toast
      .makeText(this, R.string.msg_no_perm, Toast.LENGTH_LONG)
      .show();
    finish();
  }

  @Override
  public void onReady() {
    setContentView(R.layout.main);
  }

  public void onVideoSelected(String uri, String mimeType) {
    Uri video=Uri.fromFile(new File(uri));
    Intent i=new Intent(Intent.ACTION_VIEW);

    i.setDataAndType(video, mimeType);
    startActivity(i);
  }
}
