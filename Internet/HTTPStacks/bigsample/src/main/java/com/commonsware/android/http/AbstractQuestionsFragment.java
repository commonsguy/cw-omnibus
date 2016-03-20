/***
  Copyright (c) 2013-2016 CommonsWare, LLC
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

package com.commonsware.android.http;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.commonsware.android.http.model.Item;
import com.commonsware.android.http.model.SOQuestions;
import java.util.List;
import de.greenrobot.event.EventBus;

abstract public class AbstractQuestionsFragment
  extends ListFragment
  implements QuestionStrategy.QuestionsCallback {
  abstract protected QuestionStrategy buildStrategy()
    throws Exception;

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    Item item=((ItemsAdapter)getListAdapter()).getItem(position);

    EventBus.getDefault().post(new QuestionClickedEvent(item));
  }

  @Override
  public void onLoaded(final SOQuestions questions) {
    if (getActivity()!=null) {
      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          setListAdapter(new ItemsAdapter(questions.items));
        }
      });
    }
  }

  class ItemsAdapter extends ArrayAdapter<Item> {
    ItemsAdapter(List<Item> items) {
      super(getActivity(), android.R.layout.simple_list_item_1, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View row=super.getView(position, convertView, parent);
      TextView title=(TextView)row.findViewById(android.R.id.text1);

      title.setText(Html.fromHtml(getItem(position).title));

      return(row);
    }
  }
}
