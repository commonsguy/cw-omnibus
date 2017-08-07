/***
 Copyright (c) 2017 CommonsWare, LLC
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

package com.commonsware.android.recyclerview.videolist;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.app.RemoteAction;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Rational;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;

public class VideoPlayerActivity extends Activity
  implements View.OnClickListener {
  private static final int REQUEST_PAUSE=1337;
  private static final int REQUEST_PLAY=REQUEST_PAUSE+1;
  private static final String EXTRA_REQUEST="requestCode";
  private static final String STATE_POSITION="position";
  private VideoView video;
  private MediaController ctlr;
  private View fab;
  private int lastPosition;
  private Intent current;
  private Rational aspectRatio;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.player);
    video=(VideoView)findViewById(R.id.player);

    ctlr=new MediaController(this);
    ctlr.setMediaPlayer(video);
    video.setMediaController(ctlr);

    fab=findViewById(R.id.pip);
    fab.setOnClickListener(this);

    current=getIntent();
    play();
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);

    if (!intent.getData().equals(current.getData())) {
      current=intent;
      play();
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putInt(STATE_POSITION, video.getCurrentPosition());
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);

    lastPosition=savedInstanceState.getInt(STATE_POSITION);
    video.seekTo(lastPosition);
  }

  @Override
  protected void onRestart() {
    super.onRestart();

    video.seekTo(lastPosition);
  }

  @Override
  protected void onStart() {
    super.onStart();

    EventBus.getDefault().register(this);
  }

  @Override
  protected void onStop() {
    video.pause();
    updateActions();
    lastPosition=video.getCurrentPosition();
    EventBus.getDefault().unregister(this);

    super.onStop();
  }

  @Override
  public void onClick(View view) {
    aspectRatio=new Rational(video.getWidth(), video.getHeight());
    enterPictureInPictureMode(updateActions());
  }

  @Override
  public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode,
                                            Configuration cfg) {
    super.onPictureInPictureModeChanged(isInPictureInPictureMode, cfg);

    fab.setVisibility(isInPictureInPictureMode ? View.GONE : View.VISIBLE);
  }

  @Subscribe(threadMode =ThreadMode.MAIN)
  public void onReceive(Intent intent) {
    int requestCode=intent.getIntExtra(EXTRA_REQUEST, -1);

    if (requestCode==REQUEST_PAUSE) {
      video.pause();
    }
    else if (requestCode==REQUEST_PLAY) {
      video.start();
    }

    setPictureInPictureParams(updateActions());
  }

  private void play() {
    video.stopPlayback();
    video.setVideoURI(current.getData());
    video.start();
  }

  private PictureInPictureParams updateActions() {
    ArrayList<RemoteAction> actions=new ArrayList<>();

    if (video.isPlaying()) {
      actions.add(buildRemoteAction(REQUEST_PAUSE,
        R.drawable.ic_pause_white_24dp, R.string.pause, R.string.pause_desc));
    }
    else {
      actions.add(buildRemoteAction(REQUEST_PLAY,
        R.drawable.ic_play_arrow_white_24dp, R.string.play, R.string.play_desc));
    }

    return(new PictureInPictureParams.Builder()
      .setAspectRatio(aspectRatio)
      .setActions(actions)
      .build());
  }

  private RemoteAction buildRemoteAction(int requestCode, int iconId,
                                         int titleId, int descId) {
    Intent i=new Intent(this, RemoteActionReceiver.class)
      .putExtra(EXTRA_REQUEST, requestCode);
    PendingIntent pi=PendingIntent.getBroadcast(this, requestCode, i, 0);
    Icon icon=Icon.createWithResource(this, iconId);
    String title=getString(titleId);
    String desc=getString(descId);

    return(new RemoteAction(icon, title, desc, pi));
  }
}
