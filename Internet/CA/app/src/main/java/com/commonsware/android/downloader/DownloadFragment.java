/***
  Copyright (c) 2008-2014 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.downloader;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v13.app.FragmentCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class DownloadFragment extends Fragment implements
    View.OnClickListener {
  private static final String[] PERMS_ALL=
    {WRITE_EXTERNAL_STORAGE};
  private static final int REQUEST_ALL=123;
  private Button b=null;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                           Bundle savedInstanceState) {
    View result=inflater.inflate(R.layout.main, parent, false);

    b=(Button)result.findViewById(R.id.button);
    b.setOnClickListener(this);

    return(result);
  }

  @Override
  public void onClick(View v) {
    if (hasPermission(WRITE_EXTERNAL_STORAGE)) {
      doTheDownload();
    }
    else {
      FragmentCompat.requestPermissions(this, PERMS_ALL, REQUEST_ALL);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String[] permissions,
                                         int[] grantResults) {
    if (requestCode==REQUEST_ALL) {
      if (hasPermission(WRITE_EXTERNAL_STORAGE)) {
        doTheDownload();
      }
    }
  }

  private void doTheDownload() {
    Intent i=new Intent(getActivity(), Downloader.class);

    i.setData(Uri.parse(BuildConfig.URL));
    getActivity().startService(i);
    getActivity().finish();
  }

  private boolean hasPermission(String perm) {
    return(ContextCompat.checkSelfPermission(getActivity(), perm)==
      PackageManager.PERMISSION_GRANTED);
  }
}
