package com.commonsware.empublite;

import info.juanmendez.android.utils.Trace;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;

@EActivity(R.layout.simplecontent)
public class TestHTTPS extends SherlockFragmentActivity
{

	@Pref DownloadPrefs_ preferences;

	@AfterInject
	public void thenAfterInject()
	{
	

	}

}
