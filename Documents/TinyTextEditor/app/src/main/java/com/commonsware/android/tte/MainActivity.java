/***
  Copyright (c) 2016 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  Covered in detail in the book _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.tte;

import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.HashSet;
import java.util.List;
import io.karim.MaterialTabs;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends Activity
  implements EditorFragment.Contract {
  private static final String ACTION_NEW_WINDOW=
    BuildConfig.APPLICATION_ID+".intent.action.NEW_WINDOW";
  private static final String[] PERMS_FILE={WRITE_EXTERNAL_STORAGE};
  private static final int REQUEST_OPEN=2343;
  private static final int REQUEST_CREATE=REQUEST_OPEN+1;
  private static final int REQUEST_PERMS_FILE=123;
  private EditorsAdapter adapter;
  private ViewPager pager;
  private EditHistory editHistory=EditHistory.INSTANCE;
  private boolean mustRestoreHistory;
  private MaterialTabs tabs;
  private final HashSet<Uri> pendingFiles=new HashSet<>();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    mustRestoreHistory=(savedInstanceState==null &&
            !ACTION_NEW_WINDOW.equals(getIntent().getAction()));

    pager=(ViewPager)findViewById(R.id.pager);
    adapter=new EditorsAdapter(getFragmentManager());
    pager.setAdapter(adapter);

    tabs=(MaterialTabs)findViewById(R.id.tabs);
    tabs.setViewPager(pager);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);

    if (Intent.ACTION_EDIT.equals(intent.getAction())) {
      openEditor(intent.getData());
    }
  }

  @Override
  public void onStart() {
    super.onStart();

    EventBus.getDefault().register(this);

    if (editHistory.initialize(this)) {
      loadEditors();
    }
  }

  @Override
  public void onStop() {
    EventBus.getDefault().unregister(this);

    super.onStop();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.actions, menu);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case R.id.create:
        createDocument();
        return(true);

      case R.id.open:
        openDocument(false);
        return(true);

      case R.id.open_multiple:
        openDocument(true);
        return(true);

      case R.id.close:
        closeCurrentDocument();
        return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  @Override
  protected void onActivityResult(int requestCode,
                                  int resultCode, Intent data) {
    switch(requestCode) {
      case REQUEST_OPEN:
        if (resultCode==Activity.RESULT_OK) {
          if (data.getData()==null) {
            ClipData clip=data.getClipData();

            for (int i=0;i<clip.getItemCount();i++) {
              openEditor(clip.getItemAt(i).getUri());
            }
          }
          else {
            openEditor(data.getData());
          }
        }
        break;

      case REQUEST_CREATE:
        if (resultCode==Activity.RESULT_OK) {
          openEditor(data.getData());
        }
        break;
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String[] permissions,
                                         int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions,
      grantResults);

    if (REQUEST_PERMS_FILE==requestCode) {
      if (canWriteFiles()) {
        for (Uri document : pendingFiles) {
          openEditor(document);
        }

        pendingFiles.clear();
      }
    }
  }

  @Override
  public void applyDisplayName(Uri document,
                               String displayName) {
    adapter.updateTitle(document, displayName);
    tabs.notifyDataSetChanged();
  }

  @Override
  public void close(Uri document) {
    closeDocument(document);
  }

  @Override
  public void launchInNewWindow(Uri document) {
    adapter.remove(document);

    Intent i=
      new Intent(this, MainActivity.class)
        .setAction(ACTION_NEW_WINDOW)
        .setData(document)
        .setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT |
          Intent.FLAG_ACTIVITY_NEW_TASK |
          Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

    startActivity(i);
  }

  @Subscribe(threadMode=ThreadMode.MAIN)
  public void onEditHistoryInitialized(EditHistory.InitializedEvent event) {
    loadEditors();
  }

  @Subscribe(threadMode=ThreadMode.MAIN)
  public void onPermissionFailure(DocumentStorageService.DocumentPermissionFailureEvent event) {
    Toast
      .makeText(this, R.string.msg_perm_failure,
        Toast.LENGTH_LONG)
      .show();

    closeDocument(event.document);
  }

  @Subscribe(threadMode=ThreadMode.MAIN)
  public void onDocumentLoadError(DocumentStorageService.DocumentLoadErrorEvent event) {
    Toast
      .makeText(this, R.string.msg_load_error,
        Toast.LENGTH_LONG)
      .show();

    closeDocument(event.document);
  }

  private void loadEditors() {
    if (mustRestoreHistory) {
      List<Uri> openEditors=editHistory.getOpenEditors();

      for (Uri uri : openEditors) {
        openEditor(uri);
      }

      mustRestoreHistory=false;
    }

    if (getIntent().getData()!=null) {
      openEditor(getIntent().getData());
    }
  }

  private void openEditor(Uri document) {
    if (ContentResolver.SCHEME_CONTENT.equals(document.getScheme()) ||
      canWriteFiles()) {
      int position=adapter.getPositionForDocument(document);

      if (position==-1) {
        adapter.addDocument(document);
        pager.setCurrentItem(adapter.getCount()-1);

        if (!editHistory.addOpenEditor(document)) {
          Toast
            .makeText(this, R.string.msg_save_history,
              Toast.LENGTH_LONG)
            .show();
        }
      }
      else {
        pager.setCurrentItem(position);
      }
    }
    else if (ContentResolver.SCHEME_FILE.equals(document.getScheme())) {
      pendingFiles.add(document);
      ActivityCompat.requestPermissions(this, PERMS_FILE,
        REQUEST_PERMS_FILE);
    }
  }

  private void createDocument() {
    Intent intent=
      new Intent(Intent.ACTION_CREATE_DOCUMENT)
        .addCategory(Intent.CATEGORY_OPENABLE)
        .setType("text/plain");

    startActivityForResult(intent, REQUEST_CREATE);
  }

  private void openDocument(boolean allowMultiple) {
    Intent i=new Intent()
      .setType("text/*")
      .setAction(Intent.ACTION_OPEN_DOCUMENT)
      .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultiple)
      .addCategory(Intent.CATEGORY_OPENABLE);

    startActivityForResult(i, REQUEST_OPEN);
  }

  private void closeCurrentDocument() {
    EditorFragment frag=adapter.getCurrentFragment();

    frag.markAsClosing();
    closeDocument(frag.getDocumentUri());
  }

  private void closeDocument(Uri document) {
    if (!editHistory.removeOpenEditor(document)) {
      Toast
        .makeText(this, R.string.msg_save_history,
          Toast.LENGTH_LONG)
        .show();
    }

    adapter.remove(document);
  }

  private boolean canWriteFiles() {
    return(ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)==
      PackageManager.PERMISSION_GRANTED);
  }
}