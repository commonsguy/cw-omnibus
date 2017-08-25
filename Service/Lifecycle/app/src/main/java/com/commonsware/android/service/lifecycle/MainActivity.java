/***
  Copyright (c) 2012-2017 CommonsWare, LLC
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

package com.commonsware.android.service.lifecycle;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
  private MenuItem foreground, importantish;

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.actions, menu);
    foreground=menu.findItem(R.id.foreground);
    importantish=menu.findItem(R.id.importantish);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId()==R.id.run) {
      DemoService.startMeUp(this, foreground.isChecked(),
        importantish.isChecked());
      finish();

      return(true);
    }
    else if (item.getItemId()==R.id.foreground ) {
      item.setChecked(!item.isChecked());
      importantish.setEnabled(item.isChecked());

      return(true);
    }
    else if (item.getItemId()==R.id.importantish) {
      item.setChecked(!item.isChecked());

      return(true);
    }

    return super.onOptionsItemSelected(item);
  }
}