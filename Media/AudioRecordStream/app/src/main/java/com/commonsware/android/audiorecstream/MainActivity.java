/***
  Copyright (c) 2012 CommonsWare, LLC
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

package com.commonsware.android.audiorecstream;

import android.app.Activity;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.ParcelFileDescriptor.AutoCloseInputStream;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity implements
    OnCheckedChangeListener, OnErrorListener, OnInfoListener {
  private static final String BASENAME="recording-stream.amr";
  private MediaRecorder recorder=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ((ToggleButton)findViewById(R.id.record)).setOnCheckedChangeListener(this);
  }

  @Override
  public void onResume() {
    super.onResume();

    recorder=new MediaRecorder();
    recorder.setOnErrorListener(this);
    recorder.setOnInfoListener(this);
  }

  @Override
  public void onPause() {
    recorder.release();
    recorder=null;

    super.onPause();
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView,
                               boolean isChecked) {
    File recording=getOutputFile();

    if (isChecked) {
      if (recording.exists()) {
        recording.delete();
      }

      recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
      recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
      recorder.setOutputFile(getStreamFd());
      recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
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
      }
      catch (Exception e) {
        Log.w(getClass().getSimpleName(),
              "Exception in stopping recorder", e);
        // can fail if start() failed for some reason
      }

      recorder.reset();

      if (recording.exists() && recording.length() > 0) {
        Toast.makeText(this, R.string.recording_successful,
                       Toast.LENGTH_LONG).show();
      }
      else {
        Toast.makeText(this, R.string.recording_failed,
                       Toast.LENGTH_LONG).show();
      }
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

  private FileDescriptor getStreamFd() {
    ParcelFileDescriptor[] pipe=null;

    try {
      pipe=ParcelFileDescriptor.createPipe();

      new TransferThread(new AutoCloseInputStream(pipe[0]),
                         new FileOutputStream(getOutputFile())).start();
    }
    catch (IOException e) {
      Log.e(getClass().getSimpleName(), "Exception opening pipe", e);
    }

    return(pipe[1].getFileDescriptor());
  }

  private File getOutputFile() {
    return(new File(getExternalFilesDir(null), BASENAME));
  }

  static class TransferThread extends Thread {
    InputStream in;
    FileOutputStream out;

    TransferThread(InputStream in, FileOutputStream out) {
      this.in=in;
      this.out=out;
    }

    @Override
    public void run() {
      byte[] buf=new byte[8192];
      int len;

      try {
        while ((len=in.read(buf)) >= 0) {
          out.write(buf, 0, len);
        }

        in.close();

        out.flush();
        out.getFD().sync();
        out.close();
      }
      catch (IOException e) {
        Log.e(getClass().getSimpleName(),
              "Exception transferring file", e);
      }
    }
  }
}
