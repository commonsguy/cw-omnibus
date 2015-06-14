/***
  Copyright (c) 2012 CommonsWare, LLC
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

package com.commonsware.android.anim.threepane;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import java.util.ArrayList;

public class MainActivity extends Activity {
  private static final String KEY_MIDDLE_CONTENTS="middleContents";
  private static final String[] items= { "lorem", "ipsum", "dolor",
      "sit", "amet", "consectetuer", "adipiscing", "elit", "morbi",
      "vel", "ligula", "vitae", "arcu", "aliquet", "mollis", "etiam",
      "vel", "erat", "placerat", "ante", "porttitor", "sodales",
      "pellentesque", "augue", "purus" };
  private boolean isLeftShowing=true;
  private SimpleListFragment middleFragment=null;
  private ArrayList<String> middleContents=null;
  private ThreePaneLayout root=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    root=(ThreePaneLayout)findViewById(R.id.root);

    if (getFragmentManager().findFragmentById(R.id.left) == null) {
      getFragmentManager().beginTransaction()
                          .add(R.id.left,
                               SimpleListFragment.newInstance(items))
                          .commit();
    }

    middleFragment=
        (SimpleListFragment)getFragmentManager().findFragmentById(R.id.middle);
  }

  @Override
  public void onBackPressed() {
    if (!isLeftShowing) {
      root.showLeft();
      isLeftShowing=true;
    }
    else {
      super.onBackPressed();
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    
    outState.putStringArrayList(KEY_MIDDLE_CONTENTS, middleContents);
  }
  
  @Override
  protected void onRestoreInstanceState(Bundle inState) {
    middleContents=inState.getStringArrayList(KEY_MIDDLE_CONTENTS);
  }
  
  void onListItemClick(SimpleListFragment fragment, int position) {
    if (fragment == middleFragment) {
      ((Button)root.getRightView()).setText(middleContents.get(position));

      if (isLeftShowing) {
        root.hideLeft();
        isLeftShowing=false;
      }
    }
    else {
      middleContents=new ArrayList<String>();

      for (int i=0; i < 20; i++) {
        middleContents.add(items[position] + " #" + i);
      }

      if (getFragmentManager().findFragmentById(R.id.middle) == null) {
        middleFragment=SimpleListFragment.newInstance(middleContents);
        getFragmentManager().beginTransaction()
                            .add(R.id.middle, middleFragment).commit();
      }
      else {
        middleFragment.setContents(middleContents);
      }
    }
  }
}
