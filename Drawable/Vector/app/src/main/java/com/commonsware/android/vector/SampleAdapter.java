/***
  Copyright (c) 2012-16 CommonsWare, LLC
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

package com.commonsware.android.vector;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

public class SampleAdapter extends FragmentPagerAdapter {
  private final Context ctxt;

  public SampleAdapter(Context ctxt, FragmentManager mgr) {
    super(mgr);

    this.ctxt=ctxt;
  }

  @Override
  public int getCount() {
    return(2);
  }

  @Override
  public Fragment getItem(int position) {
    if (position==0) {
      return(new VectorFragment());
    }

    return(new VectorCompatFragment());
  }

  @Override
  public String getPageTitle(int position) {
    if (position==0) {
      return(ctxt.getString(R.string.tab_native));
    }

    return(ctxt.getString(R.string.tab_compat));
  }
}
