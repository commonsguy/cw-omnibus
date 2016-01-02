/***
  Copyright (c) 2014-2015 CommonsWare, LLC
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

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import com.commonsware.cwac.provider.LegacyCompatCursorWrapper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

abstract class AbstractFileProvider extends ContentProvider {
  private final static String[] OPENABLE_PROJECTION= {
      OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE };

  @Override
  public Cursor query(Uri uri, String[] projection, String selection,
                      String[] selectionArgs, String sortOrder) {
    if (projection == null) {
      projection=OPENABLE_PROJECTION;
    }

    final MatrixCursor cursor=new MatrixCursor(projection, 1);

    MatrixCursor.RowBuilder b=cursor.newRow();

    for (String col : projection) {
      if (OpenableColumns.DISPLAY_NAME.equals(col)) {
        b.add(getFileName(uri));
      }
      else if (OpenableColumns.SIZE.equals(col)) {
        b.add(getDataLength(uri));
      }
      else { // unknown, so just add null
        b.add(null);
      }
    }

    return(new LegacyCompatCursorWrapper(cursor));
  }

  @Override
  public String getType(Uri uri) {
    return(URLConnection.guessContentTypeFromName(uri.toString()));
  }

  protected String getFileName(Uri uri) {
    return(uri.getLastPathSegment());
  }

  protected long getDataLength(Uri uri) {
    return(AssetFileDescriptor.UNKNOWN_LENGTH);
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

  static void copy(InputStream in, File dst)
                                                      throws IOException {
    FileOutputStream out=new FileOutputStream(dst);
    byte[] buf=new byte[1024];
    int len;

    while ((len=in.read(buf)) >= 0) {
      out.write(buf, 0, len);
    }

    in.close();
    out.close();
  }
}
