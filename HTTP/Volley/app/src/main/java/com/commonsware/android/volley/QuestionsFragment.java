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

package com.commonsware.android.volley;

import android.app.ListFragment;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import java.util.List;
import de.greenrobot.event.EventBus;

public class QuestionsFragment extends ListFragment implements
  Response.Listener<SOQuestions>, Response.ErrorListener {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setRetainInstance(true);
  }

  @Override
  public void onViewCreated(View view,
                            Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    GsonRequest<SOQuestions> request=
      new GsonRequest<SOQuestions>(getString(R.string.url),
        SOQuestions.class, null, this, this);

    VolleyManager.get(getActivity()).enqueue(request);
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    Item item=((ItemsAdapter)getListAdapter()).getItem(position);

    EventBus.getDefault().post(new QuestionClickedEvent(item));
  }

  @Override
  public void onErrorResponse(VolleyError error) {
    Toast.makeText(getActivity(), error.getMessage(),
                   Toast.LENGTH_LONG).show();
    Log.e(getClass().getSimpleName(),
          "Exception from Volley request to StackOverflow", error);
  }

  @Override
  public void onResponse(SOQuestions questions) {
    setListAdapter(new ItemsAdapter(questions.items));
  }

  class ItemsAdapter extends ArrayAdapter<Item> {
    ItemsAdapter(List<Item> items) {
      super(getActivity(), R.layout.row, R.id.title, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View row=super.getView(position, convertView, parent);
      Item item=getItem(position);
      ImageView icon=(ImageView)row.findViewById(R.id.icon);

      VolleyManager
        .get(getActivity())
        .loadImage(item.owner.profileImage, icon,
          R.drawable.owner_placeholder,
          R.drawable.owner_error);

      TextView title=(TextView)row.findViewById(R.id.title);

      title.setText(Html.fromHtml(getItem(position).title));

      return(row);
    }
  }
}
