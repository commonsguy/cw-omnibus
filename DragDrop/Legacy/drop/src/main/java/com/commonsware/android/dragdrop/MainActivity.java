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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

public class MainActivity extends Activity implements
  View.OnDragListener {
  private static final String STATE_IMAGE_URI=
    BuildConfig.APPLICATION_ID+".IMAGE_URI";
  private Uri imageUri;
  private ImageView image;

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);
    setContentView(R.layout.main);

    image=(ImageView)findViewById(R.id.thumbnail_large);
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
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) {
          requestDragAndDropPermissions(event);
        }

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
