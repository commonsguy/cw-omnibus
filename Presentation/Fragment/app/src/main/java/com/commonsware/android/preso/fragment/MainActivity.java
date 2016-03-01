/***
  Copyright (c) 2013 CommonsWare, LLC
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

package com.commonsware.android.preso.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.media.MediaRouter;
import android.media.MediaRouter.RouteInfo;
import android.media.MediaRouter.SimpleCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
  MediaRouter router=null;
  PresentationFragment preso=null;
  SimpleCallback cb=null;
  View inline=null;
  TextView prose=null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    inline=findViewById(R.id.preso);
    prose=(TextView)findViewById(R.id.prose);
  }

  @Override
  protected void onStart() {
    super.onStart();

    if (cb == null) {
      cb=new RouteCallback();
      router=(MediaRouter)getSystemService(MEDIA_ROUTER_SERVICE);
    }

    handleRoute(router.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_VIDEO));
    router.addCallback(MediaRouter.ROUTE_TYPE_LIVE_VIDEO, cb);
  }

  @Override
  protected void onStop() {
    clearPreso(false);

    if (router != null) {
      router.removeCallback(cb);
    }

    super.onStop();
  }

  private void handleRoute(RouteInfo route) {
    if (route == null) {
      clearPreso(true);
    }
    else {
      Display display=route.getPresentationDisplay();

      if (route.isEnabled() && display != null) {
        if (preso == null) {
          showPreso(route);
          Log.d(getClass().getSimpleName(), "enabled route");
        }
        else if (preso.getDisplay().getDisplayId() != display.getDisplayId()) {
          clearPreso(true);
          showPreso(route);
          Log.d(getClass().getSimpleName(), "switched route");
        }
        else {
          // no-op: should already be set
        }
      }
      else {
        clearPreso(true);
        Log.d(getClass().getSimpleName(), "disabled route");
      }
    }
  }

  private void clearPreso(boolean switchToInline) {
    if (switchToInline) {
      inline.setVisibility(View.VISIBLE);
      prose.setText(R.string.primary);
      getFragmentManager().beginTransaction()
                          .add(R.id.preso, buildPreso(null)).commit();
    }

    if (preso != null) {
      preso.dismiss();
      preso=null;
    }
  }

  private void showPreso(RouteInfo route) {
    if (inline.getVisibility() == View.VISIBLE) {
      inline.setVisibility(View.GONE);
      prose.setText(R.string.secondary);

      Fragment f=getFragmentManager().findFragmentById(R.id.preso);

      getFragmentManager().beginTransaction().remove(f).commit();
    }

    preso=buildPreso(route.getPresentationDisplay());
    preso.show(getFragmentManager(), "preso");
  }

  private PresentationFragment buildPreso(Display display) {
    return(SamplePresentationFragment.newInstance(this, display,
                                                  "https://commonsware.com"));
  }

  private class RouteCallback extends SimpleCallback {
    @Override
    public void onRoutePresentationDisplayChanged(MediaRouter router,
                                                  RouteInfo route) {
      handleRoute(route);
    }
  }
}
