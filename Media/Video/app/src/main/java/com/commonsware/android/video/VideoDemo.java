/***
  Copyright (c) 2008-2012 CommonsWare, LLC
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

package com.commonsware.android.video;

import java.io.File;
import android.Manifest;
import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Environment;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoDemo extends AbstractPermissionActivity {
  private VideoView video;
  private MediaController ctlr;

  @Override
  protected String[] getDesiredPermissions() {
    return(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE});
  }

  @Override
  protected void onPermissionDenied() {
    Toast
      .makeText(this, R.string.msg_sorry, Toast.LENGTH_LONG)
      .show();
    finish();
  }

  @Override
  public void onReady(Bundle state) {
    getWindow().setFormat(PixelFormat.TRANSLUCENT);
    setContentView(R.layout.main);
  
    File clip=new File(Environment.getExternalStorageDirectory(),
                       "test.mp4");
    
    if (clip.exists()) {
      video=(VideoView)findViewById(R.id.video);
      video.setVideoPath(clip.getAbsolutePath());
      
      ctlr=new MediaController(this);
      ctlr.setMediaPlayer(video);
      video.setMediaController(ctlr);
      video.requestFocus();
      video.start();
    }
  }
}