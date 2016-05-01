/***
  Copyright (c) 2014 CommonsWare, LLC
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

package com.commonsware.android.video.browse;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.view.View;
import java.util.ArrayList;

public class VideosFragment extends BrowseFragment
    implements OnItemViewClickedListener,
    LoaderManager.LoaderCallbacks<Cursor> {
  private ArrayList<Video> videos=new ArrayList<Video>();

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    getLoaderManager().initLoader(0, null, this);
    setOnItemViewClickedListener(this);
  }

  @Override
  public void onItemClicked(Presenter.ViewHolder viewHolder,
                            Object o,
                            RowPresenter.ViewHolder rowViewHolder,
                            Row row) {
    Video video=(Video)o;
    ((MainActivity)getActivity()).onVideoSelected(video.uri,
                                                  video.mimeType);
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
    mapCursorToModels(c);

    setHeadersState(BrowseFragment.HEADERS_ENABLED);
    setTitle(getString(R.string.app_name));

    ArrayObjectAdapter rows=new ArrayObjectAdapter(new ListRowPresenter());
    ArrayObjectAdapter listRowAdapter=
        new ArrayObjectAdapter(new VideoPresenter(getActivity()));

    for (Video v : videos) {
      listRowAdapter.add(v);
    }

    HeaderItem header=new HeaderItem(0, "Videos", null);

    rows.add(new ListRow(header, listRowAdapter));
    setAdapter(rows);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    setAdapter(null);
  }

  private void mapCursorToModels(Cursor c) {
    videos.clear();

    int idColumn=c.getColumnIndex(MediaStore.Video.Media._ID);
    int uriColumn=c.getColumnIndex(MediaStore.Video.Media.DATA);
    int mimeTypeColumn=
        c.getColumnIndex(MediaStore.Video.Media.MIME_TYPE);
    int titleColumn=
        c.getColumnIndex(MediaStore.Video.Media.TITLE);

    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
      videos.add(new Video(c.getInt(idColumn),
                            c.getString(uriColumn),
                            c.getString(mimeTypeColumn),
                            c.getString(titleColumn)));
    }

    c.close();
  }
}
