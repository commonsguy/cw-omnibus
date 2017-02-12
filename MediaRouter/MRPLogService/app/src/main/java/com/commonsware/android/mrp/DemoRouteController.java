/***
  Copyright (c) 2014 CommonsWare, LLC
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

package com.commonsware.android.mrp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.media.MediaControlIntent;
import android.support.v7.media.MediaItemStatus;
import android.support.v7.media.MediaRouteProvider;
import android.support.v7.media.MediaRouter.ControlRequestCallback;
import android.util.Log;

class DemoRouteController extends
    MediaRouteProvider.RouteController {
  @Override
  public void onRelease() {
    Log.d(getClass().getSimpleName(), "released");
  }

  @Override
  public void onSelect() {
    Log.d(getClass().getSimpleName(), "selected");
  }

  @Override
  public void onUnselect() {
    Log.d(getClass().getSimpleName(), "unselected");
  }

  @Override
  public boolean onControlRequest(Intent i, ControlRequestCallback cb) {
    if (i.hasCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)) {
      if (MediaControlIntent.ACTION_PLAY.equals(i.getAction())) {
        return(onPlayRequest(i, cb));
      }
      else if (MediaControlIntent.ACTION_PAUSE.equals(i.getAction())) {
        return(onPauseRequest(i, cb));
      }
      else if (MediaControlIntent.ACTION_RESUME.equals(i.getAction())) {
        return(onResumeRequest(i, cb));
      }
      else if (MediaControlIntent.ACTION_STOP.equals(i.getAction())) {
        return(onStopRequest(i, cb));
      }
      else if (MediaControlIntent.ACTION_GET_STATUS.equals(i.getAction())) {
        return(onGetStatusRequest(i, cb));
      }
      else if (MediaControlIntent.ACTION_SEEK.equals(i.getAction())) {
        return(onSeekRequest(i, cb));
      }
    }

    Log.w(getClass().getSimpleName(), "unexpected control request"
        + i.toString());

    return(false);
  }

  private boolean onPlayRequest(Intent i, ControlRequestCallback cb) {
    Log.d(getClass().getSimpleName(), "play: "
        + i.getData().toString());

    MediaItemStatus.Builder statusBuilder=
        new MediaItemStatus.Builder(
                                    MediaItemStatus.PLAYBACK_STATE_PLAYING);

    Bundle b=new Bundle();

    b.putString(MediaControlIntent.EXTRA_SESSION_ID, DemoRouteProvider.DEMO_SESSION_ID);
    b.putString(MediaControlIntent.EXTRA_ITEM_ID, DemoRouteProvider.DEMO_ITEM_ID);
    b.putBundle(MediaControlIntent.EXTRA_ITEM_STATUS,
                statusBuilder.build().asBundle());

    cb.onResult(b);

    return(true);
  }

  private boolean onPauseRequest(Intent i, ControlRequestCallback cb) {
    Log.d(getClass().getSimpleName(), "pause");

    cb.onResult(new Bundle());

    return(true);
  }

  private boolean onResumeRequest(Intent i, ControlRequestCallback cb) {
    Log.d(getClass().getSimpleName(), "resume");

    cb.onResult(new Bundle());

    return(true);
  }

  private boolean onStopRequest(Intent i, ControlRequestCallback cb) {
    Log.d(getClass().getSimpleName(), "stop");

    cb.onResult(new Bundle());

    return(true);
  }

  private boolean onGetStatusRequest(Intent i,
                                     ControlRequestCallback cb) {
    Log.d(getClass().getSimpleName(), "get-status");

    MediaItemStatus.Builder statusBuilder=
        new MediaItemStatus.Builder(
                                    MediaItemStatus.PLAYBACK_STATE_PLAYING);

    Bundle b=new Bundle();

    b.putBundle(MediaControlIntent.EXTRA_ITEM_STATUS,
                statusBuilder.build().asBundle());

    cb.onResult(b);

    return(true);
  }

  private boolean onSeekRequest(Intent i, ControlRequestCallback cb) {
    Log.d(getClass().getSimpleName(), "seek");

    MediaItemStatus.Builder statusBuilder=
        new MediaItemStatus.Builder(
                                    MediaItemStatus.PLAYBACK_STATE_PLAYING);

    Bundle b=new Bundle();

    b.putBundle(MediaControlIntent.EXTRA_ITEM_STATUS,
                statusBuilder.build().asBundle());

    cb.onResult(b);

    return(true);
  }
}