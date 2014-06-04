/***
  Copyright (c) 2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
	
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
 */

package com.commonsware.android.passwordbox;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.commonsware.cwac.loaderex.SQLiteCursorLoader;

public class DatabaseHelper extends SQLiteOpenHelper {
  private static final String DATABASE_NAME="passwordbox.db";
  private static final int SCHEMA=1;
  static final String ID="_id";
  static final String TITLE="title";
  static final String PASSPHRASE="passphrase";
  static final int SELECT_ALL_ID=0;
  static final int SELECT_ALL_TITLE=1;
  static final int SELECT_ALL_PASSPHRASE=2;
  static final String TABLE="roster";

  public DatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, SCHEMA);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE roster (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, passphrase TEXT);");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion,
                        int newVersion) {
    throw new RuntimeException("How did we get here?");
  }

  SQLiteCursorLoader buildSelectAllLoader(Context ctxt) {
    return(new SQLiteCursorLoader(
                                  ctxt,
                                  this,
                                  "SELECT _id, title, passphrase FROM roster ORDER BY title",
                                  null));
  }
}
