/***
  Copyright (c) 2008-2016 CommonsWare, LLC
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

import android.Manifest;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

public class MainActivity extends AbstractPermissionActivity implements
    LoaderManager.LoaderCallbacks<Cursor> {
  private static final String[] PERMS={Manifest.permission.READ_EXTERNAL_STORAGE};

  @Override
  protected String[] getDesiredPermissions() {
    return(PERMS);
  }

  @Override
  protected void onPermissionDenied() {
    Toast
      .makeText(this, R.string.msg_no_perm, Toast.LENGTH_LONG)
      .show();
    finish();
  }

  @Override
  public void onReady() {

    getSupportLoaderManager().initLoader(0, null, this);

    setLayoutManager(new LinearLayoutManager(this));
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

  class VideoAdapter extends RecyclerView.Adapter<RowController> {
    private final LayoutInflater inflater;
    Cursor videos=null;

    VideoAdapter() {
      super();

      inflater=getLayoutInflater();
    }

    @Override
    public RowController onCreateViewHolder(ViewGroup parent, int viewType) {
      return(new RowController(inflater.inflate(R.layout.row, parent, false)));
    }

    void setVideos(Cursor videos) {
      this.videos=videos;
      notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RowController holder, int position) {
      videos.moveToPosition(position);
      holder.bindModel(videos);
    }

    @Override
    public int getItemCount() {
      if (videos==null) {
        return(0);
      }

      return(videos.getCount());
    }
  }
}
