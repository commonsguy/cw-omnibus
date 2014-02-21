package com.commonsware.android.schedsvc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		Log.d(getClass().getSimpleName(), "I ran as broadcaster!");

	}

}
