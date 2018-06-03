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

package com.commonsware.android.broadcast.fanout;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
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
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setRetainInstance(true);
  }

  @Override
  public void onViewCreated(@NonNull View view,
                            @Nullable Bundle savedInstanceState) {
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
  public void onBroadcast(final Intent event) {
    adapter.add(event);
  }

  class EventLogAdapter extends ArrayAdapter<Intent> {
    DateFormat fmt=new SimpleDateFormat("HH:mm:ss", Locale.US);

    EventLogAdapter() {
      super(getActivity(), android.R.layout.simple_list_item_1,
            new ArrayList<Intent>());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView row=(TextView)super.getView(position, convertView, parent);
      Intent event=getItem(position);

      row.setText(String.format("%s : %s",
        fmt.format(event.getLongExtra(TestReceiver.EXTRA_TIME, 0)),
        getEventLabel(event)));

      return(row);
    }
  }

  private String getEventLabel(Intent event) {
    if (event.getAction()==null) {
      return("explicit");
    }
    else if (event.getBooleanExtra(TestReceiver.EXTRA_IS_FANOUT, false)) {
      return("fanout");
    }

    return("implicit");
  }
}
