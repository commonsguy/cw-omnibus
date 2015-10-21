/***
  Copyright (c) 2012-15 CommonsWare, LLC
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

package com.commonsware.android.assist.mo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.assist.AssistContent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import org.json.JSONException;
import org.json.JSONObject;
import java.security.SecureRandom;
import io.karim.MaterialTabs;

public class MainActivity extends Activity {
  private SampleAdapter adapter;
  private ViewPager pager;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    pager=(ViewPager)findViewById(R.id.pager);
    adapter=new SampleAdapter(this, getFragmentManager());
    pager.setAdapter(adapter);

    MaterialTabs tabs=(MaterialTabs)findViewById(R.id.tabs);
    tabs.setViewPager(pager);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.actions, menu);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId()==R.id.fixed) {
      item.setChecked(!item.isChecked());

      if (item.isChecked()) {
        if (pager.getCurrentItem()>2) {
          pager.setCurrentItem(2);
        }

        pager.postDelayed(new Runnable() {
          @Override
          public void run() {
            adapter.setPageCount(3);
            adapter.notifyDataSetChanged();
          }
        }, 100);
      }
      else {
        adapter.setPageCount(10);
        adapter.notifyDataSetChanged();
      }

      return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  @Override
  public void onProvideAssistData(Bundle data) {
    super.onProvideAssistData(data);

    data.putInt("random-value", new SecureRandom().nextInt());
  }

  @TargetApi(23)
  @Override
  public void onProvideAssistContent(AssistContent outContent) {
    super.onProvideAssistContent(outContent);

    outContent.setWebUri(Uri.parse("https://commonsware.com"));

    try {
      JSONObject json=new JSONObject()
        .put("@type", "Book")
        .put("author", "https://commonsware.com/mmurphy")
        .put("publisher", "CommonsWare, LLC")
        .put("name", "The Busy Coder's Guide to Android Development");

      outContent.setStructuredData(json.toString());
    }
    catch (JSONException e) {
      Log.e(getClass().getSimpleName(),
        "Um, what happened here?", e);
    }
  }
}