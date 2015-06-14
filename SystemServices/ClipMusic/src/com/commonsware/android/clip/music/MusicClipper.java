/***
  Copyright (c) 2008-2012 CommonsWare, LLC
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

package com.commonsware.android.clip.music;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MusicClipper extends Activity {
  private static final int PICK_REQUEST=1337;
  private ClipboardManager clipboard=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    clipboard=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
                                  Intent data) {
    if (requestCode == PICK_REQUEST) {
      if (resultCode == RESULT_OK) {
        ClipData clip=
            ClipData.newUri(getContentResolver(), "Some music",
                            data.getData());

        try {
          clipboard.setPrimaryClip(clip);
        }
        catch (Exception e) {
          Log.e(getClass().getSimpleName(), "Exception clipping Uri", e);
          Toast.makeText(this, "Exception: " + e.getMessage(),
                         Toast.LENGTH_SHORT).show();
        }
      }
    }
  }

  public void pickMusic(View v) {
    Intent i=new Intent(Intent.ACTION_GET_CONTENT);

    i.setType("audio/*");
    startActivityForResult(i, PICK_REQUEST);
  }

  public void playMusic(View v) {
    ClipData clip=clipboard.getPrimaryClip();

    if (clip == null) {
      Toast.makeText(this, "There is no clip!", Toast.LENGTH_LONG)
           .show();
    }
    else {
      ClipData.Item item=clip.getItemAt(0);
      Uri song=item.getUri();

      if (song != null
          && getContentResolver().getType(song).startsWith("audio/")) {
        startActivity(new Intent(Intent.ACTION_VIEW, song));
      }
      else {
        Toast.makeText(this, "There is no song!", Toast.LENGTH_LONG)
             .show();
      }
    }
  }
}