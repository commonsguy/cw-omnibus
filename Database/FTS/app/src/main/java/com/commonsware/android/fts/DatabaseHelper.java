/***
  Copyright (c) 2008-2014 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  Covered in detail in the book _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.fts;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
  public static final String TITLE="title";
  public static final String PROFILE_IMAGE="profileImage";
  public static final String LINK="link";
  private static final String DATABASE_NAME="questions.db";
  private static final int SCHEMA=1;
  private static volatile DatabaseHelper SINGLETON=null;
  private SQLiteDatabase db=null;

  synchronized static DatabaseHelper getInstance(Context ctxt) {
    if (SINGLETON==null) {
      SINGLETON=new DatabaseHelper(ctxt.getApplicationContext());
    }

    return(SINGLETON);
  }

  public DatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, SCHEMA);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE VIRTUAL TABLE questions USING fts4("
                  +"_id INTEGER PRIMARY KEY, title TEXT, "
                  +"link TEXT, profileImage TEXT, creationDate INTEGER, "
                  +"order=DESC);");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion,
                        int newVersion) {
    throw new RuntimeException("How did we get here?");
  }

  void insertQuestions(Context app, List<Item> items) {
    SQLiteDatabase db=getDb(app);

    db.beginTransaction();

    db.delete("questions", null, null);

    try {
      for (Item item : items) {
        Object[] args={ item.id, item.title, item.link,
                        item.owner.profileImage, item.creationDate};

        db.execSQL("INSERT INTO questions (_id, title, "
                      +"link, profileImage, creationDate) "
                      +"VALUES (?, ?, ?, ?, ?)",
                    args);
      }

      db.setTransactionSuccessful();
    }
    finally {
      db.endTransaction();
    }
  }

  Cursor loadQuestions(Context app, String match) {
    SQLiteDatabase db=getDb(app);

    if (TextUtils.isEmpty(match)) {
      return(db.rawQuery("SELECT * FROM questions ORDER BY creationDate DESC",
                          null));
    }

    String[] args={ match };

    return(db.rawQuery("SELECT * FROM questions WHERE title "
                        +"MATCH ? ORDER BY creationDate DESC", args));
  }

  private SQLiteDatabase getDb(Context app) {
    if (db==null) {
      db=getInstance(app).getWritableDatabase();
    }

    return(db);
  }
}
