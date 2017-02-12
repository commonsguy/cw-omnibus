/***
  Copyright (c) 2012-17 CommonsWare, LLC
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

package com.commonsware.android.pdfrenderer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity {
  private static final int REQUEST_OPEN=1337;
  private static final String STATE_PICKED="picked";
  private final SnapHelper snapperCarr=new PagerSnapHelper();
  private PageAdapter adapter;
  private RecyclerView pager;
  private Uri pickedDocument=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    pager=(RecyclerView)findViewById(R.id.pager);
    pager.setLayoutManager(new LinearLayoutManager(this,
      LinearLayoutManager.HORIZONTAL, false));
    snapperCarr.attachToRecyclerView(pager);

    if (savedInstanceState!=null) {
      pickedDocument=savedInstanceState.getParcelable(STATE_PICKED);

      if (pickedDocument!=null) {
        show(pickedDocument);
      }
    }
  }

  @Override
  protected void onDestroy() {
    if (adapter!=null) {
      adapter.close();
    }

    super.onDestroy();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putParcelable(STATE_PICKED, pickedDocument);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.pdf, menu);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId()==R.id.pdfs) {
      open();
    }

    return(super.onOptionsItemSelected(item));
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
                                  Intent data) {
    if (resultCode==Activity.RESULT_OK) {
      pickedDocument=data.getData();
      show(pickedDocument);
    }
  }

  private void open() {
    Intent i=new Intent()
      .setType("application/pdf")
      .setAction(Intent.ACTION_OPEN_DOCUMENT)
      .addCategory(Intent.CATEGORY_OPENABLE);

    startActivityForResult(i, REQUEST_OPEN);
  }

  private void show(Uri uri) {
    try {
      adapter=new PageAdapter(getLayoutInflater(),
        getContentResolver().openFileDescriptor(uri, "r"));
      pager.setAdapter(adapter);
    }
    catch (java.io.IOException e) {
      Log.e("PdfRenderer", getString(R.string.toast_open), e);
      Toast.makeText(this, R.string.toast_open, Toast.LENGTH_LONG).show();
    }
  }
}