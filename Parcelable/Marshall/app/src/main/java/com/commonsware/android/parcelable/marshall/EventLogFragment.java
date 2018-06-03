/***
  Copyright (c) 2013 CommonsWare, LLC
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

package com.commonsware.android.parcelable.marshall;

import android.annotation.SuppressLint;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class EventLogFragment extends ListFragment {
  private EventLogAdapter adapter=null;

  @Override
  public void onViewCreated(View view, Bundle state) {
    super.onViewCreated(view, state);

    setRetainInstance(true);
    getListView().setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);

    if (adapter == null) {
      adapter=new EventLogAdapter();
    }

    setListAdapter(adapter);
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

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onRandomEvent(final RandomEvent event) {
    adapter.add(event);
  }

  class EventLogAdapter extends ArrayAdapter<RandomEvent> {
    DateFormat fmt=new SimpleDateFormat("HH:mm:ss", Locale.US);

    public EventLogAdapter() {
      super(getActivity(), android.R.layout.simple_list_item_1,
            new ArrayList<RandomEvent>());
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView row=
          (TextView)super.getView(position, convertView, parent);
      RandomEvent event=getItem(position);

      row.setText(String.format("%s = %x", fmt.format(event.when),
                                event.value));

      return(row);
    }
  }
}
