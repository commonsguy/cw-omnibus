/***
 Copyright (c) 2015-2016 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 From _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.recyclerview.videotable;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

class VideoThumbnailController extends BaseVideoController {
  private ImageView thumbnail=null;

  VideoThumbnailController(View cell) {
    super(cell);

    thumbnail=(ImageView)cell.findViewById(R.id.thumbnail);
  }

  @Override
  void bindModel(Cursor row) {
    super.bindModel(row);

    Uri video=
        ContentUris.withAppendedId(
          MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
          row.getInt(row.getColumnIndex(MediaStore.Video.Media._ID)));

    Picasso.with(thumbnail.getContext())
      .load(video.toString())
      .fit().centerCrop()
      .placeholder(R.drawable.ic_media_video_poster)
      .into(thumbnail);
  }
}
