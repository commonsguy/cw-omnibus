/***
  Copyright (c) 2013-2016 CommonsWare, LLC
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

package com.commonsware.android.dyncode;

import android.app.ListFragment;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.commonsware.android.dyncode.api.Thing;
import com.commonsware.android.dyncode.api.ThingsLoadedEvent;
import java.util.List;
import de.greenrobot.event.EventBus;

public class ThingsFragment extends ListFragment
  implements ThingsLoaderThunk.Callback {
  ThingsLoaderThunk loader;

  interface Contract {
    void onThingClicked(Thing thing);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setRetainInstance(true);
    setHasOptionsMenu(true);

    try {
      loader=
        new ThingsLoaderThunk(getActivity(),
          BuildConfig.EXTENSION_URL,
          "com.commonsware.android.dyncode.impl.QuestionsLoader",
          this);
      loader.startAsyncLoad();
    }
    catch (Exception e) {
      Log.e(getClass().getSimpleName(), "Exception loading extension",
        e);
      Toast
        .makeText(getActivity(), "Exception loading extension",
          Toast.LENGTH_LONG)
        .show();
    }
  }

  @Override
  public void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onStop() {
    EventBus.getDefault().unregister(this);
    super.onStop();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.actions, menu);

    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId()==R.id.reset) {
      loader.reset();
      getActivity().finish();
      return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  @Override
  public void onListItemClick(ListView l, View v, int position,
                              long id) {
    Thing thing=((ThingsAdapter)getListAdapter()).getItem(position);

    ((Contract)getActivity()).onThingClicked(thing);
  }

  @Override
  public void onError(final String message, Exception e) {
    Log.e(getClass().getSimpleName(), message, e);

    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast
          .makeText(getActivity(), message, Toast.LENGTH_LONG)
          .show();
      }
    });
  }

  public void onEventMainThread(ThingsLoadedEvent event) {
    setListAdapter(new ThingsAdapter(event.loader.getThings()));
  }

  class ThingsAdapter extends ArrayAdapter<Thing> {
    ThingsAdapter(List<Thing> items) {
      super(getActivity(), android.R.layout.simple_list_item_1, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View row=super.getView(position, convertView, parent);
      TextView title=(TextView)row.findViewById(android.R.id.text1);

      title.setText(Html.fromHtml(getItem(position).getTitle()));

      return(row);
    }
  }
}
