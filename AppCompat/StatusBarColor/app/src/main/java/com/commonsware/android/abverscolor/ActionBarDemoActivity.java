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

package com.commonsware.android.abverscolor;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class ActionBarDemoActivity extends AppCompatActivity {
  private static final String[] items= { "lorem", "ipsum", "dolor",
      "sit", "amet", "consectetuer", "adipiscing", "elit", "morbi",
      "vel", "ligula", "vitae", "arcu", "aliquet", "mollis", "etiam",
      "vel", "erat", "placerat", "ante", "porttitor", "sodales",
      "pellentesque", "augue", "purus" };
  private ArrayList<String> words=null;
  private ArrayAdapter<String> adapter=null;

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);

    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
      Window window=getWindow();

      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      window.setStatusBarColor(getResources().getColor(R.color.primary_dark));
    }

    setContentView(R.layout.list_content_simple);
    initAdapter();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.actions, menu);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case R.id.add:
        addWord();

        return(true);

      case R.id.reset:
        initAdapter();

        return(true);

      case R.id.about:
        Toast.makeText(this, R.string.about_toast, Toast.LENGTH_LONG)
             .show();

        return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  private void initAdapter() {
    words=new ArrayList<String>();

    for (int i=0;i<5;i++) {
      words.add(items[i]);
    }

    adapter=
        new ArrayAdapter<String>(this,
                                 android.R.layout.simple_list_item_1,
                                 words);

    setListAdapter(adapter);
  }

  private void addWord() {
    if (adapter.getCount()<items.length) {
      adapter.add(items[adapter.getCount()]);
    }
  }

  private void setListAdapter(ListAdapter la) {
    ((ListView)findViewById(android.R.id.list)).setAdapter(la);
  }
}
