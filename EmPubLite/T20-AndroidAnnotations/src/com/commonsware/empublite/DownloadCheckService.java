package com.commonsware.empublite;

import java.io.File;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.HttpsClient;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

@EService
public class DownloadCheckService extends WakefulIntentService
{
	public static final String UPDATE_FILENAME = "book.zip";
	private static final String UPDATE_BASEDIR = "updates";
	private static final String UPDATE_URL = "http://misc.commonsware.com/empublite-update.json";

	@SystemService DownloadManager mgr;
	@HttpsClient HttpClient httpsClient;
	@Pref DownloadPrefs_ prefs;
	
	public DownloadCheckService()
	{
		super("DownloadCheckService");
	}

	@Override
	protected void doWakefulWork(Intent intent)
	{
		try
		{
			HttpGet httpget = new HttpGet(UPDATE_URL);
	        HttpResponse response = httpsClient.execute(httpget);
	        HttpEntity entity = response.getEntity();
			String responseString = EntityUtils.toString(entity, "UTF-8");
			checkDownloadInfo(responseString);
		}
		catch (Exception e)
		{
			Log.e(getClass().getSimpleName(), "Exception retrieving update info", e);
		}
	}

	static File getUpdateBaseDir(Context ctxt)
	{
		return (new File(ctxt.getFilesDir(), UPDATE_BASEDIR));
	}

	private void checkDownloadInfo(String raw) throws JSONException
	{
		JSONObject json = new JSONObject(raw);
		String version = json.names().getString(0);
		File localCopy = new File(getUpdateBaseDir(this), version);

		if (!localCopy.exists())
		{
			prefs.edit().pendingUpdateDir().put(localCopy.getAbsolutePath());

			String url = json.getString(version);
			
			DownloadManager.Request req = new DownloadManager.Request(Uri.parse(url));

			Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_DOWNLOADS).mkdirs();

			req.setAllowedNetworkTypes(
					DownloadManager.Request.NETWORK_WIFI
							| DownloadManager.Request.NETWORK_MOBILE)
					.setAllowedOverRoaming(false)
					.setTitle(getString(R.string.update_title))
					.setDescription(getString(R.string.update_description))
					.setDestinationInExternalPublicDir(
							Environment.DIRECTORY_DOWNLOADS, UPDATE_FILENAME);

			mgr.enqueue(req);
		}
	}
}
