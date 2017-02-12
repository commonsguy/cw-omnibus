/***
  Copyright (c) 2013-14 CommonsWare, LLC
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

package com.commonsware.android.preso.decktastic;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaControlIntent;
import android.support.v7.media.MediaItemStatus;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.support.v7.media.MediaSessionStatus;
import android.support.v7.media.RemotePlaybackClient;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.commonsware.cwac.preso.PresentationHelper;

public class MainActivity extends AppCompatActivity implements
    PresentationHelper.Listener, TabLayout.OnTabSelectedListener,
    View.OnClickListener, View.OnLongClickListener {
  static final String EXTRA_PRESO_ID="presoId";
  private ViewPager pager=null;
  private SlidePresentationFragment presoFrag=null;
  private SlidesAdapter adapter=null;
  private PresentationHelper helper=null;
  private boolean isFirstRCClick=true;
  private ReverseChronometer rc=null;
  private int durationInSeconds=-1;
  private MediaRouteSelector selector=null;
  private MediaRouter router=null;
  private RemotePlaybackClient client=null;
  private PresoContents preso=null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    preso=
      PresoRoster
        .getInstance()
        .getPresoById(getIntent().getIntExtra(EXTRA_PRESO_ID, 0));

    setContentView(R.layout.activity_main);

    pager=(ViewPager)findViewById(R.id.pager);
    helper=new PresentationHelper(this, this);

    selector=
        new MediaRouteSelector.Builder()
            .addControlCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)
            .build();
    router=MediaRouter.getInstance(this);
    router.addCallback(selector, routeCB,
        MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);

    if (isDirectToTV()) {
      getSupportActionBar().hide();
    }

    setupPager();
  }

  @Override
  public void onResume() {
    super.onResume();
    helper.onResume();
  }

  @Override
  public void onPause() {
    helper.onPause();
    super.onPause();
  }

  @Override
  public void onDestroy() {
    disconnect();
    router.removeCallback(routeCB);
    super.onDestroy();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity_actions, menu);

    rc=(ReverseChronometer)menu.findItem(R.id.countdown)
                               .getActionView();

    rc.setWarningDuration(5 * 60);
    rc.setOnClickListener(this);
    rc.setOnLongClickListener(this);
    rc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
    rc.setTextColor(Color.WHITE);

    if (durationInSeconds>0) {
      rc.setOverallDuration(durationInSeconds);
    }

    MenuItem item=menu.findItem(R.id.route_provider);
    MediaRouteActionProvider provider=
        (MediaRouteActionProvider)MenuItemCompat.getActionProvider(item);

    provider.setRouteSelector(selector);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.present:
        boolean original=item.isChecked();

        item.setChecked(!original);

        if (original) {
          helper.disable();
        }
        else {
          helper.enable();
        }

        break;

      case R.id.first:
        pager.setCurrentItem(0);
        break;

      case R.id.last:
        pager.setCurrentItem(adapter.getCount() - 1);
        break;
    }

    return(super.onOptionsItemSelected(item));
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    switch(keyCode) {
      case KeyEvent.KEYCODE_SPACE:
      case KeyEvent.KEYCODE_DPAD_RIGHT:
      case KeyEvent.KEYCODE_DPAD_DOWN:
      case KeyEvent.KEYCODE_PAGE_DOWN:
      case KeyEvent.KEYCODE_MEDIA_NEXT:
        if (pager.canScrollHorizontally(1)) {
          pager.setCurrentItem(pager.getCurrentItem()+1, true);
        }

        return(true);

      case KeyEvent.KEYCODE_DPAD_LEFT:
      case KeyEvent.KEYCODE_DPAD_UP:
      case KeyEvent.KEYCODE_PAGE_UP:
      case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
        if (pager.canScrollHorizontally(-1)) {
          pager.setCurrentItem(pager.getCurrentItem()-1, true);
        }

        return(true);
    }

    return(super.onKeyDown(keyCode, event));
  }

  @Override
  public void onTabReselected(TabLayout.Tab tab) {
    // unused
  }

  @Override
  public void onTabUnselected(TabLayout.Tab tab) {
    // unused
  }

  @Override
  public void onTabSelected(TabLayout.Tab tab) {
    if (presoFrag != null) {
      presoFrag
        .setSlideContent(adapter.getSlideImageUri(tab.getPosition()));
    }

    if (client!=null) {
      String url=preso.getSlideURL(tab.getPosition());

      client.play(Uri.parse(url), "image/png", null, 0, null, playCB);
    }
  }

  @Override
  public void clearPreso(boolean showInline) {
    if (presoFrag != null) {
      presoFrag.dismiss();
      presoFrag=null;
    }
  }

  @Override
  public void showPreso(Display display) {
    Uri slide=adapter.getSlideImageUri(pager.getCurrentItem());

    presoFrag=
        SlidePresentationFragment.newInstance(this, display, slide);
    presoFrag.show(getSupportFragmentManager(), "presoFrag");
  }

  @Override
  public void onClick(View v) {
    ReverseChronometer rc=(ReverseChronometer)v;

    if (rc.isRunning()) {
      rc.stop();
    }
    else {
      if (isFirstRCClick) {
        isFirstRCClick=false;
        rc.reset();
      }

      rc.run();
    }
  }

  @Override
  public boolean onLongClick(View v) {
    ReverseChronometer rc=(ReverseChronometer)v;

    rc.reset();

    return(true);
  }

  private void setupPager() {
    durationInSeconds=preso.duration * 60;

    if (rc!=null) {
      rc.setOverallDuration(durationInSeconds);
    }

    adapter=new SlidesAdapter(this, preso);
    pager.setAdapter(adapter);

    if (!isDirectToTV()) {
      TabLayout tabs=(TabLayout)findViewById(R.id.tabs);

      tabs.setVisibility(View.VISIBLE);
      tabs.setupWithViewPager(pager);
      tabs.addOnTabSelectedListener(this);
    }
  }

  private void connect(MediaRouter.RouteInfo route) {
    client=
        new RemotePlaybackClient(getApplicationContext(), route);

    if (client.isRemotePlaybackSupported()) {
      String url=preso.getSlideURL(pager.getCurrentItem());

      client.play(Uri.parse(url), "image/png", null, 0, null, playCB);
    }
    else {
      client=null;
    }
  }

  private void disconnect() {
    if (client != null) {
      client.release();
      client=null;
    }

    router.getDefaultRoute().select();
  }

  private boolean isDirectToTV() {
    return(getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEVISION)
        || getPackageManager().hasSystemFeature(PackageManager.FEATURE_LEANBACK));
  }

  private MediaRouter.Callback routeCB=new MediaRouter.Callback() {
    @Override
    public void onRouteSelected(MediaRouter router,
                                MediaRouter.RouteInfo route) {
      connect(route);
    }

    @Override
    public void onRouteUnselected(MediaRouter router,
                                  MediaRouter.RouteInfo route) {
      disconnect();
    }
  };

  RemotePlaybackClient.ItemActionCallback playCB=
    new RemotePlaybackClient.ItemActionCallback() {
    @Override
    public void onResult(Bundle data, String sessionId,
                         MediaSessionStatus sessionStatus,
                         String itemId, MediaItemStatus itemStatus) {
    }

    @Override
    public void onError(String error, int code, Bundle data) {
    }
  };
}