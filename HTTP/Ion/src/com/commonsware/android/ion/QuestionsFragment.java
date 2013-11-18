/***
  Copyright (c) 2013 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
 */

package com.commonsware.android.ion;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class QuestionsFragment extends
    ContractListFragment<QuestionsFragment.Contract> implements
    FutureCallback<JsonObject> {
  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    View result=
        super.onCreateView(inflater, container, savedInstanceState);

    setRetainInstance(true);

    Ion.with(getActivity(),
             "https://api.stackexchange.com/2.1/questions?"
                 + "order=desc&sort=creation&site=stackoverflow&"
                 + "tagged=android").asJsonObject().setCallback(this);

    return(result);
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    getContract().showItem(((ItemsAdapter)getListAdapter()).getItem(position));
  }

  @Override
  public void onCompleted(Exception e, JsonObject json) {
    if (e != null) {
      Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG)
           .show();
      Log.e(getClass().getSimpleName(),
            "Exception from Retrofit request to StackOverflow", e);
    }

    if (json != null) {
      JsonArray items=json.getAsJsonArray("items");
      ArrayList<JsonObject> normalized=new ArrayList<JsonObject>();

      for (int i=0; i < items.size(); i++) {
        normalized.add(items.get(i).getAsJsonObject());
      }

      setListAdapter(new ItemsAdapter(normalized));
    }
  }

  class ItemsAdapter extends ArrayAdapter<JsonObject> {
    int size;

    ItemsAdapter(List<JsonObject> items) {
      super(getActivity(), R.layout.row, R.id.title, items);

      size=
          getActivity().getResources()
                       .getDimensionPixelSize(R.dimen.icon);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View row=super.getView(position, convertView, parent);
      JsonObject item=getItem(position);
      ImageView icon=(ImageView)row.findViewById(R.id.icon);

      Ion.with(icon)
         .placeholder(R.drawable.owner_placeholder)
         .resize(size, size)
         .centerCrop()
         .error(R.drawable.owner_error)
         .load(item.getAsJsonObject("owner").get("profile_image")
                   .getAsString());

      TextView title=(TextView)row.findViewById(R.id.title);

      title.setText(Html.fromHtml(item.get("title").getAsString()));

      return(row);
    }
  }

  interface Contract {
    void showItem(JsonObject item);
  }
}
