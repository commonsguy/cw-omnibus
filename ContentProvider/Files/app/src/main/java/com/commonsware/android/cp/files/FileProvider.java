/***
  Copyright (c) 2008-2014 CommonsWare, LLC
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

package com.commonsware.android.cp.files;

import android.content.res.AssetManager;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileProvider extends AbstractFileProvider {
  public static final Uri CONTENT_URI=
      Uri.parse("content://com.commonsware.android.cp.files/");

  @Override
  public boolean onCreate() {
    File f=new File(getContext().getFilesDir(), "test.pdf");

    if (!f.exists()) {
      AssetManager assets=getContext().getResources().getAssets();

      try {
        copy(assets.open("test.pdf"), f);
      }
      catch (IOException e) {
        Log.e("FileProvider", "Exception copying from assets", e);

        return(false);
      }
    }

    return(true);
  }

  @Override
  public ParcelFileDescriptor openFile(Uri uri, String mode)
                                                            throws FileNotFoundException {
    File root=getContext().getFilesDir();
    File f=new File(root, uri.getPath()).getAbsoluteFile();

    if (!f.getPath().startsWith(root.getPath())) {
      throw new
        SecurityException("Resolved path jumped beyond root");
    }

    if (f.exists()) {
      return(ParcelFileDescriptor.open(f, parseMode(mode)));
    }

    throw new FileNotFoundException(uri.getPath());
  }

  @Override
  protected long getDataLength(Uri uri) {
    File f=new File(getContext().getFilesDir(), uri.getPath());

    return(f.length());
  }

  // following is from ParcelFileDescriptor source code
  // Copyright (C) 2006 The Android Open Source Project
  // (even though this method was added much after 2006...)

  private static int parseMode(String mode) {
    final int modeBits;
    if ("r".equals(mode)) {
      modeBits=ParcelFileDescriptor.MODE_READ_ONLY;
    }
    else if ("w".equals(mode) || "wt".equals(mode)) {
      modeBits=
          ParcelFileDescriptor.MODE_WRITE_ONLY
              | ParcelFileDescriptor.MODE_CREATE
              | ParcelFileDescriptor.MODE_TRUNCATE;
    }
    else if ("wa".equals(mode)) {
      modeBits=
          ParcelFileDescriptor.MODE_WRITE_ONLY
              | ParcelFileDescriptor.MODE_CREATE
              | ParcelFileDescriptor.MODE_APPEND;
    }
    else if ("rw".equals(mode)) {
      modeBits=
          ParcelFileDescriptor.MODE_READ_WRITE
              | ParcelFileDescriptor.MODE_CREATE;
    }
    else if ("rwt".equals(mode)) {
      modeBits=
          ParcelFileDescriptor.MODE_READ_WRITE
              | ParcelFileDescriptor.MODE_CREATE
              | ParcelFileDescriptor.MODE_TRUNCATE;
    }
    else {
      throw new IllegalArgumentException("Bad mode '" + mode + "'");
    }
    return modeBits;
  }
}