/***
 Copyright (c) 2008-2014 CommonsWare, LLC
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

package com.commonsware.android.advservice.remotebinding.sig;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import com.commonsware.android.advservice.remotebinding.IDownload;
import com.commonsware.cwac.security.SignatureUtils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends Service {
  @Override
  public IBinder onBind(Intent intent) {
    return(new DownloadBinder(this));
  }

  private static class DownloadBinder extends IDownload.Stub {
    private final PackageManager pm;
    private final String expectedHash;
    private final Context ctxt;

    public DownloadBinder(Context ctxt) {
      this.ctxt=ctxt.getApplicationContext();
      this.pm=this.ctxt.getPackageManager();
      this.expectedHash=this.ctxt.getString(
        R.string.expected_sig_hash);
    }

    @Override
    public void download(String url) {
      boolean ok=false;

      for (String pkg :
        pm.getPackagesForUid(Binder.getCallingUid())) {
        try {
          String otherHash=
            SignatureUtils.getSignatureHash(ctxt, pkg);

          if (expectedHash.equals(otherHash)) {
            ok=true;
            break;
          }
        }
        catch (Exception e) {
          Log.e(getClass().getSimpleName(),
            "Exception finding signature hash", e);
        }
      }

      if (ok) {
        new DownloadThread(url).start();
      }
      else {
        Log.e(getClass().getSimpleName(),
          "Could not validate client signature");
      }
    }
  }

  private static class DownloadThread extends Thread {
    String url=null;

    DownloadThread(String url) {
      this.url=url;
    }

    @Override
    public void run() {
      try {
        File root=
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        root.mkdirs();

        File output=new File(root, Uri.parse(url).getLastPathSegment());

        if (output.exists()) {
          output.delete();
        }

        HttpURLConnection c=(HttpURLConnection)new URL(url).openConnection();

        FileOutputStream fos=new FileOutputStream(output.getPath());
        BufferedOutputStream out=new BufferedOutputStream(fos);

        try {
          InputStream in=c.getInputStream();
          byte[] buffer=new byte[8192];
          int len=0;

          while ((len=in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
          }

          out.flush();
        }
        finally {
          fos.getFD().sync();
          out.close();
          c.disconnect();
        }
      }
      catch (IOException e2) {
        Log.e("DownloadJob", "Exception in download", e2);
      }
    }
  }
}