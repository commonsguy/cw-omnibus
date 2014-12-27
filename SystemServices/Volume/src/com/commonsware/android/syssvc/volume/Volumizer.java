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
    http://commonsware.com/Android
*/

package com.commonsware.android.syssvc.volume;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.SeekBar;

public class Volumizer extends Activity {
  SeekBar alarmBar=null;
  SeekBar musicBar=null;
  SeekBar ringBar=null;
  SeekBar systemBar=null;
  SeekBar voiceBar=null;
  
  AudioManager mgr=null;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    
    mgr=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
    
    alarmBar=(SeekBar)findViewById(R.id.alarm);
    musicBar=(SeekBar)findViewById(R.id.music);
    ringBar=(SeekBar)findViewById(R.id.ring);
    systemBar=(SeekBar)findViewById(R.id.system);
    voiceBar=(SeekBar)findViewById(R.id.voice);
    
    initBar(alarmBar, AudioManager.STREAM_ALARM);
    initBar(musicBar, AudioManager.STREAM_MUSIC);
    initBar(ringBar, AudioManager.STREAM_RING);
    initBar(systemBar, AudioManager.STREAM_SYSTEM);
    initBar(voiceBar, AudioManager.STREAM_VOICE_CALL);
  }
  
  private void initBar(SeekBar bar, final int stream) {
    bar.setMax(mgr.getStreamMaxVolume(stream));
    bar.setProgress(mgr.getStreamVolume(stream));
    
    bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      public void onProgressChanged(SeekBar bar, int progress,
                                    boolean fromUser) {
        mgr.setStreamVolume(stream, progress,
                            AudioManager.FLAG_PLAY_SOUND);
      }
      
      public void onStartTrackingTouch(SeekBar bar) {
        // no-op
      }
      
      public void onStopTrackingTouch(SeekBar bar) {
        // no-op
      }
    });
  }
}