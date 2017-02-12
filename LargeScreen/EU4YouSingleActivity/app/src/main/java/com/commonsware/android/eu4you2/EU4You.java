/***
  Copyright (c) 2008-2012 CommonsWare, LLC
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

package com.commonsware.android.eu4you2;

import android.app.Activity;
import android.os.Bundle;

public class EU4You extends Activity implements
    CountriesFragment.Contract {
  private static final String TAG_COUNTRIES="countries";
  private static final String TAG_DETAILS="details";
  private CountriesFragment countries=null;
  private DetailsFragment details=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    countries=
        (CountriesFragment)getFragmentManager().findFragmentByTag(TAG_COUNTRIES);
    details=
        (DetailsFragment)getFragmentManager().findFragmentByTag(TAG_DETAILS);

    if (countries == null) {
      countries=new CountriesFragment();
      getFragmentManager().beginTransaction()
                                 .add(R.id.mainfrag, countries,
                                      TAG_COUNTRIES).commit();
    }

    if (details == null) {
      details=new DetailsFragment();

      if (findViewById(R.id.details) != null) {
        getFragmentManager().beginTransaction()
                                   .add(R.id.details, details,
                                        TAG_DETAILS).commit();
      }
    }
    else {
      if (details.getId() == R.id.mainfrag) {
        if (findViewById(R.id.details) != null) {
          getFragmentManager().popBackStackImmediate();
        }
      }
      else {
        getFragmentManager().beginTransaction().remove(details)
                                   .commit();
      }

      if (findViewById(R.id.details) != null) {
        getFragmentManager().beginTransaction()
                                   .add(R.id.details, details,
                                        TAG_DETAILS).commit();
      }
    }
  }

  @Override
  public void onCountrySelected(Country c) {
    String url=getString(c.url);

    details.loadUrl(url);

    if (details.getId() != R.id.details) {
      getFragmentManager().beginTransaction()
                                 .replace(R.id.mainfrag, details,
                                          TAG_DETAILS)
                                 .addToBackStack(null).commit();
    }
  }

  @Override
  public boolean isPersistentSelection() {
    return(details.isVisible());
  }
}
