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

package com.commonsware.android.listnav;

import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class ListNavFragmentDemoActivity extends
    SherlockFragmentActivity implements OnNavigationListener {
  private static final String KEY_MODELS="models";
  private static final String KEY_POSITION="position";
  private static final String[] labels= { "Editor #1", "Editor #2",
      "Editor #3", "Editor #4", "Editor #5", "Editor #6", "Editor #7",
      "Editor #8", "Editor #9", "Editor #10" };
  private CharSequence[] models=new CharSequence[10];
  private EditorFragment frag=null;
  private int lastPosition=-1;

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);

    frag=
        (EditorFragment)getSupportFragmentManager().findFragmentById(android.R.id.content);
    
    if (frag==null) {
      frag=new EditorFragment();
      getSupportFragmentManager().beginTransaction()
                                 .add(android.R.id.content, frag)
                                 .commit();
    }

    if (state != null) {
      models=state.getCharSequenceArray(KEY_MODELS);
    }

    ArrayAdapter<String> nav=null;
    ActionBar bar=getSupportActionBar();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      nav=
          new ArrayAdapter<String>(
                                   bar.getThemedContext(),
                                   android.R.layout.simple_spinner_item,
                                   labels);
    }
    else {
      nav=
          new ArrayAdapter<String>(
                                   this,
                                   android.R.layout.simple_spinner_item,
                                   labels);
    }

    nav.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
    bar.setListNavigationCallbacks(nav, this);

    if (state != null) {
      bar.setSelectedNavigationItem(state.getInt(KEY_POSITION));
    }
  }

  @Override
  public void onSaveInstanceState(Bundle state) {
    if (lastPosition > -1) {
      models[lastPosition]=frag.getText();
    }

    state.putCharSequenceArray(KEY_MODELS, models);
    state.putInt(KEY_POSITION,
                 getSupportActionBar().getSelectedNavigationIndex());
  }

  @Override
  public boolean onNavigationItemSelected(int itemPosition, long itemId) {
    if (lastPosition > -1) {
      models[lastPosition]=frag.getText();
    }

    lastPosition=itemPosition;
    frag.setText(models[itemPosition]);
    frag.setHint(labels[itemPosition]);

    return(true);
  }
}