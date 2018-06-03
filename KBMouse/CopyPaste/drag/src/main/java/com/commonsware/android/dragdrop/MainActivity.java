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

package com.commonsware.android.dragdrop;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Canvas;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.commonsware.cwac.provider.StreamProvider;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MainActivity extends FragmentActivity implements
  View.OnLongClickListener  {
  private static final String AUTHORITY=
    BuildConfig.APPLICATION_ID+".provider";
  private static final Uri PROVIDER=
    Uri.parse("content://"+AUTHORITY);
  private ImageView iv;

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);
    setContentView(R.layout.main);

    iv=findViewById(R.id.asset);

    Picasso.with(this)
      .load("file:///android_asset/FreedomTower-Morning.jpg")
      .fit().centerCrop()
      .into(iv, new Callback() {
        @Override
        public void onSuccess() {
          iv.setOnLongClickListener(MainActivity.this);
        }

        @Override
        public void onError() {
          // TODO
        }
      });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.actions, menu);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId()==R.id.copy) {
      getSystemService(ClipboardManager.class)
        .setPrimaryClip(buildClip());
      Toast
        .makeText(this, R.string.msg_copy, Toast.LENGTH_SHORT)
        .show();

      return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  @Override
  public boolean onLongClick(View view) {
    ClipData clip=buildClip();
    View.DragShadowBuilder shadow=new ThumbDragShadow();

    iv.startDragAndDrop(clip, shadow, null,
      View.DRAG_FLAG_GLOBAL|View.DRAG_FLAG_GLOBAL_URI_READ|
        View.DRAG_FLAG_GLOBAL_PERSISTABLE_URI_PERMISSION);

    return(true);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode==KeyEvent.KEYCODE_SLASH &&
      event.isAltPressed() &&
      event.getRepeatCount()==0 &&
      Build.VERSION.SDK_INT<=Build.VERSION_CODES.M) {
      new ShortcutDialogFragment().show(getSupportFragmentManager(),
        "shortcuts");

      return(true);
    }

    return(super.onKeyDown(keyCode, event));
  }

  private ClipData buildClip() {
    Uri uri=PROVIDER
      .buildUpon()
      .appendEncodedPath(StreamProvider.getUriPrefix(AUTHORITY))
      .appendEncodedPath("assets/FreedomTower-Morning.jpg")
      .build();

    return(ClipData.newRawUri(getString(R.string.msg_photo), uri));
  }

  private class ThumbDragShadow extends View.DragShadowBuilder {
    @Override
    public void onProvideShadowMetrics(Point shadowSize,
                                       Point shadowTouchPoint) {
      shadowSize.set(iv.getWidth()/8, iv.getHeight()/8);
      shadowTouchPoint.set(shadowSize.x/2, shadowSize.y/2);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
      iv.draw(canvas);
    }
  }
}
