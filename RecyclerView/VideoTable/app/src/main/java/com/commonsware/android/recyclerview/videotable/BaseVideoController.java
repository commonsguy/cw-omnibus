/***
 Copyright (c) 2015 CommonsWare, LLC
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

package com.commonsware.android.recyclerview.videotable;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.View;

abstract class BaseVideoController extends RecyclerView.ViewHolder
    implements View.OnClickListener {
  protected Uri videoUri=null;
  private String videoMimeType=null;

  BaseVideoController(View cell) {
    super(cell);

    cell.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    Intent i=new Intent(Intent.ACTION_VIEW);

    i.setDataAndType(videoUri, videoMimeType);
    itemView.getContext().startActivity(i);
  }

  void bindModel(Cursor row) {
    int mimeTypeColumn=
        row.getColumnIndex(MediaStore.Video.Media.MIME_TYPE);

    videoUri=ContentUris.withAppendedId(
      MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
      row.getInt(row.getColumnIndex(MediaStore.Video.Media._ID)));
    videoMimeType=row.getString(mimeTypeColumn);
  }
}
