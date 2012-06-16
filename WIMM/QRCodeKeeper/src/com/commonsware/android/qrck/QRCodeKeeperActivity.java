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
import java.util.ArrayList;
import java.util.Iterator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.wimm.framework.app.AlertDialog;
import com.wimm.framework.app.LauncherActivity;
import com.wimm.framework.service.NetworkService;
import com.wimm.framework.view.AdapterViewTray;
import org.json.JSONObject;

public class QRCodeKeeperActivity extends LauncherActivity implements
    JSONLoadTask.Listener, DialogInterface.OnClickListener {
  private ArrayList<Entry> entries=new ArrayList<Entry>();
  private AdapterViewTray tray=null;
  private SharedPreferences prefs=null;
  private boolean noCanDo=false;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    prefs=PreferenceManager.getDefaultSharedPreferences(this);

    tray=(AdapterViewTray)findViewById(android.R.id.list);
    tray.setCanLoop(true);

    NetworkService network=new NetworkService(this);

    if (SyncService.iCanHasData(this)) {
      loadEntries();
    }
    else {
      noCanDo=true;
    }

    if (SyncService.isSyncNeeded(this, prefs)) {
      if (network.isNetworkAvailable()) {
        startService(new Intent(this, SyncService.class));
      }
      else {
        network.requestNetworkConnection();
      }
    }

    if (noCanDo) {
      AlertDialog dlg=new AlertDialog(this);

      dlg.setButton(getText(R.string.close), this);
      dlg.setMessage(getText(R.string.no_can_do));
      dlg.show();
    }
  }

  @Override
  public void onResume() {
    super.onResume();

    if (!noCanDo) {
      registerReceiver(statusReceiver,
                       new IntentFilter(SyncService.ACTION_SYNC_STATUS));
    }
  }

  @Override
  public void onPause() {
    if (!noCanDo) {
      unregisterReceiver(statusReceiver);
    }

    super.onPause();
  }

  @SuppressWarnings("unchecked")
  @Override
  public void handleResult(JSONObject json) {
    entries.clear();

    try {
      for (Iterator<String> i=json.keys(); i.hasNext();) {
        String title=i.next();
        String url=json.getString(title);

        entries.add(new Entry(title, url));
      }

      tray.setAdapter(new EntryAdapter());
    }
    catch (Exception ex) {
      Log.e("QRCodeKeeper", "Exception interpreting JSON", ex);
      goBlooey(ex);
    }
  }

  @Override
  public void handleError(Exception ex) {
    Log.e("QRCodeKeeper", "Exception loading JSON", ex);
    goBlooey(ex);
  }

  @Override
  public void onClick(DialogInterface dialog, int which) {
    finish();
  }

  private void loadEntries() {
    new JSONLoadTask(this, this).execute(SyncService.SYNC_LOCAL_FILE);
  }

  private void goBlooey(Exception ex) {
    AlertDialog dlg=new AlertDialog(this);

    dlg.setButton(getText(R.string.close), this);
    dlg.setMessage(ex.getMessage());
    dlg.show();
  }

  private final BroadcastReceiver statusReceiver=
      new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
          boolean isRunning=
              intent.getBooleanExtra(SyncService.KEY_STATUS, false);

          if (!isRunning) {
            loadEntries();
          }
        }
      };

  class EntryAdapter extends ArrayAdapter<Entry> {
    public EntryAdapter() {
      super(QRCodeKeeperActivity.this, R.layout.entry, R.id.title,
            entries);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View row=super.getView(position, convertView, parent);
      ImageView qrCode=(ImageView)row.findViewById(R.id.qrCode);

      try {
        File image=
            new File(getFilesDir(), getItem(position).getFilename());

        new ImageLoadTask(qrCode).execute(image.getAbsolutePath());
      }
      catch (Exception ex) {
        Log.e("QRCodeKeeper", "Exception interpreting JSON", ex);
        goBlooey(ex);
      }

      return(row);
    }
  }
}