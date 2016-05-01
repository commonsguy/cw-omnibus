/***
  Copyright (c) 2008-2016 CommonsWare, LLC
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

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public class MainActivity extends RecyclerViewActivity implements
    LoaderManager.LoaderCallbacks<Cursor> {
  private static final int[] COLUMN_WEIGHTS={1, 4, 1};

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);

    getLoaderManager().initLoader(0, null, this);

    ColumnWeightSpanSizeLookup spanSizer=new ColumnWeightSpanSizeLookup(COLUMN_WEIGHTS);
    GridLayoutManager mgr=new GridLayoutManager(this, spanSizer.getTotalSpans());

    mgr.setSpanSizeLookup(spanSizer);
    setLayoutManager(mgr);
    setAdapter(new VideoAdapter());
  }

  @Override
  public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
    return(new CursorLoader(this,
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            null, null, null,
                            MediaStore.Video.Media.TITLE));
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
    ((VideoAdapter)getAdapter()).setVideos(c);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    ((VideoAdapter)getAdapter()).setVideos(null);
  }

  class VideoAdapter extends RecyclerView.Adapter<BaseVideoController> {
    Cursor videos=null;

    @Override
    public BaseVideoController onCreateViewHolder(ViewGroup parent, int viewType) {
      BaseVideoController result=null;

      switch(viewType) {
        case 0:
          result=new VideoThumbnailController(getLayoutInflater()
                                                 .inflate(R.layout.thumbnail,
                                                          parent, false));
          break;

        case 1:
          int cursorColumn=videos.getColumnIndex(MediaStore.Video.VideoColumns.DISPLAY_NAME);

          result=new VideoTextController(getLayoutInflater()
                                            .inflate(R.layout.label,
                                                parent, false),
                                          android.R.id.text1,
                                          cursorColumn);
          break;

        case 2:
          cursorColumn=videos.getColumnIndex(MediaStore.Video.VideoColumns.DURATION);

          result=new VideoTextController(getLayoutInflater()
                                            .inflate(R.layout.label,
                                                parent, false),
                                          android.R.id.text1,
                                          cursorColumn);
          break;
      }

      return(result);
    }

    void setVideos(Cursor videos) {
      this.videos=videos;
      notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(BaseVideoController holder, int position) {
      videos.moveToPosition(position/3);
      holder.bindModel(videos);
    }

    @Override
    public int getItemCount() {
      if (videos==null) {
        return(0);
      }

      return(videos.getCount()*3);
    }

    @Override
    public int getItemViewType(int position) {
      return(position % 3);
    }
  }
}
