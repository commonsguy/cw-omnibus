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

package com.commonsware.android.http;

import android.app.ListActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.commonsware.android.http.model.Item;
import com.commonsware.android.http.model.SOQuestions;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import info.guardianproject.netcipher.client.StrongBuilder;
import info.guardianproject.netcipher.client.StrongOkHttpClientBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends ListActivity implements
  StrongBuilder.Callback<OkHttpClient> {
  String SO_URL=
    "https://api.stackexchange.com/2.1/questions?"
      + "order=desc&sort=creation&site=stackoverflow&tagged=android";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    try {
      StrongOkHttpClientBuilder
        .forMaxSecurity(this)
        .withTorValidation()
        .build(this);
    }
    catch (Exception e) {
      Toast
        .makeText(this, R.string.msg_crash, Toast.LENGTH_LONG)
        .show();
      Log.e(getClass().getSimpleName(),
        "Exception loading SO questions", e);
      finish();
    }
  }

  @Override
  public void onConnected(final OkHttpClient client) {
    new Thread() {
      @Override
      public void run() {
        try {
          Request request=new Request.Builder().url(SO_URL).build();
          Response response=client.newCall(request).execute();

          final SOQuestions result=
            new Gson().fromJson(response.body().charStream(), SOQuestions.class);

          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              setListAdapter(new ItemsAdapter(result.items));
            }
          });
        }
        catch (IOException e) {
          onConnectionException(e);
        }
      }
    }.start();
  }

  @Override
  public void onConnectionException(Exception e) {
    Log.e(getClass().getSimpleName(),
      "Exception loading SO questions", e);

    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast
          .makeText(MainActivity.this, R.string.msg_crash,
            Toast.LENGTH_LONG)
          .show();
        finish();
      }
    });
  }

  @Override
  public void onTimeout() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast
          .makeText(MainActivity.this, R.string.msg_timeout,
            Toast.LENGTH_LONG)
          .show();
        finish();
      }
    });
  }

  @Override
  public void onInvalid() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast
          .makeText(MainActivity.this, R.string.msg_invalid,
            Toast.LENGTH_LONG)
          .show();
        finish();
      }
    });
  }

  class ItemsAdapter extends ArrayAdapter<Item> {
    ItemsAdapter(List<Item> items) {
      super(MainActivity.this,
        android.R.layout.simple_list_item_1, items);
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
