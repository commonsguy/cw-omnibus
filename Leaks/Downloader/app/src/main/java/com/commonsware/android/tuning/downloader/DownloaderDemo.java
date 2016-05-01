/***
  Copyright (c) 2010-2011 CommonsWare, LLC
  
  Licensed under the Apache License, Version 2.0 (the "License"); you may
  not use this file except in compliance with the License. You may obtain
  a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package com.commonsware.android.tuning.downloader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

public class DownloaderDemo extends Activity {
  private Handler handler=new Handler() {
    @Override
    public void handleMessage(Message msg) {
      Toast
        .makeText(DownloaderDemo.this, "Download complete!",
                   Toast.LENGTH_LONG)
        .show();
      finish();
    }
  };
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    Toast
      .makeText(this, "Waiting 5 seconds", Toast.LENGTH_SHORT)
      .show();
    
    handler.postDelayed(new Runnable() {
      public void run() {
        doTheDownload();
      }
    }, 5000);
  }
  
  public void doTheDownload() {
    Intent i=new Intent(this, Downloader.class);
    
    i.setData(Uri.parse("https://commonsware.com/Android/excerpt.pdf"));
    i.putExtra(Downloader.EXTRA_MESSENGER, new Messenger(handler));
    
    startService(i);
  }
}
