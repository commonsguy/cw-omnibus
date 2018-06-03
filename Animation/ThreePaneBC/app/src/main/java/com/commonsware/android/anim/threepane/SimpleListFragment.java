/***
  Copyright (c) 2012 CommonsWare, LLC
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

package com.commonsware.android.anim.threepane;

import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Arrays;

public class SimpleListFragment extends ListFragment {
  private static final String KEY_CONTENTS="contents";

  public static SimpleListFragment newInstance(String[] contents) {
    return(newInstance(new ArrayList<String>(Arrays.asList(contents))));
  }

  public static SimpleListFragment newInstance(ArrayList<String> contents) {
    SimpleListFragment result=new SimpleListFragment();
    Bundle args=new Bundle();

    args.putStringArrayList(KEY_CONTENTS, contents);
    result.setArguments(args);

    return(result);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    setContents(getArguments().getStringArrayList(KEY_CONTENTS));
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    ((MainActivity)getActivity()).onListItemClick(this, position);
  }

  void setContents(ArrayList<String> contents) {
    setListAdapter(new ArrayAdapter<String>(
                                            getActivity(),
                                            R.layout.simple_list_item_1,
                                            contents));
  }
}
