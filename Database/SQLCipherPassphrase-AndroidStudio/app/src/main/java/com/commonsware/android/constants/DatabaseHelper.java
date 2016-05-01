/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.constants;

import android.content.ContentValues;
import android.content.Context;
import android.hardware.SensorManager;
import java.io.File;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
  private static final String DATABASE_NAME="constants-crypt.db";
  private static final String LEGACY_DATABASE_NAME="constants.db";
  private static final String PASSPHRASE="this is a secret";
  private static final int SCHEMA=1;
  static final String TITLE="title";
  static final String VALUE="value";
  static final String TABLE="constants";

  static void encrypt(Context ctxt) {
    SQLiteDatabase.loadLibs(ctxt);

    File dbFile=ctxt.getDatabasePath(DATABASE_NAME);
    File legacyFile=ctxt.getDatabasePath(LEGACY_DATABASE_NAME);

    if (!dbFile.exists() && legacyFile.exists()) {
      SQLiteDatabase db=
          SQLiteDatabase.openOrCreateDatabase(legacyFile, "", null);

      db.rawExecSQL(String.format("ATTACH DATABASE '%s' AS encrypted KEY '%s';",
                                  dbFile.getAbsolutePath(), PASSPHRASE));
      db.rawExecSQL("SELECT sqlcipher_export('encrypted')");
      db.rawExecSQL("DETACH DATABASE encrypted;");

      int version=db.getVersion();

      db.close();

      db=SQLiteDatabase.openOrCreateDatabase(dbFile, PASSPHRASE, null);
      db.setVersion(version);
      db.close();

      legacyFile.delete();
    }
  }

  public DatabaseHelper(Context ctxt) {
    super(ctxt, DATABASE_NAME, null, SCHEMA);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    try {
      db.beginTransaction();
      db.execSQL("CREATE TABLE constants (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, value REAL);");

      ContentValues cv=new ContentValues();

      cv.put(TITLE, "Gravity, Death Star I");
      cv.put(VALUE, SensorManager.GRAVITY_DEATH_STAR_I);
      db.insert("constants", TITLE, cv);

      cv.put(TITLE, "Gravity, Earth");
      cv.put(VALUE, SensorManager.GRAVITY_EARTH);
      db.insert("constants", TITLE, cv);

      cv.put(TITLE, "Gravity, Jupiter");
      cv.put(VALUE, SensorManager.GRAVITY_JUPITER);
      db.insert("constants", TITLE, cv);

      cv.put(TITLE, "Gravity, Mars");
      cv.put(VALUE, SensorManager.GRAVITY_MARS);
      db.insert("constants", TITLE, cv);

      cv.put(TITLE, "Gravity, Mercury");
      cv.put(VALUE, SensorManager.GRAVITY_MERCURY);
      db.insert("constants", TITLE, cv);

      cv.put(TITLE, "Gravity, Moon");
      cv.put(VALUE, SensorManager.GRAVITY_MOON);
      db.insert("constants", TITLE, cv);

      cv.put(TITLE, "Gravity, Neptune");
      cv.put(VALUE, SensorManager.GRAVITY_NEPTUNE);
      db.insert("constants", TITLE, cv);

      cv.put(TITLE, "Gravity, Pluto");
      cv.put(VALUE, SensorManager.GRAVITY_PLUTO);
      db.insert("constants", TITLE, cv);

      cv.put(TITLE, "Gravity, Saturn");
      cv.put(VALUE, SensorManager.GRAVITY_SATURN);
      db.insert("constants", TITLE, cv);

      cv.put(TITLE, "Gravity, Sun");
      cv.put(VALUE, SensorManager.GRAVITY_SUN);
      db.insert("constants", TITLE, cv);

      cv.put(TITLE, "Gravity, The Island");
      cv.put(VALUE, SensorManager.GRAVITY_THE_ISLAND);
      db.insert("constants", TITLE, cv);

      cv.put(TITLE, "Gravity, Uranus");
      cv.put(VALUE, SensorManager.GRAVITY_URANUS);
      db.insert("constants", TITLE, cv);

      cv.put(TITLE, "Gravity, Venus");
      cv.put(VALUE, SensorManager.GRAVITY_VENUS);
      db.insert("constants", TITLE, cv);

      db.setTransactionSuccessful();
    }
    finally {
      db.endTransaction();
    }
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion,
                        int newVersion) {
    throw new RuntimeException("How did we get here?");
  }

  SQLiteDatabase getReadableDatabase() {
    return(super.getReadableDatabase(PASSPHRASE));
  }

  SQLiteDatabase getWritableDatabase() {
    return(super.getWritableDatabase(PASSPHRASE));
  }
}
