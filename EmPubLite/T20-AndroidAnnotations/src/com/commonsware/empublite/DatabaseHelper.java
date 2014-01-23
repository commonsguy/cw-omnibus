package com.commonsware.empublite;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

@EBean(scope=Scope.Singleton)
public class DatabaseHelper extends SQLiteOpenHelper
{
	/**
	 * Application context
	 * https://github.com/excilys/androidannotations/wiki/Enhance%20custom%20classes#scopes
	 */
	@RootContext Context context;
	
	private static final String DATABASE_NAME = "empublite.db";
	private static final int SCHEMA_VERSION = 1;

	public DatabaseHelper(Context context)
	{
		super(context, DATABASE_NAME, null, SCHEMA_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		try
		{
			db.beginTransaction();
			db.execSQL("CREATE TABLE notes (position INTEGER PRIMARY KEY, prose TEXT);");
			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		throw new RuntimeException(context.getString(R.string.on_upgrade_error));
	}

	@Background
	void getNoteAsync(int position, NoteListener listener)
	{
		String[] args = { String.valueOf(position) };

		Cursor c = getReadableDatabase().rawQuery("SELECT prose FROM notes WHERE position=?", args);
		c.moveToFirst();

		if (c.isAfterLast())
		{
			c.close();
			setNoteAsync(listener, null );
		}
		else
		{
			String result = c.getString(0);

			c.close();
			setNoteAsync( listener, result );
		}
	}
	
	@UiThread
	void setNoteAsync( NoteListener listener, String prose )
	{
		listener.setNote(prose);
	}


	@Background
	void saveNoteAsync(int position, String prose)
	{
		String[] args = { String.valueOf(position), prose };
		getWritableDatabase().execSQL("INSERT OR REPLACE INTO notes (position, prose) VALUES (?, ?)", args);
	}

	@Background
	void deleteNoteAsync(int position)
	{
		String[] args = { String.valueOf(position) };
		getWritableDatabase().execSQL("DELETE FROM notes WHERE position=?",args);
	}

	interface NoteListener
	{
		void setNote(String note);
	}
}
