package com.commonsware.empublite;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class ContentsAdapter extends FragmentStatePagerAdapter {
  public ContentsAdapter(SherlockFragmentActivity ctxt) {
    super(ctxt.getSupportFragmentManager());
  }

  @Override
  public Fragment getItem(int position) {
    return null;
  }

  @Override
  public int getCount() {
    return 0;
  }
}
