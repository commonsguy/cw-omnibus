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

package com.commonsware.android.permreporter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PermissionTabAdapter extends FragmentPagerAdapter {
  private static final int[] TITLES={
      R.string.normal,
      R.string.dangerous,
      R.string.signature,
      R.string.other};
  private final Context ctxt;
  private final PermissionRoster roster;

  PermissionTabAdapter(Context ctxt, FragmentManager mgr,
                       PermissionRoster roster) {
    super(mgr);

    this.ctxt=ctxt;
    this.roster=roster;
  }

  @Override
  public int getCount() {
    return(4);
  }

  @Override
  public Fragment getItem(int position) {
    PermissionType type=PermissionType.forValue(position);

    return(PermissionListFragment.newInstance(roster.getListForType(type)));
  }

  @Override
  public String getPageTitle(int position) {
    return(ctxt.getString(TITLES[position]));
  }
}