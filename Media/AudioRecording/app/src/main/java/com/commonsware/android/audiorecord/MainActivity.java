/***
  Copyright (c) 2012 CommonsWare, LLC
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

package com.commonsware.android.audiorecord;

import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.io.File;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AbstractPermissionActivity implements
    OnCheckedChangeListener, OnErrorListener, OnInfoListener {
  private static final String BASENAME="recording.3gp";
  private MediaRecorder recorder;
  private File output;

  @Override
  protected String[] getDesiredPermissions() {
    return(new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE});
  }

  @Override
  protected void onPermissionDenied() {
    Toast
      .makeText(this, R.string.msg_sorry, Toast.LENGTH_LONG)
      .show();
    finish();
  }

  @Override
  public void onReady(Bundle savedInstanceState) {
    setContentView(R.layout.activity_main);

    ((ToggleButton)findViewById(R.id.record)).setOnCheckedChangeListener(this);
  }

  @Override
  public void onStart() {
    super.onStart();

    recorder=new MediaRecorder();
    recorder.setOnErrorListener(this);
    recorder.setOnInfoListener(this);
  }

  @Override
  public void onStop() {
    recorder.release();
    recorder=null;

    super.onStop();
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView,
                               boolean isChecked) {
    if (isChecked) {
      output=
        new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
          BASENAME);

      recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
      recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
      recorder.setOutputFile(output.getAbsolutePath());

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setAudioEncodingBitRate(160 * 1024);
      }
      else {
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
      }

      recorder.setAudioChannels(2);

      try {
        recorder.prepare();
        recorder.start();
      }
      catch (Exception e) {
        Log.e(getClass().getSimpleName(),
              "Exception in preparing recorder", e);
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
      }
    }
    else {
      try {
        recorder.stop();

        MediaScannerConnection
          .scanFile(this, new String[] {output.getAbsolutePath()}, null, null);
      }
      catch (Exception e) {
        Log.w(getClass().getSimpleName(),
              "Exception in stopping recorder", e);
        // can fail if start() failed for some reason
      }

      recorder.reset();
    }
  }

  @Override
  public void onInfo(MediaRecorder mr, int what, int extra) {
    String msg=getString(R.string.strange);

    switch (what) {
      case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
        msg=getString(R.string.max_duration);
        break;

      case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:
        msg=getString(R.string.max_size);
        break;
    }

    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
  }

  @Override
  public void onError(MediaRecorder mr, int what, int extra) {
    Toast.makeText(this, R.string.strange, Toast.LENGTH_LONG).show();
  }
}
