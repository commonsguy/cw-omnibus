/***
  Copyright (c) 2013 CommonsWare, LLC
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

package com.commonsware.android.video.list;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.loopj.android.image.SmartImage;
import com.loopj.android.image.SmartImageView;

public class VideosFragment extends
    ContractListFragment<VideosFragment.Contract> implements
    LoaderManager.LoaderCallbacks<Cursor> {

  @Override
  public void onActivityCreated(Bundle state) {
    super.onActivityCreated(state);

    String[] from=
        { MediaStore.Video.Media.TITLE, MediaStore.Video.Media._ID };
    int[] to= { android.R.id.text1, R.id.thumbnail };
    SimpleCursorAdapter adapter=
        new SimpleCursorAdapter(getActivity(), R.layout.row, null,
                                from, to, 0);

    adapter.setViewBinder(new ThumbnailBinder());
    setListAdapter(adapter);

    getLoaderManager().initLoader(0, null, this);
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    CursorAdapter adapter=(CursorAdapter)getListAdapter();
    Cursor c=(Cursor)adapter.getItem(position);
    int uriColumn=c.getColumnIndex(MediaStore.Video.Media.DATA);
    int mimeTypeColumn=
        c.getColumnIndex(MediaStore.Video.Media.MIME_TYPE);

    getContract().onVideoSelected(c.getString(uriColumn),
                                  c.getString(mimeTypeColumn));
  }

  @Override
  public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
    return(new CursorLoader(
                            getActivity(),
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            null, null, null,
                            MediaStore.Video.Media.TITLE));
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
    ((CursorAdapter)getListAdapter()).swapCursor(c);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    ((CursorAdapter)getListAdapter()).swapCursor(null);
  }

  interface Contract {
    void onVideoSelected(String uri, String mimeType);
  }

  private static class ThumbnailBinder implements
      SimpleCursorAdapter.ViewBinder {
    @Override
    public boolean setViewValue(View v, Cursor c, int column) {
      if (column == c.getColumnIndex(MediaStore.Video.Media._ID)) {
        VideoThumbnailImage thumb=
            new VideoThumbnailImage(
                                    c.getInt(column),
                                    MediaStore.Video.Thumbnails.MICRO_KIND);

        ((SmartImageView)v).setImage(thumb,
                                     R.drawable.ic_media_video_poster);

        return(true);
      }

      return(false);
    }
  }

  private static class VideoThumbnailImage implements SmartImage {
    private int videoId;
    private int thumbnailKind;

    VideoThumbnailImage(int videoId, int thumbnailKind) {
      this.videoId=videoId;
      this.thumbnailKind=thumbnailKind;
    }

    @Override
    public Bitmap getBitmap(Context ctxt) {
      return(MediaStore.Video.Thumbnails.getThumbnail(ctxt.getContentResolver(),
                                                      videoId,
                                                      thumbnailKind,
                                                      null));
    }
  }
}
