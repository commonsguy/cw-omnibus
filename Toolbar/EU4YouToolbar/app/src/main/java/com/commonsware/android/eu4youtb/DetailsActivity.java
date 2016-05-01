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
    https://commonsware.com/Android
 */

package com.commonsware.android.eu4youtb;

import android.app.Activity;
import android.os.Bundle;

public class DetailsActivity extends Activity {
  public static final String EXTRA_COUNTRY=
      "com.commonsware.android.eu4you.EXTRA_COUNTRY";
  private Country c=null;
  private DetailsFragment details=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    details=(DetailsFragment)getFragmentManager()
                               .findFragmentById(android.R.id.content);

    if (details == null) {
      details=new DetailsFragment();

      getFragmentManager().beginTransaction()
                                 .add(android.R.id.content, details)
                                 .commit();
    }

    c=getIntent().getParcelableExtra(EXTRA_COUNTRY);
  }

  @Override
  public void onResume() {
    super.onResume();

    details.showCountry(c);
  }
}
