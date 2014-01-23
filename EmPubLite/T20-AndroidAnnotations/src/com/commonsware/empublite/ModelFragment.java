package com.commonsware.empublite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragment;

@EFragment
public class ModelFragment extends SherlockFragment
{
	private BookContents contents = null;
	private boolean contentsTask = false;
	private SharedPreferences prefs = null;
	private boolean prefsTask = false;

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		setRetainInstance(true);
		deliverModel();
	}

	synchronized private void deliverModel()
	{
		if (prefs != null && contents != null)
		{
			((EmPubLiteActivity) getActivity()).setupPager(prefs, contents);
		}
		else
		{
			if (prefs == null && !prefsTask )
			{
				prefsTask = true;
				loadTask();
			}
			else
			if ( contents == null && !contentsTask )
			{
				updateBook();
			}
		}
	}

	@Background
	void updateBook()
	{	
		Context ctxt = getActivity().getApplicationContext();
		BookContents localContents = null;
		Exception e = null;
		
		String updateDir = prefs.getString( DownloadInstallService.PREF_UPDATE_DIR, null);

		try
		{
			StringBuilder buf = new StringBuilder();
			InputStream json = null;

			if (updateDir != null && new File(updateDir).exists())
			{
				json = new FileInputStream(new File(new File(updateDir), "contents.json"));
			}
			else
			{
				json = ctxt.getAssets().open("book/contents.json");
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(json));
			String str;

			while ((str = in.readLine()) != null)
			{
				buf.append(str);
			}

			in.close();

			if (updateDir != null && new File(updateDir).exists())
			{
				localContents = new BookContents(new JSONObject( buf.toString()), new File(updateDir));
			}
			else
			{
				localContents = new BookContents(new JSONObject(
						buf.toString()));
			}
			
			
		}
		catch (Exception exception)
		{
			e = exception;
		}

		String prevUpdateDir = prefs.getString( DownloadInstallService.PREF_PREV_UPDATE, null);

		if (prevUpdateDir != null)
		{
			File toBeDeleted = new File(prevUpdateDir);

			if (toBeDeleted.exists())
			{
				deleteDir(toBeDeleted);
			}
		}
		
		afterUpdateBook(localContents, e );
	}
	
	@UiThread
	void afterUpdateBook( BookContents localContents, Exception e )
	{
		if (e == null)
		{
			contents = localContents;
			contentsTask = true;
			deliverModel();
		}
		else
		{
			Log.e(getClass().getSimpleName(), "Exception loading contents", e);
		}
	}
	
	@Background
	void loadTask()
	{
		Context ctxt = getActivity().getApplicationContext();
		SharedPreferences localPrefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
		localPrefs.getAll();
		
		afterLoadTask(localPrefs);
	}
	
	@UiThread
	void afterLoadTask(SharedPreferences localPrefs)
	{
		prefs = localPrefs;
		prefsTask = false;
		deliverModel();
	}

	@TargetApi(11)
	static public <T> void executeAsyncTask(AsyncTask<T, ?, ?> task, T... params)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
		}
		else
		{
			task.execute(params);
		}
	}

	private static boolean deleteDir(File dir)
	{
		if (dir.exists() && dir.isDirectory())
		{
			File[] children = dir.listFiles();

			for (File child : children)
			{
				boolean ok = deleteDir(child);

				if (!ok)
				{
					return (false);
				}
			}
		}

		return (dir.delete());
	}

}
