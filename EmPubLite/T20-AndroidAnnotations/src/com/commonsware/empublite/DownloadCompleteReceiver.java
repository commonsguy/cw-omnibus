package com.commonsware.empublite;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class DownloadCompleteReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context ctxt, Intent i)
	{
		File update = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
				DownloadCheckService.UPDATE_FILENAME);
		
		if (update.exists())
		{
			WakefulIntentService.sendWakefulWork(ctxt,
					DownloadInstallService_.class);
		}
	}
}
