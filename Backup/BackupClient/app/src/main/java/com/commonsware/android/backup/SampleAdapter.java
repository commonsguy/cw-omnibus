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

package com.commonsware.android.backup;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Environment;
import android.support.v13.app.FragmentPagerAdapter;
import java.io.File;

public class SampleAdapter extends FragmentPagerAdapter {
  private static final int[] TITLES={R.string.internal,
      R.string.external, R.string.pub};
  private static final int TAB_INTERNAL=0;
  private static final int TAB_EXTERNAL=1;
  private static final String FILENAME="test.txt";
  private final Context ctxt;

  public SampleAdapter(Context ctxt, FragmentManager mgr) {
    super(mgr);

    this.ctxt=ctxt;
  }

  @Override
  public int getCount() {
    return(3);
  }

  @Override
  public Fragment getItem(int position) {
    File fileToEdit;

    switch(position) {
      case TAB_INTERNAL:
        fileToEdit=new File(ctxt.getFilesDir(), FILENAME);
        break;

      case TAB_EXTERNAL:
        fileToEdit=new File(ctxt.getExternalFilesDir(null), FILENAME);
        break;

      default:
        fileToEdit=
            new File(Environment.
                  getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                FILENAME);
        break;
    }

    return(EditorFragment.newInstance(fileToEdit));
  }

  @Override
  public String getPageTitle(int position) {
    return(ctxt.getString(TITLES[position]));
  }
}