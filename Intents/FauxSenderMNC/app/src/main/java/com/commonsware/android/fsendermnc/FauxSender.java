/***
  Copyright (c) 2008-2015 CommonsWare, LLC
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

package com.commonsware.android.fsendermnc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

public class FauxSender extends Activity {
  public static final String EXTRA_TARGET_ID="targetId";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    String epilogue="";

    super.onCreate(savedInstanceState);

    int targetId=getIntent().getIntExtra(EXTRA_TARGET_ID, -1);

    if (targetId>0) {
      epilogue=" for target ID #"+targetId;
    }

    String msg=getIntent().getStringExtra(Intent.EXTRA_TEXT);

    if (TextUtils.isEmpty(msg)) {
      msg=getIntent().getStringExtra(Intent.EXTRA_SUBJECT);
    }

    if (TextUtils.isEmpty(msg)) {
      msg=getString(R.string.no_message_supplied);
    }

    Toast.makeText(this, msg+epilogue, Toast.LENGTH_LONG).show();

    finish();
  }
}