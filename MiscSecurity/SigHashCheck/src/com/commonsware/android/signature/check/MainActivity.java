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
    http://commonsware.com/Android
 */

package com.commonsware.android.signature.check;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.commonsware.cwac.security.SignatureUtils;

public class MainActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    int msg=R.string.package_invalid;

    try {
      String hash=
          SignatureUtils.getSignatureHash(this, "com.android.settings");
      // String hash=
      // SignatureUtils.getOwnSignatureHash(this);

      if (getString(R.string.hash).equals(hash)) {
        msg=R.string.package_valid;
      }
    }
    catch (Exception e) {
      Log.e(getClass().getSimpleName(), "Exception in validation", e);
      msg=R.string.we_crashed;
    }

    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

    finish();
  }
}
