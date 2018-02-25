/***
  Copyright (c) 2013-2015 CommonsWare, LLC
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

package com.commonsware.android.okhttp;

import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QuestionsFragment extends ListFragment {
  public interface Contract {
    void onQuestion(Item question);
  }

  static final String SO_URL=
    "https://api.stackexchange.com/2.1/questions?pagesize=100&"
      + "order=desc&sort=creation&site=stackoverflow&tagged=android";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setRetainInstance(true);
  }

  @Override
  public void onViewCreated(final View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    OkHttpClient client=((MainActivity)getActivity()).getOkHttpClient();
    Request request=new Request.Builder().url(SO_URL).build();

    client.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        Log.e(getClass().getSimpleName(), "Exception loading JSON", e);
      }

      @Override
      public void onResponse(Call call, Response response)
        throws IOException {
        if (response.isSuccessful()) {
          Reader in=response.body().charStream();
          BufferedReader reader=new BufferedReader(in);
          final SOQuestions questions=
            new Gson().fromJson(reader, SOQuestions.class);

          reader.close();

          view.post(new Runnable() {
            @Override
            public void run() {
              setListAdapter(new ItemsAdapter(questions.items));
            }
          });
        }
        else {
          Log.e(getClass().getSimpleName(), response.toString());
        }
      }
    });
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    Item item=((ItemsAdapter)getListAdapter()).getItem(position);

    ((Contract)getActivity()).onQuestion(item);
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
