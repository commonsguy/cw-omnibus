/***
 Copyright (c) 2008-2012 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.abf;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import java.util.Collections;

public class ActionBarFragment extends ListFragment {
  private static final String[] items={"lorem", "ipsum", "dolor",
    "sit", "amet", "consectetuer", "adipiscing", "elit", "morbi",
    "vel", "ligula", "vitae", "arcu", "aliquet", "mollis", "etiam",
    "vel", "erat", "placerat", "ante", "porttitor", "sodales",
    "pellentesque", "augue", "purus"};
  private ArrayList<String> words=null;
  private ArrayAdapter<String> adapter=null;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setRetainInstance(true);
    setHasOptionsMenu(true);
  }

  @Override
  public void onViewCreated(@NonNull View view,
                            @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    if (adapter==null) {
      initAdapter();
    }
    else {
      setListAdapter(adapter);
    }
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.actions, menu);

    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId()==R.id.reset) {
      initAdapter();

      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void initAdapter() {
    words=new ArrayList<>();

    Collections.addAll(words, items);

    adapter=new ArrayAdapter<>(getActivity(),
      android.R.layout.simple_list_item_1, words);

    setListAdapter(adapter);
  }
}
