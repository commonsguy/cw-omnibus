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
    http://commonsware.com/Android
 */

package com.commonsware.android.audiolstream;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.ParcelFileDescriptor.AutoCloseOutputStream;
import android.util.Log;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class PipeProvider extends ContentProvider {
  public static final Uri CONTENT_URI=
      Uri.parse("content://com.commonsware.android.audiolstream/");
  private static final HashMap<String, String> MIME_TYPES=
      new HashMap<String, String>();

  static {
    MIME_TYPES.put(".ogg", "audio/ogg");
  }

  @Override
  public boolean onCreate() {
    return(true);
  }

  @Override
  public String getType(Uri uri) {
    String path=uri.toString();

    for (String extension : MIME_TYPES.keySet()) {
      if (path.endsWith(extension)) {
        return(MIME_TYPES.get(extension));
      }
    }

    return(null);
  }

  @Override
  public ParcelFileDescriptor openFile(Uri uri, String mode)
                                                            throws FileNotFoundException {
    ParcelFileDescriptor[] pipe=null;
    
    try {
      pipe=ParcelFileDescriptor.createPipe();
      AssetManager assets=getContext().getResources().getAssets();

      new TransferTask(assets.open(uri.getLastPathSegment()),
                       new AutoCloseOutputStream(pipe[1])).start();
    }
    catch (IOException e) {
      Log.e(getClass().getSimpleName(), "Exception opening pipe", e);
      throw new FileNotFoundException("Could not open pipe for: "
          + uri.toString());
    }

    return(pipe[0]);
  }

  @Override
  public Cursor query(Uri url, String[] projection, String selection,
                      String[] selectionArgs, String sort) {
    throw new RuntimeException("Operation not supported");
  }

  @Override
  public Uri insert(Uri uri, ContentValues initialValues) {
    throw new RuntimeException("Operation not supported");
  }

  @Override
  public int update(Uri uri, ContentValues values, String where,
                    String[] whereArgs) {
    throw new RuntimeException("Operation not supported");
  }

  @Override
  public int delete(Uri uri, String where, String[] whereArgs) {
    throw new RuntimeException("Operation not supported");
  }

  static class TransferTask extends Thread {
    InputStream in;
    OutputStream out;

    TransferTask(InputStream in, OutputStream out) {
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
        out.close();
      }
      catch (IOException e) {
        Log.e(getClass().getSimpleName(),
              "Exception transferring file", e);
      }
    }
  }
}