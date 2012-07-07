/***
  Copyright (c) 2012 CommonsWare, LLC
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
package com.commonsware.android.defdet;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class DefaultDetectorActivity extends Activity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    TextView status=(TextView)findViewById(R.id.status);
    PackageManager mgr=getPackageManager();
    Intent i=
        new Intent(Intent.ACTION_VIEW,
                   Uri.parse("http://commonsware.com"));
    ResolveInfo test=
        mgr.resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY);

    if (test == null) {
      status.setText(R.string.no_default);
    }
    else {
      if (getPackageName().equals(test.activityInfo.packageName)
          && getClass().getName().equals(test.activityInfo.name)) {
        status.setText(R.string.am_too_the_default);
      }
      else {
        status.setText(R.string.not_default);
        Log.d(getClass().getSimpleName(), test.activityInfo.name);
      }
    }
  }
}