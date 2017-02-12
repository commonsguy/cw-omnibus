/***
 Copyright (c) 2015 CommonsWare, LLC
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

package com.commonsware.android.tasks.roundrobin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class SecondActivity extends Activity implements View.OnClickListener {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.second);
    findViewById(R.id.button).setOnClickListener(this);

    Log.d(getClass().getSimpleName(),
        String.format("onCreate for %x", hashCode()));
  }

  @Override
  protected void onResume() {
    super.onResume();

    Log.d(getClass().getSimpleName(),
        String.format("onResume for %x", hashCode()));
  }

  @Override
  protected void onDestroy() {
    Log.d(getClass().getSimpleName(),
        String.format("onDestroy for %x", hashCode()));

    super.onDestroy();
  }

  @Override
  public void onClick(View view) {
    startActivity(new Intent(this, FirstActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
  }
}
