/***
  Copyright (c) 2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
 */

package com.commonsware.android.hcnotify;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

public class SillyService extends IntentService
{
	private static int NOTIFICATION_ID = 1337;

	public SillyService()
	{
		super("SillyService");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		NotificationManager mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this);

		builder.setContent(buildContent(0))
				.setTicker(getText(R.string.ticker), buildTicker())
				.setContentIntent(buildContentIntent())
				.setLargeIcon(buildLargeIcon())
				.setSmallIcon(R.drawable.ic_stat_notif_small_icon)
				.setOngoing(true);

		Notification notif = builder.build();

		for (int i = 0; i < 20; i++)
		{
			/**
			 * viewId  The id of the ProgressBar to change 
				max  The 100% value for the progress bar 
				progress  The current value of the progress bar. 
				indeterminate  True if the progress bar is indeterminate, false if not.  

			 */
			notif.contentView.setProgressBar(android.R.id.progress, 100, i * 5,
					false);
			mgr.notify(NOTIFICATION_ID, notif);

			if (i == 0 )
			{
				notif.tickerText = null;
				//notif.tickerView = null;
			}
			else
			{
				notif.tickerText = String.format("progress ?", i*5);
			}
			
			SystemClock.sleep( i == 0 ? 4000:1000);
		}

		mgr.cancel(NOTIFICATION_ID);
	}

	private Bitmap buildLargeIcon()
	{
		Bitmap raw = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon);

		return (raw);
	}

	private RemoteViews buildTicker()
	{
		RemoteViews ticker = new RemoteViews(this.getPackageName(),
				R.layout.ticker);

		ticker.setTextViewText(R.id.ticker_text, getString(R.string.ticker));

		return (ticker);
	}

	private PendingIntent buildContentIntent()
	{
		Intent i = new Intent(Settings.ACTION_SETTINGS);

		return (PendingIntent.getActivity(this, 0, i, 0));
	}

	private RemoteViews buildContent(int progress)
	{
		RemoteViews content = new RemoteViews(this.getPackageName(),
				R.layout.content);

		return (content);
	}
}