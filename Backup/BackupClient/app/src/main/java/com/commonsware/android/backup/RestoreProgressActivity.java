/***
 Copyright (c) 2012-2015 CommonsWare, LLC
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

package com.commonsware.android.backup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import de.greenrobot.event.EventBus;

public class RestoreProgressActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.progress);

    if (savedInstanceState==null) {
      Intent i=
        new Intent(this, RestoreService.class)
          .setData(getIntent().getData());

      startService(i);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();

    EventBus.getDefault().register(this);
  }

  @Override
  protected void onPause() {
    EventBus.getDefault().unregister(this);

    super.onPause();
  }

  public void onEventMainThread(RestoreService.RestoreCompletedEvent event) {
    startActivity(new Intent(this, MainActivity.class));
    finish();
  }

  public void onEventMainThread(RestoreService.RestoreFailedEvent event) {
    Toast.makeText(this, R.string.msg_restore_failed,
      Toast.LENGTH_LONG).show();
    finish();
  }
}
