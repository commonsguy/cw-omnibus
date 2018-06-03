/***
  Copyright (c) 2014-2016 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  Covered in detail in the book _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.documents.consumer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ConsumerFragment extends Fragment {
  private static final int REQUEST_OPEN=1337;
  private static final int REQUEST_GET=REQUEST_OPEN + 1;
  private TextView transcript=null;
  private ScrollView scroll=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    scroll=
        (ScrollView)inflater.inflate(R.layout.activity_main, container,
                                     false);

    transcript=scroll.findViewById(R.id.transcript);

    return(scroll);
  }

  @Override
  public void onStart() {
    super.onStart();

    EventBus.getDefault().register(this);
  }

  @Override
  public void onStop() {
    EventBus.getDefault().unregister(this);

    super.onStop();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.actions, menu);

    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT) {
      menu.findItem(R.id.open).setEnabled(true);
    }

    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId()==R.id.open) {
      open();
    }
    else if (item.getItemId()==R.id.get) {
      get();
    }

    return(super.onOptionsItemSelected(item));
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode,
                               Intent resultData) {
    if (resultCode==Activity.RESULT_OK) {
      getActivity()
        .startService(new Intent(getActivity(), DurablizerService.class)
          .setData(resultData.getData())
          .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION));
    }
  }

  @Subscribe(threadMode=ThreadMode.MAIN)
  public void onContentReady(DurablizerService.ContentReadyEvent event) {
    logToTranscript(event.docFile.getUri().toString());
    logToTranscript("Display name: "+event.docFile.getName());
    logToTranscript("Size: "+Long.toString(event.docFile.length()));
  }

  @TargetApi(Build.VERSION_CODES.KITKAT)
  private void open() {
    Intent i=
      new Intent()
        .setType("*/*")
        .setAction(Intent.ACTION_OPEN_DOCUMENT)
        .addCategory(Intent.CATEGORY_OPENABLE);

    startActivityForResult(i, REQUEST_OPEN);
  }

  private void get() {
    Intent i=
      new Intent()
        .setType("image/png")
        .setAction(Intent.ACTION_GET_CONTENT)
        .addCategory(Intent.CATEGORY_OPENABLE);

    startActivityForResult(i, REQUEST_GET);
  }

  private void logToTranscript(String msg) {
    transcript.setText(transcript.getText().toString() + msg + "\n");
    scroll.fullScroll(View.FOCUS_DOWN);
  }
}
