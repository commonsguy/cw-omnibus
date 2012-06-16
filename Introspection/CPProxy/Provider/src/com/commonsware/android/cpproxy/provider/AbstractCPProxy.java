/***
  Copyright (c) 2012 CommonsWare, LLC
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

package com.commonsware.android.cpproxy.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.CrossProcessCursor;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.CursorWrapper;
import android.net.Uri;

public abstract class AbstractCPProxy extends ContentProvider {
  abstract protected Uri convertUri(Uri uri);

  public AbstractCPProxy() {
    super();
  }

  @Override
  public boolean onCreate() {
    return(true);
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection,
                      String[] selectionArgs, String sortOrder) {
    Cursor result=
        getContext().getContentResolver().query(convertUri(uri),
                                                projection, selection,
                                                selectionArgs,
                                                sortOrder);

    return(new CrossProcessCursorWrapper(result));
  }

  @Override
  public Uri insert(Uri uri, ContentValues values) {
    return(getContext().getContentResolver().insert(convertUri(uri),
                                                    values));
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection,
                    String[] selectionArgs) {
    return(getContext().getContentResolver().update(convertUri(uri),
                                                    values, selection,
                                                    selectionArgs));
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    return(getContext().getContentResolver().delete(convertUri(uri),
                                                    selection,
                                                    selectionArgs));
  }

  @Override
  public String getType(Uri uri) {
    return(getContext().getContentResolver().getType(convertUri(uri)));
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