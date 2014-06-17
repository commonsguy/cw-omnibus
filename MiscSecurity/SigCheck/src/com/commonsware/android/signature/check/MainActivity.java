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
    http://commonsware.com/Android
 */

package com.commonsware.android.signature.check;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    int msg=R.string.package_invalid;

    try {
      if (validate("com.commonsware.android.signature.dump",
                   R.raw.com_commonsware_android_signature_dump)) {
        msg=R.string.package_valid;
      }
    }
    catch (Exception e) {
      Log.e(getClass().getSimpleName(), "Exception in validation", e);
      msg=R.string.we_crashed;
    }

    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

    finish();
  }

  boolean validate(String pkg, int raw) throws NameNotFoundException,
                                       NotFoundException, IOException {
    PackageManager mgr=getPackageManager();
    PackageInfo pkgInfo=
        mgr.getPackageInfo(pkg, PackageManager.GET_SIGNATURES);
    Signature[] signatures=pkgInfo.signatures;
    byte[] local=signatures[0].toByteArray();

    return(isEqual(new ByteArrayInputStream(local),
                   getResources().openRawResource(raw)));
  }

  // from http://stackoverflow.com/a/4245881/115145

  private boolean isEqual(InputStream i1, InputStream i2)
                                                         throws IOException {
    byte[] buf1=new byte[1024];
    byte[] buf2=new byte[1024];

    try {
      DataInputStream d2=new DataInputStream(i2);
      int len;
      while ((len=i1.read(buf1)) >= 0) {
        d2.readFully(buf2, 0, len);
        for (int i=0; i < len; i++)
          if (buf1[i] != buf2[i])
            return false;
      }
      return d2.read() < 0; // is the end of the second file
                            // also.
    }
    catch (EOFException ioe) {
      return false;
    }
    finally {
      i1.close();
      i2.close();
    }
  }
}
