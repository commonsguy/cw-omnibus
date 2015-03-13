/***
 Copyright (c) 2015 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 From _The Busy Coder's Guide to Android Development_
 http://commonsware.com/Android
 */

package com.commonsware.android.recyclerview.videolist;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.io.File;

class RowController extends RecyclerView.ViewHolder
    implements View.OnClickListener {
  private TextView title=null;
  private ImageView thumbnail=null;
  private ImageLoader imageLoader=null;
  private String videoUri=null;
  private String videoMimeType=null;

  RowController(View row, ImageLoader imageLoader) {
    super(row);
    this.imageLoader=imageLoader;

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

    Uri video=
        ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            row.getInt(row.getColumnIndex(MediaStore.Video.Media._ID)));
    DisplayImageOptions opts=new DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.ic_media_video_poster)
        .build();

    imageLoader.displayImage(video.toString(), thumbnail, opts);

    int uriColumn=row.getColumnIndex(MediaStore.Video.Media.DATA);
    int mimeTypeColumn=
        row.getColumnIndex(MediaStore.Video.Media.MIME_TYPE);

    videoUri=row.getString(uriColumn);
    videoMimeType=row.getString(mimeTypeColumn);
  }
}
