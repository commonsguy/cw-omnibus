/***
 Copyright (c) 2016 CommonsWare, LLC
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

package com.commonsware.android.recyclerview.videolist;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

class Video implements Comparable<Video> {
  final String title;
  final Uri videoUri;
  final String mimeType;

  Video(Cursor row) {
    this.title=
      row.getString(row.getColumnIndex(MediaStore.Video.Media.TITLE));
    this.videoUri=ContentUris.withAppendedId(
      MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
      row.getInt(row.getColumnIndex(MediaStore.Video.Media._ID)));
    this.mimeType=
      row.getString(row.getColumnIndex(MediaStore.Video.Media.MIME_TYPE));
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Video)) {
      return(false);
    }

    return(videoUri.equals(((Video)obj).videoUri));
  }

  @Override
  public int hashCode() {
    return(videoUri.hashCode());
  }

  @Override
  public int compareTo(Video video) {
    return(title.compareTo(video.title));
  }
}
