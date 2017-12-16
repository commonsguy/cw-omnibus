/***
 Copyright (c) 2014-2016 CommonsWare, LLC
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

package com.commonsware.android.dragdrop;

import android.app.Activity;
import android.content.ClipData;
import android.graphics.Canvas;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.commonsware.cwac.provider.StreamProvider;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MainActivity extends Activity implements
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

    iv=(ImageView)findViewById(R.id.asset);

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
  public boolean onLongClick(View view) {
    Uri uri=PROVIDER
      .buildUpon()
      .appendEncodedPath(StreamProvider.getUriPrefix(AUTHORITY))
      .appendEncodedPath("assets/FreedomTower-Morning.jpg")
      .build();

    ClipData clip=ClipData.newRawUri(getString(R.string.msg_photo), uri);
    View.DragShadowBuilder shadow=new ThumbDragShadow();

    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) {
      iv.startDragAndDrop(clip, shadow, null,
        View.DRAG_FLAG_GLOBAL | View.DRAG_FLAG_GLOBAL_URI_READ |
          View.DRAG_FLAG_GLOBAL_PERSISTABLE_URI_PERMISSION);
    }
    else {
      iv.startDrag(clip, shadow, null, 0);
    }

    return(true);
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
