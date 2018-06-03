/***
  Copyright (c) 2012 CommonsWare, LLC
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

package com.commonsware.android.webbeam;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BeamFragment extends WebViewFragment {
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setHasOptionsMenu(true);
  }

  @SuppressLint("SetJavaScriptEnabled")
  @Override
  public void onViewCreated(@NonNull View view,
                            @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    getWebView().setWebViewClient(new BeamClient());
    getWebView().getSettings().setJavaScriptEnabled(true);
    loadUrl("https://google.com");
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    if (getContract().hasNFC()) {
      inflater.inflate(R.menu.actions, menu);
    }

    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.beam) {
      getContract().enablePush();
      
      return(true);
    }

    return(super.onOptionsItemSelected(item));
  }
  
  WebBeamActivity getContract() {
    return((WebBeamActivity)getActivity());
  }
  
  String getUrl() {
    return(getWebView().getUrl());
  }
  
  void loadUrl(String url) {
    android.util.Log.d(getClass().getSimpleName(), url);
    getWebView().stopLoading();
    getWebView().loadUrl(url);
  }

  class BeamClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView wv, String url) {
      wv.loadUrl(url);

      return(true);
    }
  }
}
