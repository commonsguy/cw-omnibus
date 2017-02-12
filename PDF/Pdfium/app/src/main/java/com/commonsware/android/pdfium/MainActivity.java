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

package com.commonsware.android.pdfium;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

public class MainActivity extends Activity {
  private static final int REQUEST_OPEN=1337;
  private static final int REQUEST_GET=REQUEST_OPEN+1;
  private static final String STATE_ASSET="asset";
  private static final String STATE_PICKED="picked";
  private PDFView viewer;
  private String chosenAsset=null;
  private Uri pickedDocument=null;

  @SuppressLint("SetJavaScriptEnabled")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    viewer=(PDFView)findViewById(R.id.viewer);

    if (savedInstanceState!=null) {
      chosenAsset=savedInstanceState.getString(STATE_ASSET);

      if (chosenAsset==null) {
        pickedDocument=savedInstanceState.getParcelable(STATE_PICKED);

        if (pickedDocument!=null) {
          configureViewer(viewer.fromUri(pickedDocument));
        }
      }
      else {
        configureViewer(viewer.fromAsset(chosenAsset));
      }
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putString(STATE_ASSET, chosenAsset);
    outState.putParcelable(STATE_PICKED, pickedDocument);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.pdf, menu);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId()==R.id.preso) {
      loadPdf("MultiWindowAndYourApp.pdf");
      return(true);
    }
    else if (item.getItemId()==R.id.taxes) {
      loadPdf("f1040a.pdf");
      return(true);
    }
    else if (item.getItemId()==R.id.open) {
      open();
    }

    return(super.onOptionsItemSelected(item));
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
                                  Intent data) {
    if (resultCode==Activity.RESULT_OK) {
      pickedDocument=data.getData();
      chosenAsset=null;
      configureViewer(viewer.fromUri(pickedDocument));
    }
  }

  private void loadPdf(String name) {
    chosenAsset=name;
    pickedDocument=null;
    configureViewer(viewer.fromAsset(name));
  }

  private void configureViewer(PDFView.Configurator configurator) {
    configurator
      .enableSwipe(true)
      .swipeHorizontal(true)
      .enableDoubletap(true)
      .scrollHandle(new DefaultScrollHandle(this))
      .load();
  }

  private void open() {
    if (Build.VERSION.SDK_INT<Build.VERSION_CODES.KITKAT) {
      Intent i=
        new Intent()
          .setType("application/pdf")
          .setAction(Intent.ACTION_GET_CONTENT)
          .addCategory(Intent.CATEGORY_OPENABLE);

      startActivityForResult(i, REQUEST_GET);
    }
    else {
      Intent i=
        new Intent()
          .setType("application/pdf")
          .setAction(Intent.ACTION_OPEN_DOCUMENT)
          .addCategory(Intent.CATEGORY_OPENABLE);

      startActivityForResult(i, REQUEST_OPEN);
    }
  }
}
