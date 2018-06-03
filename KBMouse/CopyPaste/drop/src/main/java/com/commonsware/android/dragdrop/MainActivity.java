/**
 * Copyright (c) 2008-2016 CommonsWare, LLC
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.commonsware.android.dragdrop;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;

public class MainActivity extends FragmentActivity implements
  View.OnDragListener {
  private static final String STATE_IMAGE_URI=
    BuildConfig.APPLICATION_ID+".IMAGE_URI";
  private Uri imageUri;
  private ImageView image;

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);
    setContentView(R.layout.main);

    image=findViewById(R.id.thumbnail_large);
    image.setOnDragListener(this);

    if (state!=null) {
      imageUri=state.getParcelable(STATE_IMAGE_URI);

      if (imageUri!=null) {
        showThumbnail();
      }
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putParcelable(STATE_IMAGE_URI, imageUri);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.actions, menu);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId()==R.id.paste) {
      boolean handled=false;

      ClipData clip=
        getSystemService(ClipboardManager.class)
          .getPrimaryClip();

      if (clip!=null) {
        ClipData.Item clipItem=clip.getItemAt(0);

        if (clipItem!=null) {
          imageUri=clipItem.getUri();

          if (imageUri!=null) {
            showThumbnail();
            handled=true;
          }
        }
      }

      if (!handled) {
        Toast
          .makeText(this, "Could not paste an image!", Toast.LENGTH_LONG)
          .show();
      }

      return(handled);
    }

    return(super.onOptionsItemSelected(item));
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

  @Override
  public boolean onDrag(View v, DragEvent event) {
    boolean result=true;

    switch (event.getAction()) {
      case DragEvent.ACTION_DRAG_STARTED:
        if (event
          .getClipDescription()
          .hasMimeType(ClipDescription.MIMETYPE_TEXT_URILIST)) {
          applyDropHint(v, R.drawable.droppable);
        }
        else {
          result=false;
        }

        break;

      case DragEvent.ACTION_DRAG_ENTERED:
        applyDropHint(v, R.drawable.drop);
        break;

      case DragEvent.ACTION_DRAG_EXITED:
        applyDropHint(v, R.drawable.droppable);
        break;

      case DragEvent.ACTION_DRAG_ENDED:
        applyDropHint(v, -1);
        break;

      case DragEvent.ACTION_DROP:
        requestDragAndDropPermissions(event);

        ClipData.Item clip=event.getClipData().getItemAt(0);

        imageUri=clip.getUri();
        showThumbnail();
        break;
    }

    return(result);
  }

  private void applyDropHint(View v, int drawableId) {
    View parent=(View)v.getParent();

    if (drawableId>-1) {
      parent.setBackgroundResource(drawableId);
    }
    else {
      parent.setBackground(null);
    }
  }

  private void showThumbnail() {
    Picasso.with(this)
      .load(imageUri)
      .fit().centerCrop()
      .placeholder(R.drawable.ic_photo_size_select_actual_black_24dp)
      .error(R.drawable.ic_error_black_24dp)
      .into(image);
  }
}
