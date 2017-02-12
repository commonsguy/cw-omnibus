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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.commonsware.android.http.model.Item;
import com.commonsware.android.http.model.SOQuestions;
import com.google.gson.Gson;
import java.io.StringReader;
import java.util.List;
import info.guardianproject.netcipher.client.StrongBuilder;
import info.guardianproject.netcipher.client.StrongVolleyQueueBuilder;

public class MainActivity extends ListActivity implements
  StrongBuilder.Callback<RequestQueue> {
  String SO_URL=
    "https://api.stackexchange.com/2.1/questions?"
      + "order=desc&sort=creation&site=stackoverflow&tagged=android";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    try {
      StrongVolleyQueueBuilder
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
  public void onConnected(final RequestQueue rq) {
    new Thread() {
      @Override
      public void run() {
        final StringRequest stringRequest=
          new StringRequest(StringRequest.Method.GET, SO_URL,
            new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {
                final SOQuestions result=
                  new Gson().fromJson(new StringReader(response),
                    SOQuestions.class);

                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    setListAdapter(new ItemsAdapter(result.items));
                  }
                });
              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                Log.e(getClass().getSimpleName(),
                  "Exception making Volley request", error);
              }
            });

        rq.add(stringRequest);
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
