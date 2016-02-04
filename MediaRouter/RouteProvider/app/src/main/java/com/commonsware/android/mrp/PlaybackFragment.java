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
    https://commonsware.com/Android
 */

package com.commonsware.android.mrp;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaControlIntent;
import android.support.v7.media.MediaItemStatus;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.support.v7.media.MediaSessionStatus;
import android.support.v7.media.RemotePlaybackClient;
import android.support.v7.media.RemotePlaybackClient.ItemActionCallback;
import android.support.v7.media.RemotePlaybackClient.SessionActionCallback;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

public class PlaybackFragment extends Fragment {
  private MediaRouteSelector selector=null;
  private MediaRouter router=null;
  private TextView transcript=null;
  private ScrollView scroll=null;
  private RemotePlaybackClient client=null;
  private boolean isPlaying=false;
  private boolean isPaused=false;
  private DemoRouteProvider provider=null;
  private Menu menu=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setRetainInstance(true);
    setHasOptionsMenu(true);
    selector=
        new MediaRouteSelector.Builder().addControlCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)
                                        .build();
  }

  @Override
  public void onAttach(Activity host) {
    super.onAttach(host);

    router=MediaRouter.getInstance(host);
    provider=new DemoRouteProvider(getActivity());
    router.addProvider(provider);
  }

  @Override
  public void onDetach() {
    router.removeProvider(provider);

    super.onDetach();
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    scroll=
        (ScrollView)inflater.inflate(R.layout.activity_main, container,
                                     false);

    transcript=(TextView)scroll.findViewById(R.id.transcript);

    logToTranscript("Started");

    return(scroll);
  }

  @Override
  public void onStart() {
    super.onStart();

    router.addCallback(selector, cb,
                       MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
  }

  @Override
  public void onStop() {
    router.removeCallback(cb);

    super.onStop();
  }

  @Override
  public void onDestroy() {
    disconnect();

    super.onDestroy();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    this.menu=menu;
    inflater.inflate(R.menu.main, menu);

    updateMenu();

    MenuItem item=menu.findItem(R.id.route_provider);
    MediaRouteActionProvider provider=
        (MediaRouteActionProvider)MenuItemCompat.getActionProvider(item);

    provider.setRouteSelector(selector);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.play:
        if (isPlaying && isPaused) {
          resume();
        }
        else {
          play();
        }

        return(true);

      case R.id.stop:
        stop();
        return(true);

      case R.id.pause:
        pause();
        return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  private void updateMenu() {
    if (menu != null) {
      menu.findItem(R.id.stop).setVisible(client != null && isPlaying);
      menu.findItem(R.id.pause).setVisible(client != null && isPlaying
                                               && !isPaused);
      menu.findItem(R.id.play)
          .setVisible(client != null && (!isPlaying || isPaused));
    }
  }

  private void play() {
    logToTranscript(getActivity().getString(R.string.play_requested));

    ItemActionCallback playCB=new ItemActionCallback() {
      @Override
      public void onResult(Bundle data, String sessionId,
                           MediaSessionStatus sessionStatus,
                           String itemId, MediaItemStatus itemStatus) {
        logToTranscript(getActivity().getString(R.string.playing));
        isPlaying=true;
        getActivity().supportInvalidateOptionsMenu();
      }

      @Override
      public void onError(String error, int code, Bundle data) {
        logToTranscript(getActivity().getString(R.string.play_error)
            + error);
      }
    };

    client.play(Uri.parse("http://misc.commonsware.com/ed_hd_512kb.mp4"),
                "video/mp4", null, 0, null, playCB);
  }

  private void pause() {
    logToTranscript(getActivity().getString(R.string.pause_requested));

    PauseCallback pauseCB=new PauseCallback();

    client.pause(null, pauseCB);
    transcript.postDelayed(pauseCB, 1000);
  }

  private void resume() {
    logToTranscript(getActivity().getString(R.string.resume_requested));

    ResumeCallback resumeCB=new ResumeCallback();

    client.resume(null, resumeCB);
    transcript.postDelayed(resumeCB, 1000);
  }

  private void stop() {
    logToTranscript(getActivity().getString(R.string.stop_requested));

    StopCallback stopCB=new StopCallback();

    client.stop(null, stopCB);
    transcript.postDelayed(stopCB, 1000);
  }

  private void logToTranscript(String msg) {
    if (client != null) {
      String sessionId=client.getSessionId();

      if (sessionId != null) {
        msg="(" + sessionId + ") " + msg;
      }
    }

    transcript.setText(transcript.getText().toString() + msg + "\n");
    scroll.fullScroll(View.FOCUS_DOWN);
  }

  private void connect(MediaRouter.RouteInfo route) {
    client=
        new RemotePlaybackClient(getActivity().getApplication(), route);

    if (client.isRemotePlaybackSupported()) {
      logToTranscript(getActivity().getString(R.string.connected));

      if (client.isSessionManagementSupported()) {
        client.startSession(null, new SessionActionCallback() {
          @Override
          public void onResult(Bundle data, String sessionId,
                               MediaSessionStatus sessionStatus) {
            logToTranscript(getActivity().getString(R.string.session_started));
            getActivity().supportInvalidateOptionsMenu();
          }

          @Override
          public void onError(String error, int code, Bundle data) {
            logToTranscript(getActivity().getString(R.string.session_failed));
          }
        });
      }
      else {
        getActivity().supportInvalidateOptionsMenu();
      }
    }
    else {
      logToTranscript(getActivity().getString(R.string.remote_playback_not_supported));
      client=null;
    }
  }

  private void disconnect() {
    isPlaying=false;
    isPaused=false;

    if (client != null) {
      logToTranscript(getActivity().getString(R.string.session_ending));
      EndSessionCallback endCB=new EndSessionCallback();

      if (client.isSessionManagementSupported()) {
        client.endSession(null, endCB);
      }

      transcript.postDelayed(endCB, 1000);
    }
  }

  private MediaRouter.Callback cb=new MediaRouter.Callback() {
    @Override
    public void onRouteSelected(MediaRouter router,
                                MediaRouter.RouteInfo route) {
      logToTranscript(getActivity().getString(R.string.route_selected));
      connect(route);
    }

    @Override
    public void onRouteUnselected(MediaRouter router,
                                  MediaRouter.RouteInfo route) {
      logToTranscript(getActivity().getString(R.string.route_unselected));
      disconnect();
    }
  };

  abstract class RunnableSessionActionCallback extends
      SessionActionCallback implements Runnable {
    abstract protected void doWork();

    private boolean hasRun=false;

    @Override
    public void onResult(Bundle data, String sessionId,
                         MediaSessionStatus sessionStatus) {
      transcript.removeCallbacks(this);
      run();
    }

    @Override
    public void run() {
      if (!hasRun) {
        hasRun=true;
        doWork();
      }
    }
  }

  private class PauseCallback extends RunnableSessionActionCallback {
    @Override
    protected void doWork() {
      isPaused=true;
      getActivity().supportInvalidateOptionsMenu();
      logToTranscript(getActivity().getString(R.string.paused));
    }
  }

  private class ResumeCallback extends RunnableSessionActionCallback {
    @Override
    protected void doWork() {
      isPaused=false;
      getActivity().supportInvalidateOptionsMenu();
      logToTranscript(getActivity().getString(R.string.resumed));
    }
  }

  private class StopCallback extends RunnableSessionActionCallback {
    @Override
    protected void doWork() {
      isPlaying=false;
      isPaused=false;
      getActivity().supportInvalidateOptionsMenu();
      logToTranscript(getActivity().getString(R.string.stopped));
    }
  }

  private class EndSessionCallback extends
      RunnableSessionActionCallback {
    @Override
    protected void doWork() {
      client.release();
      client=null;

      if (getActivity() != null) {
        getActivity().supportInvalidateOptionsMenu();
        logToTranscript(getActivity().getString(R.string.session_ended));
      }
    }
  }
}
