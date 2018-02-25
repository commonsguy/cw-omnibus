/***
 Copyright (c) 2012-2015 CommonsWare, LLC
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

package com.commonsware.android.backup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class RestoreProgressActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.progress);

    if (savedInstanceState==null) {
      RestoreService.enqueueWork(this);
    }
  }

  @Override
  protected void onStart() {
    super.onStart();

    EventBus.getDefault().register(this);
  }

  @Override
  protected void onStop() {
    EventBus.getDefault().unregister(this);

    super.onStop();
  }

  @Subscribe(threadMode =ThreadMode.MAIN)
  public void onCompleted(RestoreService.RestoreCompletedEvent event) {
    startActivity(new Intent(this, MainActivity.class));
    finish();
  }

  @Subscribe(threadMode =ThreadMode.MAIN)
  public void onFailed(RestoreService.RestoreFailedEvent event) {
    Toast.makeText(this, R.string.msg_restore_failed,
      Toast.LENGTH_LONG).show();
    finish();
  }
}
