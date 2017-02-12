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

import android.content.Context;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.media.AudioManager;
import android.support.v7.media.MediaControlIntent;
import android.support.v7.media.MediaRouteDescriptor;
import android.support.v7.media.MediaRouteDiscoveryRequest;
import android.support.v7.media.MediaRouteProvider;
import android.support.v7.media.MediaRouteProviderDescriptor;
import android.support.v7.media.MediaRouter;

public class DemoRouteProvider extends MediaRouteProvider {
  private static final String DEMO_ROUTE_ID="demo-route";
  static final String DEMO_SESSION_ID="demo-session";
  static final String DEMO_ITEM_ID="demo-item";
  private static final IntentFilter ifPlay=new IntentFilter();
  private static final IntentFilter ifControl=new IntentFilter();

  static {
    ifPlay.addCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK);
    ifPlay.addAction(MediaControlIntent.ACTION_PLAY);
    ifPlay.addDataScheme("http");
    ifPlay.addDataScheme("https");
    ifPlay.addDataScheme("rtsp");

    try {
      ifPlay.addDataType("video/*");
    }
    catch (MalformedMimeTypeException e) {
      throw new RuntimeException("Exception setting MIME type", e);
    }

    ifControl.addCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK);
    ifControl.addAction(MediaControlIntent.ACTION_PAUSE);
    ifControl.addAction(MediaControlIntent.ACTION_RESUME);
    ifControl.addAction(MediaControlIntent.ACTION_STOP);
    ifControl.addAction(MediaControlIntent.ACTION_GET_STATUS);
    ifControl.addAction(MediaControlIntent.ACTION_SEEK);
  }

  public DemoRouteProvider(Context ctxt) {
    super(ctxt);

    handleDiscovery();
  }

  @Override
  public RouteController onCreateRouteController(String routeId) {
    return(new DemoRouteController());
  }

  @Override
  public void onDiscoveryRequestChanged(MediaRouteDiscoveryRequest disco) {
    handleDiscovery();
  }

  private void handleDiscovery() {
    MediaRouteDescriptor.Builder mrdBuilder=
        new MediaRouteDescriptor.Builder(DEMO_ROUTE_ID, "Demo Route");

    mrdBuilder.setDescription("The description of a demo route")
              .addControlFilter(ifPlay)
              .addControlFilter(ifControl)
              .setPlaybackStream(AudioManager.STREAM_MUSIC)
              .setPlaybackType(MediaRouter.RouteInfo.PLAYBACK_TYPE_REMOTE)
              .setVolumeHandling(MediaRouter.RouteInfo.PLAYBACK_VOLUME_FIXED);

    MediaRouteProviderDescriptor.Builder mrpdBuilder=
        new MediaRouteProviderDescriptor.Builder();

    mrpdBuilder.addRoute(mrdBuilder.build());

    setDescriptor(mrpdBuilder.build());
  }
}