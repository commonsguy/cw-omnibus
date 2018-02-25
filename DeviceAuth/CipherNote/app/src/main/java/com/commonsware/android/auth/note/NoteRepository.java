/***
 Copyright (c) 2018 CommonsWare, LLC
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

package com.commonsware.android.auth.note;

import android.content.ContentValues;
import android.content.Context;
import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import java.util.Arrays;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

class NoteRepository {
  private static final Note EMPTY=new Note(-1, null);
  private static volatile NoteRepository INSTANCE;
  private SQLiteDatabase db;

  private synchronized static NoteRepository init(Context ctxt, char[] passphrase) {
    if (INSTANCE==null) {
      INSTANCE=new NoteRepository(ctxt.getApplicationContext(), passphrase);
    }

    return INSTANCE;
  }

  private synchronized static NoteRepository get() {
    return INSTANCE;
  }

  private NoteRepository(Context ctxt, char[] passphrase) {
    DatabaseHelper helper=new DatabaseHelper(ctxt);

    db=helper.getWritableDatabase(passphrase);
  }

  static Observable<Note> load(Context ctxt, char[] passphrase) {
    return Observable.create(new LoadObservable(ctxt, passphrase));
  }

  static Note save(Note note, String content) {
    ContentValues cv=new ContentValues(1);

    cv.put("content", content);

    if (note==EMPTY) {
      long id=NoteRepository.get().db.insert("note", null, cv);

      return new Note(id, content);
    }
    else {
      NoteRepository.get().db.update("note", cv, "_id=?",
        new String[]{String.valueOf(note.id)});

      return new Note(note.id, content);
    }
  }

  private static class LoadObservable implements ObservableOnSubscribe<Note> {
    private final Context app;
    private final char[] passphrase;

    LoadObservable(Context ctxt, char[] passphrase) {
      this.app=ctxt.getApplicationContext();
      this.passphrase=passphrase;
    }

    @Override
    public void subscribe(ObservableEmitter<Note> e) throws Exception {
      Cursor c=NoteRepository.init(app, passphrase).db
        .rawQuery("SELECT _id, content FROM note", null);

      if (c.isAfterLast()) {
        e.onNext(EMPTY);
      }
      else {
        c.moveToFirst();
        e.onNext(new Note(c.getLong(0), c.getString(1)));
        Arrays.fill(passphrase, '\u0000');
      }

      c.close();
    }
  }

  static class Note {
    final long id;
    final String content;

    private Note(long id, String content) {
      this.id=id;
      this.content=content;
    }
  }
}
