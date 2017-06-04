/***
  Copyright (c) 2012 CommonsWare, LLC
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

package com.commonsware.android.cp.pipe;

import android.content.res.AssetManager;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.storage.StorageManager;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class PipeProvider extends AbstractFileProvider {
  public static final Uri CONTENT_URI=
      Uri.parse("content://"+BuildConfig.APPLICATION_ID+".provider");

  @Override
  public boolean onCreate() {
    return(true);
  }

  @Override
  public ParcelFileDescriptor openFile(Uri uri, String mode)
    throws FileNotFoundException {
    AssetManager assets=getContext().getAssets();

    try {
      InputStream in=
        assets.open(uri.getLastPathSegment(), AssetManager.ACCESS_STREAMING);
      byte[] content=readAll(in);

      StorageManager sm=getContext().getSystemService(StorageManager.class);

      return(sm.openProxyFileDescriptor(ParcelFileDescriptor.MODE_READ_ONLY,
        new BufferProxyCallback(content)));
    }
    catch (IOException e) {
      Log.e(getClass().getSimpleName(), "Exception opening pipe", e);

      throw new FileNotFoundException("Could not open pipe for: "
        +uri.toString());
    }
  }

  // inspired by http://stackoverflow.com/a/17861016/115145

  public static byte[] readAll(InputStream is) throws IOException {
    try (ByteArrayOutputStream baos=new ByteArrayOutputStream()) {
      byte[] buf=new byte[16384];

      for (int len; (len = is.read(buf)) != -1; ) {
        baos.write(buf, 0, len);
      }

      baos.flush();
      is.close();

      return(baos.toByteArray());
    }
  }
}