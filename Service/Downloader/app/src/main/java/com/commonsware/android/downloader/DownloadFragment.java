/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  Covered in detail in the book _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.downloader;

import android.support.v4.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v13.app.FragmentCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class DownloadFragment extends Fragment implements
    View.OnClickListener, FragmentCompat.OnRequestPermissionsResultCallback {
  private static final int REQUEST_STORAGE=123;
  private Button b=null;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                           Bundle savedInstanceState) {
    View result=inflater.inflate(R.layout.main, parent, false);

    b=result.findViewById(R.id.button);
    b.setOnClickListener(this);

    return(result);
  }

  @Override
  public void onStart() {
    super.onStart();

    IntentFilter f=new IntentFilter(Downloader.ACTION_COMPLETE);

    LocalBroadcastManager.getInstance(getActivity())
                         .registerReceiver(onEvent, f);
  }

  @Override
  public void onStop() {
    LocalBroadcastManager.getInstance(getActivity())
                         .unregisterReceiver(onEvent);

    super.onStop();
  }

  @Override
  public void onClick(View v) {
    if (hasPermission(WRITE_EXTERNAL_STORAGE)) {
      doTheDownload();
    }
    else {
      requestPermissions(
        new String[] { WRITE_EXTERNAL_STORAGE }, REQUEST_STORAGE);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                         int[] grantResults) {
    if (hasPermission(WRITE_EXTERNAL_STORAGE)) {
      doTheDownload();
    }
  }

  private void doTheDownload() {
    b.setEnabled(false);

    Intent i=new Intent(getActivity(), Downloader.class);

    i.setData(Uri.parse("https://commonsware.com/Android/Android-1_0-CC.pdf"));

    getActivity().startService(i);
  }

  private boolean hasPermission(String perm) {
    return(ContextCompat.checkSelfPermission(getActivity(), perm)==
      PackageManager.PERMISSION_GRANTED);
  }

  private BroadcastReceiver onEvent=new BroadcastReceiver() {
    public void onReceive(Context ctxt, Intent i) {
      b.setEnabled(true);

      Toast.makeText(getActivity(), R.string.download_complete,
                     Toast.LENGTH_LONG).show();
    }
  };
}
