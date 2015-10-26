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
    https://commonsware.com/Android
 */

package com.commonsware.android.mapsv2.basic;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class LegalNoticesActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.legal);

    TextView legal=(TextView)findViewById(R.id.legal);

    legal.setText(GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(this));
  }
}
