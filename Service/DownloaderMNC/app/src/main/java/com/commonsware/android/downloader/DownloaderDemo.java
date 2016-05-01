/***
  Copyright (c) 2008-2012 CommonsWare, LLC
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

package com.commonsware.android.downloader;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;

public class DownloaderDemo extends Activity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (BuildConfig.APPLY_STRICT_MODE) {
      StrictMode.VmPolicy.Builder b=new StrictMode.VmPolicy.Builder();

      b.detectCleartextNetwork().penaltyLog();

      StrictMode.setVmPolicy(b.build());
    }

    if (getFragmentManager().findFragmentById(android.R.id.content) == null) {
      getFragmentManager().beginTransaction()
                                 .add(android.R.id.content,
                                      new DownloadFragment()).commit();
    }
  }
}
