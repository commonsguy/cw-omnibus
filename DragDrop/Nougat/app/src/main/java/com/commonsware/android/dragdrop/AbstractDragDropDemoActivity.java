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
import android.content.ClipDescription;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.commonsware.cwac.provider.StreamProvider;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

abstract public class AbstractDragDropDemoActivity extends Activity implements
  View.OnLongClickListener {
  protected static final String STATE_IMAGE_URI=
    BuildConfig.APPLICATION_ID+".IMAGE_URI";
  protected Uri imageUri;
  protected ImageView image;
  protected SparseIntArray originalColors=new SparseIntArray();
  protected int dropTargetReadyColor;

  abstract int getDropFrameContentId();
  abstract int getOwnMenuId();

  private static final String AUTHORITY=
    BuildConfig.APPLICATION_ID+".provider";
  private static final Uri PROVIDER=
    Uri.parse("content://"+AUTHORITY);
  private ImageView iv;

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);
    setContentView(R.layout.main);

    originalColors.put(R.id.outer_container,
      getResources().getColor(R.color.outer_normal));
    originalColors.put(R.id.inner_container,
      getResources().getColor(R.color.inner_normal));
    originalColors.put(R.id.thumbnail_large,
      getResources().getColor(R.color.image_normal));
    dropTargetReadyColor=
      getResources().getColor(R.color.drop_target_ready);

    ViewGroup dropFrame=(ViewGroup)findViewById(R.id.drop_frame);

    getLayoutInflater().inflate(getDropFrameContentId(), dropFrame,
      true);

    iv=(ImageView)findViewById(R.id.asset);

    Picasso.with(this)
      .load("file:///android_asset/FreedomTower-Morning.jpg")
      .fit().centerCrop()
      .into(iv, new Callback() {
        @Override
        public void onSuccess() {
          iv.setOnLongClickListener(AbstractDragDropDemoActivity.this);
        }

        @Override
        public void onError() {
          // TODO
        }
      });

    if (state!=null) {
      imageUri=state.getParcelable(STATE_IMAGE_URI);

      if (imageUri!=null) {
        showThumbnail();
      }
    }
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
      iv.startDragAndDrop(clip, shadow, Boolean.TRUE, 0);
    }
    else {
      iv.startDrag(clip, shadow, Boolean.TRUE, 0);
    }

    return(true);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    new MenuInflater(this).inflate(R.menu.actions, menu);

    MenuItem item=menu.findItem(getOwnMenuId());

    if (item!=null) {
      item.setVisible(false);
    }

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Intent i=null;

    switch(item.getItemId()) {
      case R.id.main:
        i=new Intent(this, MainActivity.class);
        break;

      case R.id.bug:
        i=new Intent(this, BugActivity.class);
        break;

      case R.id.inclusive:
        i=new Intent(this, InclusiveActivity.class);
        break;
    }

    if (i!=null) {
      startActivity(i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
      return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putParcelable(STATE_IMAGE_URI, imageUri);
  }

  protected void showThumbnail() {
    if (image!=null) {
      Picasso.with(this)
        .load(imageUri)
        .fit().centerCrop()
        .placeholder(
          R.drawable.ic_photo_size_select_actual_black_24dp)
        .error(R.drawable.ic_error_black_24dp)
        .into(image);
    }
  }

  public boolean onDrag(View v, DragEvent event) {
    boolean result=true;

    switch (event.getAction()) {
      case DragEvent.ACTION_DRAG_STARTED:
        if (event.getLocalState()==null || !event
          .getClipDescription()
          .hasMimeType(ClipDescription.MIMETYPE_TEXT_URILIST)) {
          result=false;
        }

        break;

      case DragEvent.ACTION_DRAG_ENTERED:
        v.setBackgroundColor(dropTargetReadyColor);
        break;

      case DragEvent.ACTION_DRAG_EXITED:
        v.setBackgroundColor(originalColors.get(v.getId()));
        break;

      case DragEvent.ACTION_DRAG_ENDED:
        v.setBackgroundColor(originalColors.get(v.getId()));
        break;

      case DragEvent.ACTION_DROP:
        v.setBackgroundColor(originalColors.get(v.getId()));

        if (v instanceof ImageView) {
          if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) {
            requestDragAndDropPermissions(event);
          }

          ClipData.Item clip=event.getClipData().getItemAt(0);

          imageUri=clip.getUri();
          showThumbnail();
        }

        break;
    }

    return(result);
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
