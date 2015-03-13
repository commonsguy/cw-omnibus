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
    http://commonsware.com/Android
 */

package com.commonsware.android.webkit;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class BrowserDemo3 extends Activity {
  WebView browser;

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.main);
    browser=(WebView)findViewById(R.id.webkit);

    browser.getSettings().setJavaScriptEnabled(true);
    browser.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

    browser.setWebChromeClient(new WebChromeClient() {
      @Override
      public boolean onJsAlert(WebView wv, String url,
                               String msg, JsResult result) {
        Log.e("SOPDemo", msg);
        result.confirm();

        return (true);
      }
    });

    browser.loadUrl("http://commonsware.com/misc/sop-demo.html");
  }
}
