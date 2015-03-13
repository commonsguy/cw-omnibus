/***
  Copyright (c) 2014 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
 */

package com.commonsware.android.documents.consumer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

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

    transcript=(TextView)scroll.findViewById(R.id.transcript);

    return(scroll);
  }

  private void logToTranscript(String msg) {
    transcript.setText(transcript.getText().toString() + msg + "\n");
    scroll.fullScroll(View.FOCUS_DOWN);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.actions, menu);

    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.open) {
      open();
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode,
                               Intent resultData) {
    if (resultCode == Activity.RESULT_OK) {
      Uri uri=null;

      if (resultData != null) {
        uri=resultData.getData();
        logToTranscript(uri.toString());

        if (requestCode == REQUEST_OPEN) {
          Cursor c=
              getActivity().getContentResolver().query(uri, null, null,
                                                       null, null);

          if (c != null && c.moveToFirst()) {
            int displayNameColumn=
                c.getColumnIndex(OpenableColumns.DISPLAY_NAME);

            if (displayNameColumn >= 0) {
              logToTranscript("Display name: "
                  + c.getString(displayNameColumn));
            }

            int sizeColumn=c.getColumnIndex(OpenableColumns.SIZE);

            if (sizeColumn < 0 || c.isNull(sizeColumn)) {
              logToTranscript("Size not available");
            }
            else {
              logToTranscript(String.format("Size: %d",
                                            c.getInt(sizeColumn)));
            }
          }
          else {
            logToTranscript("...no metadata available?");
          }
        }
      }
    }
  }

  @TargetApi(Build.VERSION_CODES.KITKAT)
  private void open() {
    Intent i=new Intent().setType("image/*");

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      startActivityForResult(i.setAction(Intent.ACTION_OPEN_DOCUMENT)
                              .addCategory(Intent.CATEGORY_OPENABLE),
                             REQUEST_OPEN);
    }
    else {
      startActivityForResult(i.setAction(Intent.ACTION_GET_CONTENT),
                             REQUEST_GET);
    }
  }
}
