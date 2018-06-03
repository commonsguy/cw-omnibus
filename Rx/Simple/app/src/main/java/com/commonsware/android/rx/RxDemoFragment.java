/***
  Copyright (c) 2008-2016 CommonsWare, LLC
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

package com.commonsware.android.rx;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import io.reactivex.Observable;

public class RxDemoFragment extends Fragment {
  private static final String[] ITEMS= { "lorem", "ipsum", "dolor",
      "sit", "amet", "consectetuer", "adipiscing", "elit", "morbi",
      "vel", "ligula", "vitae", "arcu", "aliquet", "mollis", "etiam",
      "vel", "erat", "placerat", "ante", "porttitor", "sodales",
      "pellentesque", "augue", "purus" };
  private ArrayList<String> model=new ArrayList<>();
  private RVArrayAdapter adapter;
  private AddStringTask task;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setRetainInstance(true);

    adapter=new RVArrayAdapter(model, getLayoutInflater());
    task=new AddStringTask();
    task.execute();
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.main, container, false);
  }

  @Override
  public void onViewCreated(View v, Bundle savedInstanceState) {
    super.onViewCreated(v, savedInstanceState);

    RecyclerView rv=v.findViewById(android.R.id.list);

    rv.setLayoutManager(new LinearLayoutManager(getActivity()));
    rv.addItemDecoration(new DividerItemDecoration(getActivity(),
      DividerItemDecoration.VERTICAL));
    rv.setAdapter(adapter);
  }

  @Override
  public void onDestroy() {
    if (task!=null) {
      task.cancel(false);
    }

    super.onDestroy();
  }

  class AddStringTask extends AsyncTask<Void, String, Void> {
    @Override
    protected Void doInBackground(Void... unused) {
      Observable.fromArray(ITEMS)
        .subscribe(s -> {
          if (!isCancelled()) {
            publishProgress(s);
            SystemClock.sleep(400);
          }
        });

      return(null);
    }

    @Override
    protected void onProgressUpdate(String... item) {
      if (!isCancelled()) {
        adapter.add(item[0]);
      }
    }

    @Override
    protected void onPostExecute(Void unused) {
      Toast.makeText(getActivity(), R.string.done, Toast.LENGTH_SHORT)
           .show();

      task=null;
    }
  }

  private static class RVArrayAdapter extends RecyclerView.Adapter<RowHolder> {
    private final ArrayList<String> words;
    private final LayoutInflater inflater;

    private RVArrayAdapter(ArrayList<String> words,
                           LayoutInflater inflater) {
      this.words=words;
      this.inflater=inflater;
    }

    @NonNull
    @Override
    public RowHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                        int viewType) {
      View row=inflater.inflate(android.R.layout.simple_list_item_1, parent, false);

      return new RowHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull RowHolder holder,
                                 int position) {
      holder.bind(words.get(position));
    }

    @Override
    public int getItemCount() {
      return words.size();
    }

    private void add(String word) {
      words.add(word);
      notifyItemInserted(words.size()-1);
    }
  }

  private static class RowHolder extends RecyclerView.ViewHolder {
    private final TextView title;

    RowHolder(View itemView) {
      super(itemView);
      title=itemView.findViewById(android.R.id.text1);
    }

    public void bind(String text) {
      title.setText(text);
    }
  }
}
