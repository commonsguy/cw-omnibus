package com.commonsware.empublite;

import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * public static final String PREF_UPDATE_DIR = "updateDir"; public static final
 * String PREF_PREV_UPDATE = "previousUpdateDir";
 * 
 * @author Juan
 * 
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