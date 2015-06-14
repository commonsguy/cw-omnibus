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

package com.commonsware.android.cp.v4file;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FilesCPDemo extends Activity {
  private static final String AUTHORITY="com.commonsware.android.cp.v4file";
      
  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);

    File f=new File(getFilesDir(), "test.pdf");

    if (!f.exists()) {
      AssetManager assets=getResources().getAssets();

      try {
        copy(assets.open("test.pdf"), f);
      }
      catch (IOException e) {
        Log.e("FileProvider", "Exception copying from assets", e);
      }
    }

    Intent i=
        new Intent(Intent.ACTION_VIEW,
                   FileProvider.getUriForFile(this, AUTHORITY, f));

    i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    startActivity(i);
    finish();
  }

  static private void copy(InputStream in, File dst) throws IOException {
    FileOutputStream out=new FileOutputStream(dst);
    byte[] buf=new byte[1024];
    int len;

    while ((len=in.read(buf)) > 0) {
      out.write(buf, 0, len);
    }

    in.close();
    out.close();
  }
}