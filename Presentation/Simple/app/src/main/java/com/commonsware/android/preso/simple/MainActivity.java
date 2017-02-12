/***
  Copyright (c) 2013 CommonsWare, LLC
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

package com.commonsware.android.preso.simple;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Presentation;
import android.content.Context;
import android.media.MediaRouter;
import android.media.MediaRouter.RouteInfo;
import android.media.MediaRouter.SimpleCallback;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.webkit.WebView;

public class MainActivity extends Activity {
  MediaRouter router=null;
  Presentation preso=null;
  SimpleCallback cb=null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
  @Override
  protected void onStart() {
    super.onStart();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      if (cb==null) {
        cb=new RouteCallback();
        router=(MediaRouter)getSystemService(MEDIA_ROUTER_SERVICE);
      }

      handleRoute(router.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_VIDEO));
      router.addCallback(MediaRouter.ROUTE_TYPE_LIVE_VIDEO, cb);
    }
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
  @Override
  protected void onStop() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      clearPreso();

      if (router != null) {
        router.removeCallback(cb);
      }
    }

    super.onStop();
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
  private void handleRoute(RouteInfo route) {
    if (route == null) {
      clearPreso();
    }
    else {
      Display display=route.getPresentationDisplay();

      if (route.isEnabled() && display != null) {
        if (preso == null) {
          showPreso(route);
          Log.d(getClass().getSimpleName(), "enabled route");
        }
        else if (preso.getDisplay().getDisplayId() != display.getDisplayId()) {
          clearPreso();
          showPreso(route);
          Log.d(getClass().getSimpleName(), "switched route");
        }
        else {
          // no-op: should already be set
        }
      }
      else {
        clearPreso();
        Log.d(getClass().getSimpleName(), "disabled route");
      }
    }
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
  private void clearPreso() {
    if (preso != null) {
      preso.dismiss();
      preso=null;
    }
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
  private void showPreso(RouteInfo route) {
    preso=new SimplePresentation(this, route.getPresentationDisplay());
    preso.show();
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  private class RouteCallback extends SimpleCallback {
    @Override
    public void onRoutePresentationDisplayChanged(MediaRouter router,
                                                  RouteInfo route) {
      handleRoute(route);
    }
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
  private class SimplePresentation extends Presentation {
    SimplePresentation(Context ctxt, Display display) {
      super(ctxt, display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      WebView wv=new WebView(getContext());

      wv.loadUrl("https://commonsware.com");

      setContentView(wv);
    }
  }
}
