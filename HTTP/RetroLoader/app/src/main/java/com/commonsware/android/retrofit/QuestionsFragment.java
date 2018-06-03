/***
  Copyright (c) 2013-2014 CommonsWare, LLC
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

package com.commonsware.android.retrofit;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;

public class QuestionsFragment extends ListFragment implements
    LoaderManager.LoaderCallbacks<SOQuestions> {
  public interface Contract {
    void onQuestion(Item question);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    getLoaderManager().initLoader(0, null, this);
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    Item item=((ItemsAdapter)getListAdapter()).getItem(position);

    ((Contract)getActivity()).onQuestion(item);
  }

  @Override
  public Loader<SOQuestions> onCreateLoader(int id, Bundle args) {
    return(new QuestionsLoader(getActivity()));
  }

  @Override
  public void onLoadFinished(Loader<SOQuestions> loader,
                             SOQuestions data) {
    setListAdapter(new ItemsAdapter(data.items));
  }

  @Override
  public void onLoaderReset(Loader<SOQuestions> loader) {
    setListAdapter(null);
  }

  class ItemsAdapter extends ArrayAdapter<Item> {
    ItemsAdapter(List<Item> items) {
      super(getActivity(), android.R.layout.simple_list_item_1, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View row=super.getView(position, convertView, parent);
      TextView title=row.findViewById(android.R.id.text1);

      title.setText(Html.fromHtml(getItem(position).title));

      return(row);
    }
  }
}
