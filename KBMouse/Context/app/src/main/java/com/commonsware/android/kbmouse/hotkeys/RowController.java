/***
 Copyright (c) 2015-2016 CommonsWare, LLC
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

package com.commonsware.android.kbmouse.hotkeys;

import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.io.File;

class RowController extends RecyclerView.ViewHolder
  implements View.OnClickListener, View.OnLongClickListener,
  View.OnTouchListener {
  private final ChoiceCapableAdapter<?> adapter;
  private int position;
  private TextView title=null;
  private ImageView thumbnail=null;
  private Uri videoUri=null;
  private String videoMimeType=null;

  RowController(View row, ChoiceCapableAdapter<?> adapter) {
    super(row);
    this.adapter=adapter;

    title=(TextView)row.findViewById(android.R.id.text1);
    thumbnail=(ImageView)row.findViewById(R.id.thumbnail);

    row.setOnClickListener(this);
    row.setOnLongClickListener(this);
    row.setOnTouchListener(this);
  }

  @Override
  public void onClick(View v) {
    Intent i=new Intent(Intent.ACTION_VIEW);

    i.setDataAndType(videoUri, videoMimeType);
    title.getContext().startActivity(i);
  }

  @Override
  public boolean onLongClick(View v) {
    ClipData clip=ClipData.newRawUri(title.getText(), videoUri);
    View.DragShadowBuilder shadow=new View.DragShadowBuilder(thumbnail);

    itemView.startDrag(clip, shadow, null, 0);

    return(true);
  }

  @Override
  public boolean onTouch(View v, MotionEvent event) {
    if ((event.getButtonState() & MotionEvent.BUTTON_SECONDARY)!=0 &&
      event.getAction()==MotionEvent.ACTION_DOWN) {
      adapter.onChecked(position, true, true);

      String[] items=
        itemView
          .getContext()
          .getResources()
          .getStringArray(R.array.popup);
      ArrayAdapter<String> adapter=
        new ArrayAdapter<>(itemView.getContext(),
          android.R.layout.simple_list_item_1,
          items);
      final ListPopupWindow popup=
        new ListPopupWindow(itemView.getContext());

      popup.setAnchorView(itemView);
      popup.setHorizontalOffset((int)event.getX());
      popup.setVerticalOffset((int)event.getY()-itemView.getHeight());
      popup.setAdapter(adapter);
      popup.setWidth(measureContentWidth(itemView.getContext(), adapter));

      popup.setOnItemClickListener(
        new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view,
                                  int position, long id) {
            if (position==0) {
              ((MainActivity)itemView.getContext())
                .playVideo(videoUri);
            }
            else {
              ((MainActivity)itemView.getContext())
                .showLargeThumbnail(videoUri);
            }

            popup.dismiss();
          }
        });

      popup.show();

      return(true);
    }

    return(false);
  }

  void bindModel(Cursor row, boolean isChecked, int position) {
    this.position=position;
    title.setText(row.getString(
      row.getColumnIndex(MediaStore.Video.Media.TITLE)));

    videoUri=
        ContentUris.withAppendedId(
          MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
          row.getInt(row.getColumnIndex(MediaStore.Video.Media._ID)));

    Picasso.with(thumbnail.getContext())
      .load(videoUri.toString())
      .fit().centerCrop()
      .placeholder(R.drawable.ic_media_video_poster)
      .into(thumbnail);

    int mimeTypeColumn=
        row.getColumnIndex(MediaStore.Video.Media.MIME_TYPE);

    videoMimeType=row.getString(mimeTypeColumn);
    setChecked(isChecked);
  }

  void setChecked(boolean isChecked) {
    itemView.setActivated(isChecked);
  }

  // based on http://stackoverflow.com/a/26814964/115145

  private int measureContentWidth(Context ctxt, ListAdapter listAdapter) {
    ViewGroup mMeasureParent = null;
    int maxWidth = 0;
    View itemView = null;
    int itemType = 0;

    final ListAdapter adapter = listAdapter;
    final int widthMeasureSpec =
      View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
    final int heightMeasureSpec =
      View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
    final int count = adapter.getCount();
    for (int i = 0; i < count; i++) {
      final int positionType = adapter.getItemViewType(i);
      if (positionType != itemType) {
        itemType = positionType;
        itemView = null;
      }

      if (mMeasureParent == null) {
        mMeasureParent = new FrameLayout(ctxt);
      }

      itemView = adapter.getView(i, itemView, mMeasureParent);
      itemView.measure(widthMeasureSpec, heightMeasureSpec);

      final int itemWidth = itemView.getMeasuredWidth();

      if (itemWidth > maxWidth) {
        maxWidth = itemWidth;
      }
    }

    return maxWidth;
  }
}
