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

package com.commonsware.android.actionprog;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;


public class ActionBarDemoActivity extends SherlockListActivity {
  private static final String[] items= { "lorem", "ipsum", "dolor",
      "sit", "amet", "consectetuer", "adipiscing", "elit", "morbi",
      "vel", "ligula", "vitae", "arcu", "aliquet", "mollis", "etiam",
      "vel", "erat", "placerat", "ante", "porttitor", "sodales",
      "pellentesque", "augue", "purus" };
  private MenuItem refresh=null;

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);

    setListAdapter(new ArrayAdapter<String>(
                                            this,
                                            android.R.layout.simple_list_item_1,
                                            items));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getSupportMenuInflater().inflate(R.menu.actions, menu);

    refresh=menu.findItem(R.id.refresh);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.refresh) {
      refresh();

      return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  private void refresh() {
    refresh.setActionView(R.layout.refresh);

    getListView().postDelayed(new Runnable() {
      public void run() {
        refresh.setActionView(null);
      }
    }, 5000);
  }
}
