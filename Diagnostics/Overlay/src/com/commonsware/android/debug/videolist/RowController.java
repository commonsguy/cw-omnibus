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

package com.commonsware.android.debug.videolist;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.io.File;

class RowController extends RecyclerView.ViewHolder
    implements View.OnClickListener {
  final private TextView title;
  final private ImageView thumbnail;
  private String videoUri=null;
  private String videoMimeType=null;

  RowController(View row) {
    super(row);

    title=(TextView)row.findViewById(android.R.id.text1);
    thumbnail=(ImageView)row.findViewById(R.id.thumbnail);

    row.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    Uri video=Uri.fromFile(new File(videoUri));
    Intent i=new Intent(Intent.ACTION_VIEW);

    i.setDataAndType(video, videoMimeType);
    title.getContext().startActivity(i);
  }

  void bindModel(Cursor row) {
    title.setText(row.getString(row.getColumnIndex(MediaStore.Video.Media.TITLE)));

    int uriColumn=row.getColumnIndex(MediaStore.Video.Media.DATA);
    int mimeTypeColumn=
        row.getColumnIndex(MediaStore.Video.Media.MIME_TYPE);
    int videoId=row.getInt(row.getColumnIndex(MediaStore.Video.Media._ID));

    videoUri=row.getString(uriColumn);
    videoMimeType=row.getString(mimeTypeColumn);

    if (BuildConfig.BE_STUPID) {
      ContentResolver cr=thumbnail.getContext().getContentResolver();
      BitmapFactory.Options options=new BitmapFactory.Options();

      options.inSampleSize = 1;

      Bitmap thumb=MediaStore.Video.Thumbnails.getThumbnail(cr, videoId,
          MediaStore.Video.Thumbnails.MICRO_KIND, options);

      thumbnail.setImageBitmap(thumb);
    }
    else {
      Uri video=
          ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
              videoId);

      Picasso.with(thumbnail.getContext())
        .load(video.toString())
        .fit().centerCrop()
        .placeholder(R.drawable.ic_media_video_poster)
        .into(thumbnail);
    }
  }
}
