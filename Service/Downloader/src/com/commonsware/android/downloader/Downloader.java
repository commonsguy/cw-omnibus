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
    http://commonsware.com/Android
 */

package com.commonsware.android.downloader;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * IntentService is a base class for Services that handle asynchronous requests (expressed as Intents) on demand. Clients send requests through startService(Intent) calls; the service is started as needed, handles each Intent in turn using a worker thread, and stops itself when it runs out of work.
 * @author Juan
 *
 */
public class Downloader extends IntentService
{
	public static final String ACTION_COMPLETE = "com.commonsware.android.downloader.action.COMPLETE";

	public Downloader()
	{
		super("Downloader");
	}

	/**
	 * All requests are handled on a single worker thread -- they may take as long as necessary (and will not block the application's main loop), but only one request will be processed at a time.
	 */
	@Override
	public void onHandleIntent(Intent i)
	{
		try
		{
			File root = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

			root.mkdirs();

			File output = new File(root, i.getData().getLastPathSegment());

			if (output.exists())
			{
				output.delete();
			}

			URL url = new URL(i.getData().toString());
			HttpURLConnection c = (HttpURLConnection) url.openConnection();

			c.setRequestMethod("GET");
			c.setReadTimeout(15000);
			c.connect();

			FileOutputStream fos = new FileOutputStream(output.getPath());
			BufferedOutputStream out = new BufferedOutputStream(fos);

			try
			{
				InputStream in = c.getInputStream();
				byte[] buffer = new byte[8192];
				int len = 0;

				while ((len = in.read(buffer)) > 0)
				{
					out.write(buffer, 0, len);
				}

				out.flush();
			}
			finally
			{
				fos.getFD().sync();
				out.close();
			}

			sendBroadcast(new Intent(ACTION_COMPLETE));
		}
		catch (IOException e2)
		{
			Log.e(getClass().getName(), "Exception in download", e2);
		}
		//when onHandleIntent() ends, the IntentService will stop itself automatically
	}
}
