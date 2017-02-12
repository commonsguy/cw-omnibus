/***
  Copyright (c) 2012-2014 CommonsWare, LLC
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

package com.commonsware.android.cpproxy.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.CrossProcessCursor;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.CursorWrapper;
import android.net.Uri;
import android.preference.PreferenceManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.commonsware.cwac.security.PermissionLint;
import com.commonsware.cwac.security.PermissionUtils;

public abstract class AbstractCPProxy extends ContentProvider {
  abstract protected Uri convertUri(Uri uri);

  private static final String PREFS_FIRST_RUN="firstRun";
  private static final String PREFS_TAINTED="tainted";
  private boolean tainted=false;

  public AbstractCPProxy() {
    super();
  }

  @Override
  public boolean onCreate() {
    SharedPreferences prefs=
        PreferenceManager.getDefaultSharedPreferences(getContext());

    if (prefs.getBoolean(PREFS_FIRST_RUN, true)) {
      SharedPreferences.Editor editor=
          prefs.edit().putBoolean(PREFS_FIRST_RUN, false);

      HashMap<PackageInfo, ArrayList<PermissionLint>> entries=
          PermissionUtils.checkCustomPermissions(getContext());

      for (Map.Entry<PackageInfo, ArrayList<PermissionLint>> entry : entries.entrySet()) {
        if (!"com.commonsware.android.cpproxy.consumer".equals(entry.getKey().packageName)) {
          tainted=true;
          break;
        }
      }
      
      editor.putBoolean(PREFS_TAINTED, tainted).apply();
    }
    else {
      tainted=prefs.getBoolean(PREFS_TAINTED, true);
    }

    return(true);
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection,
                      String[] selectionArgs, String sortOrder) {
    checkTainted();
    
    Cursor result=
        getContext().getContentResolver().query(convertUri(uri),
                                                projection, selection,
                                                selectionArgs,
                                                sortOrder);

    return(new CrossProcessCursorWrapper(result));
  }

  @Override
  public Uri insert(Uri uri, ContentValues values) {
    checkTainted();
    
    return(getContext().getContentResolver().insert(convertUri(uri),
                                                    values));
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection,
                    String[] selectionArgs) {
    checkTainted();
    
    return(getContext().getContentResolver().update(convertUri(uri),
                                                    values, selection,
                                                    selectionArgs));
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    checkTainted();
    
    return(getContext().getContentResolver().delete(convertUri(uri),
                                                    selection,
                                                    selectionArgs));
  }

  @Override
  public String getType(Uri uri) {
    checkTainted();
    
    return(getContext().getContentResolver().getType(convertUri(uri)));
  }
  
  private void checkTainted() {
    if (tainted) {
      throw new RuntimeException(getContext().getString(R.string.tainted_abort));
    }
  }

  // following from
  // http://stackoverflow.com/a/5243978/115145

  public class CrossProcessCursorWrapper extends CursorWrapper
      implements CrossProcessCursor {
    public CrossProcessCursorWrapper(Cursor cursor) {
      super(cursor);
    }

    @Override
    public CursorWindow getWindow() {
      return null;
    }

    @Override
    public void fillWindow(int position, CursorWindow window) {
      if (position < 0 || position > getCount()) {
        return;
      }
      window.acquireReference();
      try {
        moveToPosition(position - 1);
        window.clear();
        window.setStartPosition(position);
        int columnNum=getColumnCount();
        window.setNumColumns(columnNum);
        while (moveToNext() && window.allocRow()) {
          for (int i=0; i < columnNum; i++) {
            String field=getString(i);
            if (field != null) {
              if (!window.putString(field, getPosition(), i)) {
                window.freeLastRow();
                break;
              }
            }
            else {
              if (!window.putNull(getPosition(), i)) {
                window.freeLastRow();
                break;
              }
            }
          }
        }
      }
      catch (IllegalStateException e) {
        // simply ignore it
      }
      finally {
        window.releaseReference();
      }
    }

    @Override
    public boolean onMove(int oldPosition, int newPosition) {
      return true;
    }
  }
}