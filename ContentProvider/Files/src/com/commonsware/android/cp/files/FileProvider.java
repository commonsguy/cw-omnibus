/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Advanced Android Development_
    http://commonsware.com/AdvAndroid
*/

   
package com.commonsware.android.cp.files;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;

public class FileProvider extends ContentProvider {
  public static final Uri CONTENT_URI=Uri.parse("content://com.commonsware.android.cp.files/");
  private static final HashMap<String, String> MIME_TYPES=new HashMap<String, String>();
  
  static {
    MIME_TYPES.put(".pdf", "application/pdf");
  }

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
    File f=new File(getContext().getFilesDir(), uri.getPath());
    
    if (f.exists()) {
      return(ParcelFileDescriptor.open(f,
                                        ParcelFileDescriptor.MODE_READ_ONLY));
    }

    throw new FileNotFoundException(uri.getPath());  
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
  public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
    throw new RuntimeException("Operation not supported");
  }
  
  @Override
  public int delete(Uri uri, String where, String[] whereArgs) {
    throw new RuntimeException("Operation not supported");
  }
  
  static private void copy(InputStream in, File dst) throws IOException {
    FileOutputStream out=new FileOutputStream(dst);
    byte[] buf=new byte[1024];
    int len;
    
    while((len=in.read(buf))>0) {
      out.write(buf, 0, len);
    }
    
    in.close();
    out.close();
  }
}