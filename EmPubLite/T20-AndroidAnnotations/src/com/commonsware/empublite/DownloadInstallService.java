package com.commonsware.empublite;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

@EService
public class DownloadInstallService extends WakefulIntentService
{
	public static final String PREF_UPDATE_DIR = "updateDir";
	public static final String PREF_PREV_UPDATE = "previousUpdateDir";
	public static final String ACTION_UPDATE_READY = "com.commonsware.empublite.action.UPDATE_READY";

	@Pref DownloadPrefs_ prefs;

	public DownloadInstallService()
	{
		super("DownloadInstallService");
	}

	@Override
	protected void doWakefulWork(Intent intent)
	{

		String prevUpdateDir = prefs.updateDir().get();
		String pendingUpdateDir = prefs.pendingUpdateDir().get();

		if (pendingUpdateDir != null)
		{
			File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			File update = new File(root, DownloadCheckService.UPDATE_FILENAME);

			try
			{
				unzip(update, new File(pendingUpdateDir));

				prefs.edit().previousUpdateDir().put(prevUpdateDir)
				.updateDir().put(pendingUpdateDir).apply();

			}
			catch (IOException e)
			{
				Log.e(getClass().getSimpleName(), "Exception unzipping update",
						e);
			}

			update.delete();

			Intent i = new Intent(ACTION_UPDATE_READY);
			
			/*
			 * (Usually optional) Set an explicit application package name that limits 
			 * the components this Intent will resolve to. If left to the default value 
			 * of null, all components in all applications will considered. If non-null, 
			 * the Intent can only match the components in the given application package.
			 */
			i.setPackage(getPackageName());
			sendOrderedBroadcast(i, null);
		}
		else
		{
			Log.e(getClass().getSimpleName(), "null pendingUpdateDir");
		}
	}

	private static void unzip(File src, File dest) throws IOException
	{
		InputStream is = new FileInputStream(src);
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
		ZipEntry ze;

		dest.mkdirs();

		while ((ze = zis.getNextEntry()) != null)
		{
			byte[] buffer = new byte[8192];
			int count;
			FileOutputStream fos = new FileOutputStream(new File(dest,
					ze.getName()));
			BufferedOutputStream out = new BufferedOutputStream(fos);

			try
			{
				while ((count = zis.read(buffer)) != -1)
				{
					out.write(buffer, 0, count);
				}

				out.flush();
			}
			finally
			{
				fos.getFD().sync();
				out.close();
			}

			zis.closeEntry();
		}

		zis.close();
	}
}
