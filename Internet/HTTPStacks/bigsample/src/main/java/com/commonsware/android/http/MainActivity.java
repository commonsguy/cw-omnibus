/***
  Copyright (c) 2013-2014 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.http;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.commonsware.cwac.security.SignatureUtils;
import java.security.NoSuchAlgorithmException;
import de.greenrobot.event.EventBus;
import io.karim.MaterialTabs;

public class MainActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    ViewPager pager=(ViewPager)findViewById(R.id.pager);
    pager.setAdapter(new QuestionsPagerAdapter(this, getFragmentManager()));

    MaterialTabs tabs=(MaterialTabs)findViewById(R.id.tabs);
    tabs.setViewPager(pager);
  }

  @Override
  public void onResume() {
    super.onResume();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onPause() {
    EventBus.getDefault().unregister(this);
    super.onPause();
  }

  public void onEventMainThread(QuestionClickedEvent event) {
    startActivity(new Intent(Intent.ACTION_VIEW,
                             Uri.parse(event.item.link)));
  }
}
