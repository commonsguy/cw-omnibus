/***
 Copyright (c) 2015 CommonsWare, LLC
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

package com.commonsware.android.autofill.logger;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.autofill.AutofillManager;
import android.widget.Toast;

public class MainActivity extends ListActivity {
  private static final int REQUEST_ID=1337;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    AutofillManager af=getSystemService(AutofillManager.class);

    if (af.isAutofillSupported()) {
      if (af.hasEnabledAutofillServices()) {
        Toast.makeText(this, R.string.msg_ready, Toast.LENGTH_LONG).show();
        finish();
      }
      else {
        Uri uri=Uri.parse("package:"+getPackageName());
        Intent i=new Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE, uri);

        startActivityForResult(i, REQUEST_ID);
      }
    }
    else {
      Toast.makeText(this, R.string.msg_not_supported, Toast.LENGTH_LONG).show();
      finish();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
                                  Intent data) {
    if (requestCode==REQUEST_ID) {
      if (resultCode==RESULT_OK) {
        Toast.makeText(this, R.string.msg_ready, Toast.LENGTH_LONG).show();
      }
      else {
        Toast.makeText(this, R.string.msg_reject, Toast.LENGTH_LONG).show();
      }
    }

    finish();
  }
}
