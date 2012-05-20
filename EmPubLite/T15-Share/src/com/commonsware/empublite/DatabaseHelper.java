package com.commonsware.empublite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

public class DatabaseHelper extends SQLiteOpenHelper {
  private static final String DATABASE_NAME="empublite.db";
  private static final int SCHEMA_VERSION=1;
  private static DatabaseHelper singleton=null;
  private Context ctxt=null;

  synchronized static DatabaseHelper getInstance(Context ctxt) {
    if (singleton == null) {
      singleton=new DatabaseHelper(ctxt.getApplicationContext());
    }

    return(singleton);
  }

  public DatabaseHelper(Context ctxt) {
    super(ctxt, DATABASE_NAME, null, SCHEMA_VERSION);
    this.ctxt=ctxt;
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    try {
      db.beginTransaction();
      db.execSQL("CREATE TABLE notes (position INTEGER PRIMARY KEY, prose TEXT);");
      db.setTransactionSuccessful();
    }
    finally {
      db.endTransaction();
    }
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion,
                        int newVersion) {
    throw new RuntimeException(
                               ctxt.getString(R.string.on_upgrade_error));
  }

  void getNoteAsync(int position, NoteListener listener) {
    ModelFragment.executeAsyncTask(new GetNoteTask(listener), position);
  }

  void saveNoteAsync(int position, String note) {
    ModelFragment.executeAsyncTask(new SaveNoteTask(position, note));
  }

  void deleteNoteAsync(int position) {
    ModelFragment.executeAsyncTask(new DeleteNoteTask(), position);
  }

  interface NoteListener {
    void setNote(String note);
  }

  private class GetNoteTask extends AsyncTask<Integer, Void, String> {
    private NoteListener listener=null;

    GetNoteTask(NoteListener listener) {
      this.listener=listener;
    }

    @Override
    protected String doInBackground(Integer... params) {
      String[] args= { params[0].toString() };

      Cursor c=
          getReadableDatabase().rawQuery("SELECT prose FROM notes WHERE position=?",
                                         args);

      c.moveToFirst();

      if (c.isAfterLast()) {
        return(null);
      }

      String result=c.getString(0);

      c.close();

      return(result);
    }

    @Override
    public void onPostExecute(String prose) {
      listener.setNote(prose);
    }
  }

  private class SaveNoteTask extends AsyncTask<Void, Void, Void> {
    private int position;
    private String note=null;

    SaveNoteTask(int position, String note) {
      this.position=position;
      this.note=note;
    }

    @Override
    protected Void doInBackground(Void... params) {
      String[] args= { String.valueOf(position), note };

      getWritableDatabase().execSQL("INSERT OR REPLACE INTO notes (position, prose) VALUES (?, ?)",
                                    args);

      return(null);
    }
  }

  private class DeleteNoteTask extends AsyncTask<Integer, Void, Void> {
    @Override
    protected Void doInBackground(Integer... params) {
      String[] args= { params[0].toString() };

      getWritableDatabase().execSQL("DELETE FROM notes WHERE position=?",
                                    args);

      return(null);
    }
  }
}
