/***
  Copyright (c) 2008-2012 CommonsWare, LLC
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

package com.commonsware.android.ordered;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NoticeReceiver extends BroadcastReceiver
{
	private static final int NOTIFY_ME_ID = 1337;

	@Override
	public void onReceive(Context ctxt, Intent intent)
	{
		Log.e(NoticeService.BROADCAST,  "NoticeReceiver.onReceive");
		
		NotificationManager mgr = (NotificationManager) ctxt
				.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder b = new NotificationCompat.Builder(ctxt);
		PendingIntent pi = PendingIntent.getActivity(ctxt, 0, new Intent(ctxt,
				OrderedActivity.class), 0);

		b.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
				.setWhen(System.currentTimeMillis())
				.setContentTitle(ctxt.getString(R.string.notify_title))
				.setContentText(ctxt.getString(R.string.notify_text))
				.setSmallIcon(android.R.drawable.stat_notify_chat)
				.setTicker(ctxt.getString(R.string.notify_ticker))
				.setContentIntent(pi);

		mgr.notify(NOTIFY_ME_ID, b.build());
	}
}