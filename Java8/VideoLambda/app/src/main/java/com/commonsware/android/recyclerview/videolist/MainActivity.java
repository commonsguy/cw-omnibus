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

package com.commonsware.android.recyclerview.videolist;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends RecyclerViewActivity implements
    LoaderManager.LoaderCallbacks<Cursor> {
  private static final String STATE_SORT="sortAscending";
  private static final String STATE_IN_PERMISSION="inPermission";
  private static final int REQUEST_PERMS=137;
  private boolean isInPermission=false;
  private MenuItem sort;
  private VideoAdapter adapter;

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);

    setLayoutManager(new LinearLayoutManager(this));

    adapter=new VideoAdapter();
    setAdapter(adapter);

    if (state!=null) {
      isInPermission=
        state.getBoolean(STATE_IN_PERMISSION, false);
      adapter.sortAscending=state.getBoolean(STATE_SORT, true);
    }

    if (hasFilesPermission()) {
      loadVideos();
    }
    else if (!isInPermission) {
      isInPermission=true;

      ActivityCompat.requestPermissions(this,
        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
        REQUEST_PERMS);
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putBoolean(STATE_IN_PERMISSION, isInPermission);
    outState.putBoolean(STATE_SORT, adapter.sortAscending);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    isInPermission=false;

    if (requestCode==REQUEST_PERMS) {
      if (hasFilesPermission()) {
        loadVideos();
      }
      else {
        finish(); // denied permission, so we're done
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.actions, menu);
    sort=menu.findItem(R.id.sort);
    sort.setEnabled(adapter.getItemCount()>0);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId()==R.id.sort) {
      item.setChecked(!item.isChecked());
      adapter.sort(item.isChecked());

      return(true);
    }

    return(super.onOptionsItemSelected(item));
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
    adapter.setVideos(c);

    if (sort!=null) {
      sort.setEnabled(adapter.getItemCount()>0);
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    adapter.setVideos(null);

    if (sort!=null) {
      sort.setEnabled(false);
    }
  }

  private boolean hasFilesPermission() {
    return(ContextCompat.checkSelfPermission(this,
      Manifest.permission.READ_EXTERNAL_STORAGE)==
      PackageManager.PERMISSION_GRANTED);
  }

  private void loadVideos() {
    getSupportLoaderManager().initLoader(0, null, this);
  }

  class VideoAdapter extends RecyclerView.Adapter<RowController> {
    ArrayList<Video> videos;
    boolean sortAscending=true;

    @Override
    public RowController onCreateViewHolder(ViewGroup parent, int viewType) {
      return(new RowController(getLayoutInflater()
                                 .inflate(R.layout.row, parent, false)));
    }

    void setVideos(Cursor c) {
      if (c==null) {
        videos=null;
        notifyDataSetChanged();
      }
      else {
        ArrayList<Video> temp=new ArrayList<>();

        while (c.moveToNext()) {
          temp.add(new Video(c));
        }

        if (videos==null) {
          videos=new ArrayList<>();
        }

        sortAndApply(temp);
      }
    }

    @Override
    public void onBindViewHolder(RowController holder, int position) {
      holder.bindModel(videos.get(position));
    }

    @Override
    public int getItemCount() {
      if (videos==null) {
        return(0);
      }

      return(videos.size());
    }

    private void sortAndApply(ArrayList<Video> newVideos) {
      if (sortAscending) {
        Collections.sort(newVideos,
          (one, two) -> one.compareTo(two));
      }
      else {
        Collections.sort(newVideos,
          (one, two) -> two.compareTo(one));
      }

      DiffUtil.Callback cb=new SimpleCallback<>(videos, newVideos);
      DiffUtil.DiffResult result=DiffUtil.calculateDiff(cb, true);

      videos=newVideos;
      result.dispatchUpdatesTo(this);
    }

    private void sort(boolean checked) {
      if (sortAscending!=checked) {
        sortAscending=checked;
        sortAndApply(new ArrayList<>(videos));
      }
    }
  }
}
