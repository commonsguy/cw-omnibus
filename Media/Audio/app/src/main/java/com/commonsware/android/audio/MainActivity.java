/***
 Copyright (c) 2008-2016 CommonsWare, LLC
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

package com.commonsware.android.audio;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity
  implements MediaPlayer.OnCompletionListener {
  private MenuItem play;
  private MenuItem pause;
  private MenuItem stop;
  private MediaPlayer mp;

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);

    try {
      mp=MediaPlayer.create(this, R.raw.clip);
      mp.setOnCompletionListener(this);
    }
    catch (Exception e) {
      goBlooey(e);
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    if (mp.isPlaying()) {
      mp.stop();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.actions, menu);
    play=menu.findItem(R.id.play);
    pause=menu.findItem(R.id.pause);
    stop=menu.findItem(R.id.stop);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.play:
        play();
        return (true);

      case R.id.pause:
        pause();
        return (true);

      case R.id.stop:
        stop();
        return (true);
    }

    return(super.onOptionsItemSelected(item));
  }

  public void onCompletion(MediaPlayer mp) {
    stop();
  }

  private void play() {
    mp.start();

    play.setVisible(false);
    pause.setVisible(true);
    stop.setVisible(true);
  }

  private void stop() {
    mp.stop();
    pause.setVisible(false);
    stop.setVisible(false);

    findViewById(android.R.id.content).postDelayed(new Runnable() {
      @Override
      public void run() {
        try {
          mp.prepare();
          mp.seekTo(0);
          play.setVisible(true);
        }
        catch (Exception e) {
          goBlooey(e);
        }
      }
    }, 100);
  }

  private void pause() {
    mp.pause();

    play.setVisible(true);
    pause.setVisible(false);
    stop.setVisible(true);
  }

  private void goBlooey(Exception e) {
    Log.e(getClass().getSimpleName(), getString(R.string.msg_error),
      e);
    Toast
      .makeText(this, R.string.msg_error_toast, Toast.LENGTH_LONG)
      .show();
  }
}