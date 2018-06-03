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

package com.commonsware.android.kbmouse.hotkeys;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.KeyboardShortcutGroup;
import android.view.KeyboardShortcutInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements
  LoaderManager.LoaderCallbacks<Cursor>, View.OnDragListener {
  private static final String STATE_IN_PERMISSION="inPermission";
  private static final int REQUEST_PERMS=137;
  private RecyclerView videoList;
  private VideoView player;
  private ImageView thumbnailLarge;
  private boolean isInPermission=false;
  private VideoAdapter adapter;

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);
    setContentView(R.layout.main);

    player=findViewById(R.id.player);

    if (player!=null) {
      player.setOnDragListener(this);
    }

    thumbnailLarge=findViewById(R.id.thumbnail_large);

    if (thumbnailLarge!=null) {
      thumbnailLarge.setOnDragListener(this);
    }

    setLayoutManager(new LinearLayoutManager(this));
    adapter=new VideoAdapter(getRecyclerView());
    setAdapter(adapter);
    getRecyclerView().requestFocus();

    if (state!=null) {
      isInPermission=
        state.getBoolean(STATE_IN_PERMISSION, false);
    }

    if (hasFilesPermission()) {
      loadVideos();
    }
    else if (!isInPermission) {
      isInPermission=true;

      ActivityCompat.requestPermissions(this,
        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
        REQUEST_PERMS);
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putBoolean(STATE_IN_PERMISSION, isInPermission);
    adapter.onSaveInstanceState(outState);
  }

  @Override
  public void onRestoreInstanceState(Bundle state) {
    adapter.onRestoreInstanceState(state);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String[] permissions,
                                         int[] grantResults) {
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

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int position=adapter.getCheckedPosition();

    if (item.getItemId()==R.id.play_video) {
      if (position>=0) {
        playVideo(adapter.getVideoUri(position));
      }
      else {
        Toast.makeText(this, R.string.msg_choose,
          Toast.LENGTH_LONG).show();
      }

      return(true);
    }
    else if (item.getItemId()==R.id.show_thumbnail) {
      if (position>=0) {
        showLargeThumbnail(adapter.getVideoUri(position));
      }
      else {
        Toast.makeText(this, R.string.msg_choose,
          Toast.LENGTH_LONG).show();
      }

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
    ((VideoAdapter)getAdapter()).setVideos(c);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    ((VideoAdapter)getAdapter()).setVideos(null);
  }

  @Override
  public boolean onDrag(View v, DragEvent event) {
    boolean result=true;

    switch (event.getAction()) {
      case DragEvent.ACTION_DRAG_STARTED:
        applyDropHint(v, R.drawable.droppable);
        break;

      case DragEvent.ACTION_DRAG_ENTERED:
        applyDropHint(v, R.drawable.drop);
        break;

      case DragEvent.ACTION_DRAG_EXITED:
        applyDropHint(v, R.drawable.droppable);
        break;

      case DragEvent.ACTION_DRAG_ENDED:
        applyDropHint(v, -1);
        break;

      case DragEvent.ACTION_DROP:
        ClipData.Item clip=event.getClipData().getItemAt(0);
        Uri videoUri=clip.getUri();

        if (v==player) {
          playVideo(videoUri);
        }
        else {
          showLargeThumbnail(videoUri);
        }

        break;
    }

    return(result);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (event.getRepeatCount()==0) {
      if (keyCode==KeyEvent.KEYCODE_DPAD_RIGHT) {
        int position=adapter.getCheckedPosition();

        if (position>=0) {
          Uri videoUri=adapter.getVideoUri(position);

          if (event.isAltPressed()) {
            playVideo(videoUri);
          }
          else if (event.isCtrlPressed()) {
            showLargeThumbnail(videoUri);
          }

          return(true);
        }
      }
      else if (keyCode==KeyEvent.KEYCODE_SLASH &&
        event.isMetaPressed() &&
        Build.VERSION.SDK_INT<Build.VERSION_CODES.N) {
        new ShortcutDialogFragment().show(getSupportFragmentManager(),
          "shortcuts");

        return(true);
      }
    }

    return(super.onKeyDown(keyCode, event));
  }

  @TargetApi(Build.VERSION_CODES.N)
  @Override
  public void onProvideKeyboardShortcuts(
    List<KeyboardShortcutGroup> data, Menu menu, int deviceId) {
    super.onProvideKeyboardShortcuts(data, menu, deviceId);

    List<KeyboardShortcutInfo> shortcuts=new ArrayList<>();
    String caption=getString(R.string.menu_video);

    shortcuts.add(new KeyboardShortcutInfo(caption,
      KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.META_ALT_ON));

    caption=getString(R.string.menu_thumbnail);
    shortcuts.add(new KeyboardShortcutInfo(caption,
      KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.META_CTRL_ON));
    data.add(new KeyboardShortcutGroup(getString(R.string.msg_custom),
      shortcuts));
  }

  private void setAdapter(RecyclerView.Adapter adapter) {
    getRecyclerView().setAdapter(adapter);
  }

  private RecyclerView.Adapter getAdapter() {
    return(getRecyclerView().getAdapter());
  }

  private void setLayoutManager(RecyclerView.LayoutManager mgr) {
    getRecyclerView().setLayoutManager(mgr);
  }

  private RecyclerView getRecyclerView() {
    if (videoList==null) {
      videoList=findViewById(R.id.video_list);
    }

    return(videoList);
  }

  private void applyDropHint(View v, int drawableId) {
    View parent=(View)v.getParent();

    if (drawableId>-1) {
      parent.setBackgroundResource(drawableId);
    }
    else {
      parent.setBackground(null);
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

  private void playVideo(Uri videoUri) {
    player.setVideoURI(videoUri);
    player.start();
  }

  private void showLargeThumbnail(Uri videoUri) {
    Picasso.with(thumbnailLarge.getContext())
      .load(videoUri.toString())
      .fit().centerCrop()
      .placeholder(R.drawable.ic_media_video_poster)
      .into(thumbnailLarge);
  }

  private class VideoAdapter
    extends ChoiceCapableAdapter<RowController> {
    Cursor videos=null;

    VideoAdapter(RecyclerView rv) {
      super(rv, new SingleChoiceMode());
    }

    @Override
    public RowController onCreateViewHolder(ViewGroup parent,
                                            int viewType) {
      return(new RowController(getLayoutInflater()
        .inflate(R.layout.row, parent, false)));
    }

    void setVideos(Cursor videos) {
      this.videos=videos;
      notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RowController holder, int position) {
      videos.moveToPosition(position);
      holder.bindModel(videos, isChecked(position));
    }

    @Override
    public int getItemCount() {
      if (videos==null) {
        return(0);
      }

      return(videos.getCount());
    }

    private Uri getVideoUri(int position) {
      videos.moveToPosition(position);

      return(ContentUris.withAppendedId(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        videos.getInt(
          videos.getColumnIndex(MediaStore.Video.Media._ID))));
    }
  }
}
