package com.commonsware.empublite;

import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * This shared preference uses Application's default. So we can share
 * it among other services and activities.
 */

@SharedPref( value = SharedPref.Scope.APPLICATION_DEFAULT)
public interface DownloadPrefs
{
	@DefaultString("")
	String updateDir();

	@DefaultString("")
	String previousUpdateDir();

	@DefaultString("")
	String pendingUpdateDir();
}