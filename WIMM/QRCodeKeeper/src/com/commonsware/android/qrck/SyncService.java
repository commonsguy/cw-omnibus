/***
  Copyright (c) 2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Advanced Android Development_
    http://commonsware.com/AdvAndroid
 */

package com.commonsware.android.qrck;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.wimm.framework.service.NetworkService;
import org.json.JSONObject;

public class SyncService extends IntentService {
  static final String ACTION_SYNC_STATUS=
      "com.commonsware.android.qrck.SYNC_STATUS";
  static final String KEY_STATUS="KEY_STATUS";
  private static final String TAG="QRCodeKeeper-SyncService";
  private static final String SYNC_URL=
      "http://misc.commonsware.com/codes.json";
  static final String SYNC_LOCAL_FILE="codes.json";
  private static final String KEY_SYNC_TIME="sync_time";
  private static final long SYNC_PERIOD=900000L; // 15 min
  private AtomicBoolean inProgress=new AtomicBoolean(false);
  private NetworkService network=null;
  private SharedPreferences prefs=null;

  static boolean iCanHasData(Context ctxt) {
    File json=new File(ctxt.getFilesDir(), SYNC_LOCAL_FILE);

    return(json.exists());
  }

  static boolean isSyncNeeded(Context ctxt, SharedPreferences prefs) {
    long now=System.currentTimeMillis();
    long lastSyncTime=prefs.getLong(KEY_SYNC_TIME, 0);

    return(lastSyncTime == 0 || (now - lastSyncTime) >= SYNC_PERIOD || !iCanHasData(ctxt));
  }

  public SyncService() {
    super(TAG);
  }

  @Override
  public void onCreate() {
    super.onCreate();

    prefs=PreferenceManager.getDefaultSharedPreferences(this);
    network=new NetworkService(this);

    IntentFilter filter=
        new IntentFilter(NetworkService.ACTION_NETWORK_TAKEDOWN);

    registerReceiver(takedownReceiver, filter);
  }

  @Override
  public void onDestroy() {
    unregisterReceiver(takedownReceiver);

    super.onDestroy();
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void onHandleIntent(Intent intent) {
    ArrayList<String> visited=new ArrayList<String>();

    if (network.isNetworkAvailable()) {
      inProgress.set(true);
      broadcastStatus();

      try {
        URL jsonUrl=new URL(SYNC_URL);
        ReadableByteChannel rbc=
            Channels.newChannel(jsonUrl.openStream());
        FileOutputStream fos=openFileOutput(SYNC_LOCAL_FILE, 0);

        fos.getChannel().transferFrom(rbc, 0, 1 << 16);

        JSONObject json=AppUtils.load(this, SYNC_LOCAL_FILE);

        for (Iterator<String> i=json.keys(); i.hasNext();) {
          String title=i.next();
          String url=json.getString(title);
          Entry entry=new Entry(title, url);
          String filename=entry.getFilename();
          File imageFile=new File(getFilesDir(), filename);

          if (!imageFile.exists()) {
            visited.add(filename);

            URL imageUrl=new URL(jsonUrl, entry.getUrl());

            rbc=Channels.newChannel(imageUrl.openStream());
            fos=new FileOutputStream(imageFile);
            fos.getChannel().transferFrom(rbc, 0, 1 << 16);
          }
        }

        String[] children=getFilesDir().list();

        if (children != null) {
          for (int i=0; i < children.length; i++) {
            String filename=children[i];

            if (!SYNC_LOCAL_FILE.equals(filename)
                && !visited.contains(filename)) {
              new File(getFilesDir(), filename).delete();
            }
          }
        }
      }
      catch (Exception ex) {
        // TODO: let the UI know about this via broadcast
        Log.e(TAG, "Exception syncing", ex);
        AppUtils.cleanup(this);
      }
      finally {
        inProgress.set(false);
        broadcastStatus();
        syncCompleted();
      }
    }
  }

  private void broadcastStatus() {
    Intent i=new Intent(ACTION_SYNC_STATUS);

    i.setPackage(getPackageName());
    i.putExtra(KEY_STATUS, inProgress.get());

    sendBroadcast(i);
  }

  private void syncCompleted() {
    AppUtils.persist(prefs.edit().putLong(KEY_SYNC_TIME,
                                          System.currentTimeMillis()));
  }

  private final BroadcastReceiver takedownReceiver=
      new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
          if (inProgress.get()) {
            network.postponeNetworkTakedown();
          }
        }
      };
}
