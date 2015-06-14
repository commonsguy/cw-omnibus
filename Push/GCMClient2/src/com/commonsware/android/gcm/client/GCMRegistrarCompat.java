/***
  Copyright (c) 2013 CommonsWare, LLC
  Portions Copyright 2012 Google Inc.
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.gcm.client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMRegistrarCompat {
  private static final String TAG="GCMRegistrar";
  private static final String INTENT_FROM_GCM_MESSAGE=
      "com.google.android.c2dm.intent.RECEIVE";
  private static final String PERMISSION_GCM_INTENTS=
      "com.google.android.c2dm.permission.SEND";
  private static final String PROPERTY_REG_ID="regId";
  private static final String PROPERTY_APP_VERSION="appVersion";
  private static final String PREFERENCES="com.google.android.gcm";
  private static final String PROPERTY_ON_SERVER_EXPIRATION_TIME=
      "onServerExpirationTime";
  private static final long REGISTRATION_EXPIRY_TIME_MS=
      1000 * 3600 * 24 * 7;

  public static void checkDevice(Context context) {
    int version=Build.VERSION.SDK_INT;
    if (version < 8) {
      throw new UnsupportedOperationException(
                                              "Device must be at least "
                                                  + "API Level 8 (instead of "
                                                  + version + ")");
    }
  }

  public static void checkManifest(Context context) {
    PackageManager packageManager=context.getPackageManager();
    String packageName=context.getPackageName();
    String permissionName=packageName + ".permission.C2D_MESSAGE";
    // check permission
    try {
      packageManager.getPermissionInfo(permissionName,
                                       PackageManager.GET_PERMISSIONS);
    }
    catch (NameNotFoundException e) {
      throw new IllegalStateException(
                                      "Application does not define permission "
                                          + permissionName);
    }
    // check receivers
    PackageInfo receiversInfo;
    try {
      receiversInfo=
          packageManager.getPackageInfo(packageName,
                                        PackageManager.GET_RECEIVERS);
    }
    catch (NameNotFoundException e) {
      throw new IllegalStateException(
                                      "Could not get receivers for package "
                                          + packageName);
    }
    ActivityInfo[] receivers=receiversInfo.receivers;
    if (receivers == null || receivers.length == 0) {
      throw new IllegalStateException("No receiver for package "
          + packageName);
    }
    if (Log.isLoggable(TAG, Log.VERBOSE)) {
      Log.v(TAG, "number of receivers for " + packageName + ": "
          + receivers.length);
    }
    Set<String> allowedReceivers=new HashSet<String>();
    for (ActivityInfo receiver : receivers) {
      if (PERMISSION_GCM_INTENTS.equals(receiver.permission)) {
        allowedReceivers.add(receiver.name);
      }
    }
    if (allowedReceivers.isEmpty()) {
      throw new IllegalStateException("No receiver allowed to receive "
          + PERMISSION_GCM_INTENTS);
    }
    checkReceiver(context, allowedReceivers, INTENT_FROM_GCM_MESSAGE);
  }

  private static void checkReceiver(Context context,
                                    Set<String> allowedReceivers,
                                    String action) {
    PackageManager pm=context.getPackageManager();
    String packageName=context.getPackageName();
    Intent intent=new Intent(action);
    intent.setPackage(packageName);
    List<ResolveInfo> receivers=
        pm.queryBroadcastReceivers(intent,
                                   PackageManager.GET_INTENT_FILTERS);
    if (receivers.isEmpty()) {
      throw new IllegalStateException("No receivers for action "
          + action);
    }
    if (Log.isLoggable(TAG, Log.VERBOSE)) {
      Log.v(TAG, "Found " + receivers.size() + " receivers for action "
          + action);
    }
    // make sure receivers match
    for (ResolveInfo receiver : receivers) {
      String name=receiver.activityInfo.name;
      if (!allowedReceivers.contains(name)) {
        throw new IllegalStateException("Receiver " + name
            + " is not set with permission " + PERMISSION_GCM_INTENTS);
      }
    }
  }

  public static String getRegistrationId(Context context) {
    final SharedPreferences prefs=getGCMPreferences(context);
    String registrationId=prefs.getString(PROPERTY_REG_ID, "");
    // check if app was updated; if so, it must clear
    // registration id to
    // avoid a race condition if GCM sends a message
    int oldVersion=
        prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
    int newVersion=getAppVersion(context);
    if (oldVersion != Integer.MIN_VALUE && oldVersion != newVersion) {
      Log.v(TAG, "App version changed from " + oldVersion + " to "
          + newVersion + "; resetting registration id");
      clearRegistrationId(context);
      registrationId="";
    }
    else if (isRegistrationExpired(context)) {
      Log.v(TAG, "Registration expired; resetting registration id");
      clearRegistrationId(context);
      registrationId="";
    }
    return registrationId;
  }

  public static String clearRegistrationId(Context context) {
    return setRegistrationId(context, "");
  }

  private static String setRegistrationId(Context context, String regId) {
    final SharedPreferences prefs=getGCMPreferences(context);
    String oldRegistrationId=prefs.getString(PROPERTY_REG_ID, "");
    int appVersion=getAppVersion(context);
    long expirationTime=
        System.currentTimeMillis() + REGISTRATION_EXPIRY_TIME_MS;
    Editor editor=prefs.edit();

    editor.putString(PROPERTY_REG_ID, regId);
    editor.putInt(PROPERTY_APP_VERSION, appVersion);
    editor.putLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, expirationTime);
    editor.commit();

    return oldRegistrationId;
  }

  private static int getAppVersion(Context context) {
    try {
      PackageInfo packageInfo=
          context.getPackageManager()
                 .getPackageInfo(context.getPackageName(), 0);
      return packageInfo.versionCode;
    }
    catch (NameNotFoundException e) {
      // should never happen
      throw new RuntimeException("Coult not get package name: " + e);
    }
  }

  private static boolean isRegistrationExpired(Context context) {
    final SharedPreferences prefs=getGCMPreferences(context);
    // checks if the information is not stale
    long expirationTime=
        prefs.getLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, -1);
    return System.currentTimeMillis() > expirationTime;
  }

  private static SharedPreferences getGCMPreferences(Context context) {
    return context.getSharedPreferences(PREFERENCES,
                                        Context.MODE_PRIVATE);
  }

  private GCMRegistrarCompat() {
    throw new UnsupportedOperationException();
  }

  static public class BaseRegisterTask extends
      AsyncTask<String, Void, String> {
    protected Context context=null;

    BaseRegisterTask(Context context) {
      this.context=context;
    }

    @Override
    protected String doInBackground(String... params) {
      GoogleCloudMessaging gcm=
          GoogleCloudMessaging.getInstance(context);
      String regid=null;

      try {
        regid=gcm.register(params[0]);
        setRegistrationId(context, regid);
        sendRegistrationIdToServer(regid);
      }
      catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      return(regid);
    }

    protected void sendRegistrationIdToServer(String regid) {
      // no-op -- subclasses should override and send
      // registration id to server by some means
    }

    // override this to do something more, note that it
    // is called on a background thread!
    
    protected void onError(IOException e) {
      Log.e(getClass().getSimpleName(),
            "Exception registering for GCM", e);
    }
  }
}
