/***
  Copyright (c) 2008-2012 CommonsWare, LLC
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

package com.commonsware.android.constants;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

public class Provider extends ContentProvider {
  private static final int CONSTANTS=1;
  private static final int CONSTANT_ID=2;
  private static final UriMatcher MATCHER;
  private static final String TABLE="constants";

  public static final class Constants implements BaseColumns {
    public static final Uri CONTENT_URI=
        Uri.parse("content://com.commonsware.android.constants.Provider/constants");
    public static final String DEFAULT_SORT_ORDER="title";
    public static final String TITLE="title";
    public static final String VALUE="value";
  }

  static {
    MATCHER=new UriMatcher(UriMatcher.NO_MATCH);
    MATCHER.addURI("com.commonsware.android.constants.Provider",
                   "constants", CONSTANTS);
    MATCHER.addURI("com.commonsware.android.constants.Provider",
                   "constants/#", CONSTANT_ID);
  }

  private DatabaseHelper db=null;

  @Override
  public boolean onCreate() {
    db=new DatabaseHelper(getContext());

    return(true);
  }

  @Override
  public Cursor query(Uri url, String[] projection, String selection,
                      String[] selectionArgs, String sort) {
    SQLiteQueryBuilder qb=new SQLiteQueryBuilder();

    qb.setTables(TABLE);

    String orderBy;

    if (TextUtils.isEmpty(sort)) {
      orderBy=Constants.DEFAULT_SORT_ORDER;
    }
    else {
      orderBy=sort;
    }

    Cursor c=
        qb.query(db.getReadableDatabase(), projection, selection,
                 selectionArgs, null, null, orderBy);

    c.setNotificationUri(getContext().getContentResolver(), url);

    return(c);
  }

  @Override
  public String getType(Uri url) {
    if (isCollectionUri(url)) {
      return("vnd.android.cursor.dir/constant");
    }

    return("vnd.android.cursor.item/constant");
  }

  @Override
  public Uri insert(Uri url, ContentValues initialValues) {
    long rowID=
        db.getWritableDatabase().insert(TABLE, Constants.TITLE,
                                        initialValues);

    if (rowID > 0) {
      Uri uri=
          ContentUris.withAppendedId(Provider.Constants.CONTENT_URI,
                                     rowID);
      getContext().getContentResolver().notifyChange(uri, null);

      return(uri);
    }

    throw new SQLException("Failed to insert row into " + url);
  }

  @Override
  public int delete(Uri url, String where, String[] whereArgs) {
    int count=db.getWritableDatabase().delete(TABLE, where, whereArgs);

    getContext().getContentResolver().notifyChange(url, null);

    return(count);
  }

  @Override
  public int update(Uri url, ContentValues values, String where,
                    String[] whereArgs) {
    int count=
        db.getWritableDatabase()
          .update(TABLE, values, where, whereArgs);

    getContext().getContentResolver().notifyChange(url, null);

    return(count);
  }

  private boolean isCollectionUri(Uri url) {
    return(MATCHER.match(url) == CONSTANTS);
  }
}