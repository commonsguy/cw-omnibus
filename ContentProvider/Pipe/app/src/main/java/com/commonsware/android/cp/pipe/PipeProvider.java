/***
  Copyright (c) 2012 CommonsWare, LLC
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

package com.commonsware.android.cp.pipe;

import android.content.res.AssetManager;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.ParcelFileDescriptor.AutoCloseOutputStream;
import android.util.Log;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PipeProvider extends AbstractFileProvider {
  public static final Uri CONTENT_URI=
      Uri.parse("content://com.commonsware.android.cp.pipe/");

  @Override
  public boolean onCreate() {
    return(true);
  }

  @Override
  public ParcelFileDescriptor openFile(Uri uri, String mode)
    throws FileNotFoundException {
    ParcelFileDescriptor[] pipe=null;

    try {
      pipe=ParcelFileDescriptor.createPipe();
      AssetManager assets=getContext().getResources().getAssets();

      new TransferThread(assets.open(uri.getLastPathSegment()),
                       new AutoCloseOutputStream(pipe[1])).start();
    }
    catch (IOException e) {
      Log.e(getClass().getSimpleName(), "Exception opening pipe", e);
      throw new FileNotFoundException("Could not open pipe for: "
          + uri.toString());
    }

    return(pipe[0]);
  }

  static class TransferThread extends Thread {
    InputStream in;
    OutputStream out;

    TransferThread(InputStream in, OutputStream out) {
      this.in=in;
      this.out=out;
    }

    @Override
    public void run() {
      byte[] buf=new byte[1024];
      int len;

      try {
        while ((len=in.read(buf)) >= 0) {
          out.write(buf, 0, len);
        }

        in.close();
        out.flush();
        out.close();
      }
      catch (IOException e) {
        Log.e(getClass().getSimpleName(),
              "Exception transferring file", e);
      }
    }
  }
}