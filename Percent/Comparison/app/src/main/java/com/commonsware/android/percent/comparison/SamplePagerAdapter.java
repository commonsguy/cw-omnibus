/***
 * Copyright (c) 2012-15 CommonsWare, LLC
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not
 * use this file except in compliance with the License. You may
 * obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required
 * by applicable law or agreed to in writing, software
 * distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for
 * the specific
 * language governing permissions and limitations under the
 * License.
 * <p/>
 * From _The Busy Coder's Guide to Android Development_
 * https://commonsware.com/Android
 */

package com.commonsware.android.percent.comparison;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

public class SamplePagerAdapter extends FragmentPagerAdapter {
  private static final int[] TITLES={
    R.string.title_percent,
    R.string.title_percent_rl,
    R.string.title_weight,
    R.string.title_percent_grid,
    R.string.title_weight_grid
  };
  private static final String[] CLASSES={
    PercentListFragment.class.getCanonicalName(),
    PercentRelativeLayoutListFragment.class.getCanonicalName(),
    WeightsListFragment.class.getCanonicalName(),
    PercentGridFragment.class.getCanonicalName(),
    WeightsGridFragment.class.getCanonicalName()
  };
  private final Context ctxt;

  public SamplePagerAdapter(Context ctxt, FragmentManager mgr) {
    super(mgr);

    this.ctxt=ctxt;
  }

  @Override
  public int getCount() {
    return(5);
  }

  @Override
  public Fragment getItem(int position) {
    return(Fragment.instantiate(ctxt, CLASSES[position]));
  }

  @Override
  public String getPageTitle(int position) {
    return(ctxt.getString(TITLES[position]));
  }
}