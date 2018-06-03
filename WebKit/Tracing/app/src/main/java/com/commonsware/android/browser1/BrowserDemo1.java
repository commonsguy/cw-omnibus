/***
  Copyright (c) 2008-2012 CommonsWare, LLC
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

package com.commonsware.android.browser1;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.TracingConfig;
import android.webkit.TracingController;
import android.webkit.WebView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.concurrent.Executors;
import static android.webkit.TracingConfig.CATEGORIES_WEB_DEVELOPER;

public class BrowserDemo1 extends Activity {
  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);
    setContentView(R.layout.main);

    TracingController.getInstance()
      .start(new TracingConfig.Builder()
        .addCategories(CATEGORIES_WEB_DEVELOPER)
        .build());

    ((WebView)findViewById(R.id.webkit)).loadUrl("https://commonsware.com");
  }

  @Override
  protected void onDestroy() {
    File out=new File(getExternalFilesDir(null), "trace.json");

    try {
      TracingController.getInstance().stop(new FileOutputStream(out),
        AsyncTask.THREAD_POOL_EXECUTOR);
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    super.onDestroy();
  }
}
