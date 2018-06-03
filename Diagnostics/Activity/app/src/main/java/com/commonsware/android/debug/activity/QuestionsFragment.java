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

package com.commonsware.android.debug.activity;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
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
import java.util.List;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuestionsFragment extends ListFragment implements
    Callback<SOQuestions> {
  interface Contract {
    void onQuestion(Item question);
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    View result=
        super.onCreateView(inflater, container, savedInstanceState);

    setRetainInstance(true);

    Retrofit retrofit=
      new Retrofit.Builder()
        .baseUrl("https://api.stackexchange.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build();
    StackOverflowInterface so=
      retrofit.create(StackOverflowInterface.class);

    so.questions("android").enqueue(this);

    return(result);
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    Item item=((ItemsAdapter)getListAdapter()).getItem(position);

    ((Contract)getActivity()).onQuestion(item);
  }

  @Override
  public void onResponse(Call<SOQuestions> call,
                         Response<SOQuestions> response) {
    setListAdapter(new ItemsAdapter(response.body().items));
  }

  @Override
  public void onFailure(Call<SOQuestions> call, Throwable t) {
    Toast.makeText(getActivity(), t.getMessage(),
      Toast.LENGTH_LONG).show();
    Log.e(getClass().getSimpleName(),
      "Exception from Retrofit request to StackOverflow", t);
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

      Picasso.with(getActivity()).load(item.owner.profileImage)
             .fit().centerCrop()
             .placeholder(R.drawable.owner_placeholder)
             .error(R.drawable.owner_error).into(icon);

      TextView title=(TextView)row.findViewById(R.id.title);

      title.setText(Html.fromHtml(getItem(position).title));

      return(row);
    }
  }
}
