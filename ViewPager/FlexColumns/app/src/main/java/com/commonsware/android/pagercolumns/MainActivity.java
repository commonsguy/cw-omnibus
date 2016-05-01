/***
  Copyright (c) 2012-2013 CommonsWare, LLC
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

package com.commonsware.android.pagercolumns;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.commonsware.cwac.pager.ArrayPagerAdapter;
import com.commonsware.cwac.pager.PageDescriptor;
import com.commonsware.cwac.pager.SimplePageDescriptor;
import java.util.ArrayList;

public class MainActivity extends Activity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    ViewPager pager=(ViewPager)findViewById(R.id.pager);

    if (pager == null) {
      if (getFragmentManager().findFragmentById(R.id.editor1) == null) {
        SamplePagerAdapter adapter=buildAdapter();
        FragmentTransaction ft=
            getFragmentManager().beginTransaction();

        populateColumn(getFragmentManager(), ft, adapter, 0,
                       R.id.editor1);
        populateColumn(getFragmentManager(), ft, adapter, 1,
                       R.id.editor2);
        populateColumn(getFragmentManager(), ft, adapter, 2,
                       R.id.editor3);
        ft.commit();
      }
    }
    else {
      SamplePagerAdapter adapter=buildAdapter();

      pager.setAdapter(adapter);
    }
  }

  private SamplePagerAdapter buildAdapter() {
    ArrayList<PageDescriptor> pages=new ArrayList<PageDescriptor>();

    for (int i=0; i < 3; i++) {
      pages.add(new SimplePageDescriptor(buildTag(i), buildTitle(i)));
    }

    return(new SamplePagerAdapter(getFragmentManager(), pages));
  }

  private String buildTag(int position) {
    return("editor" + String.valueOf(position));
  }

  private String buildTitle(int position) {
    return(String.format(getString(R.string.hint), position + 1));
  }

  private void populateColumn(FragmentManager fm,
                              FragmentTransaction ft,
                              SamplePagerAdapter adapter, int position,
                              int slot) {
    EditorFragment f=adapter.getExistingFragment(position);

    if (f == null) {
      f=adapter.createFragment(buildTitle(position));
    }
    else {
      fm.beginTransaction().remove(f).commit();
      fm.executePendingTransactions();
    }

    ft.add(slot, f, buildTag(position));
  }

  static class SamplePagerAdapter extends
      ArrayPagerAdapter<EditorFragment> {
    public SamplePagerAdapter(FragmentManager fragmentManager,
                              ArrayList<PageDescriptor> descriptors) {
      super(fragmentManager, descriptors);
    }

    @Override
    protected EditorFragment createFragment(PageDescriptor desc) {
      return(createFragment(desc.getTitle()));
    }

    EditorFragment createFragment(String title) {
      return(EditorFragment.newInstance(title));
    }
  }
}