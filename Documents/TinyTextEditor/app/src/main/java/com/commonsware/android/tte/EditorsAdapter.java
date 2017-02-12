/***
 Copyright (c) 2016 CommonsWare, LLC
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

package com.commonsware.android.tte;

import android.app.FragmentManager;
import android.net.Uri;
import com.commonsware.cwac.pager.ArrayPagerAdapter;
import com.commonsware.cwac.pager.PageDescriptor;
import com.commonsware.cwac.pager.SimplePageDescriptor;
import java.util.ArrayList;

public class EditorsAdapter extends ArrayPagerAdapter<EditorFragment> {
  public EditorsAdapter(FragmentManager fm) {
    super(fm, new ArrayList<PageDescriptor>());
  }

  @Override
  protected EditorFragment createFragment(PageDescriptor desc) {
    Uri document=Uri.parse(desc.getFragmentTag());

    return(EditorFragment.newInstance(document));
  }

  void addDocument(Uri document) {
    add(new SimplePageDescriptor(document.toString(),
      document.getLastPathSegment()));
  }

  void updateTitle(Uri document, String title) {
    int position=getPositionForDocument(document);

    if (position>=0) {
      SimplePageDescriptor desc=
        (SimplePageDescriptor)getPageDescriptor(position);

      desc.setTitle(title);
    }
  }

  void remove(Uri document) {
    int position=getPositionForDocument(document);

    if (position>=0) {
      remove(position);
    }
  }

  int getPositionForDocument(Uri document) {
    return(getPositionForTag(document.toString()));
  }
}
