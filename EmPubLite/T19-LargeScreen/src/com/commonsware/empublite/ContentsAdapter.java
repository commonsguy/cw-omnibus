package com.commonsware.empublite;

import android.app.Activity;
import android.app.Fragment;
import android.support.v13.app.FragmentStatePagerAdapter;

public class ContentsAdapter extends FragmentStatePagerAdapter {
  private BookContents contents=null;

  public ContentsAdapter(Activity ctxt, BookContents contents) {
    super(ctxt.getFragmentManager());

    this.contents=contents;
  }

  @Override
  public Fragment getItem(int position) {
    return(SimpleContentFragment.newInstance(contents.getChapterPath(position)));
  }

  @Override
  public int getCount() {
    return(contents.getChapterCount());
  }
}
