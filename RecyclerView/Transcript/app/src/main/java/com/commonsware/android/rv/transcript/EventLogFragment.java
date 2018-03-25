/***
  Copyright (c) 2013-2017 CommonsWare, LLC
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

package com.commonsware.android.rv.transcript;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class EventLogFragment extends Fragment {
  private EventLogAdapter adapter=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setRetainInstance(true);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return(inflater.inflate(R.layout.main, container, false));
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    if (adapter==null) {
      adapter=new EventLogAdapter();
    }

    RecyclerView transcript=(RecyclerView)view.findViewById(R.id.transcript);

    transcript.setAdapter(adapter);
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

  class EventLogAdapter extends RecyclerView.Adapter<RowHolder> {
    private final ArrayList<RandomEvent> events=new ArrayList<>();

    @Override
    public RowHolder onCreateViewHolder(ViewGroup parent,
                                        int viewType) {
      View row=
        getActivity()
          .getLayoutInflater()
          .inflate(android.R.layout.simple_list_item_1, parent, false);

      return(new RowHolder(row));
    }

    @Override
    public void onBindViewHolder(RowHolder holder,
                                 int position) {
      holder.bind(events.get(position));
    }

    @Override
    public int getItemCount() {
      return(events.size());
    }

    void add(RandomEvent event) {
      events.add(event);
      notifyItemInserted(getItemCount());
    }
  }

  static class RowHolder extends RecyclerView.ViewHolder {
    private static final DateFormat fmt=
      new SimpleDateFormat("HH:mm:ss", Locale.US);
    private final TextView tv;

    RowHolder(View itemView) {
      super(itemView);

      tv=(TextView)itemView.findViewById(android.R.id.text1);
    }

    void bind(RandomEvent event) {
      tv.setText(String.format("%s = %x", fmt.format(event.when),
        event.value));
    }
  }
}
