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

package com.commonsware.android.eu4you2;

import android.os.Bundle;
import android.webkit.WebViewFragment;

public class DetailsFragment extends WebViewFragment {
  private static final String STATE_URL="url";
  private String url=null;

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    if (url == null && savedInstanceState != null) {
      url=savedInstanceState.getString(STATE_URL);
    }

    if (url != null) {
      loadUrl(url);
      url=null;
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    if (url == null) {
      outState.putString(STATE_URL, getWebView().getUrl());
    }
    else {
      outState.putString(STATE_URL, url);
    }
  }

  void loadUrl(String url) {
    if (getView() == null) {
      this.url=url;
    }
    else {
      getWebView().loadUrl(url);
    }
  }
}
