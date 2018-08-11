/***
  Copyright (c) 2008-2018 CommonsWare, LLC
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

package com.commonsware.android.recyclerview.sorted;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SortedFragment extends RecyclerViewFragment {
  private static final String[] ITEMS={"lorem", "ipsum", "dolor",
          "sit", "amet", "consectetuer", "adipiscing", "elit", "morbi", "vel",
          "ligula", "vitae", "arcu", "aliquet", "mollis",
          "etiam", "vel", "erat", "placerat", "ante",
          "porttitor", "sodales", "pellentesque", "augue", "purus"};
  private Disposable sub;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setRetainInstance(true);
  }

  @Override
  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    IconicAdapter adapter=new IconicAdapter();

    setAdapter(adapter);
    setLayoutManager(new LinearLayoutManager(getActivity()));

    ArrayList<String> wordsSoFar=new ArrayList<>();

    sub=Observable.fromArray(ITEMS)
      .zipWith(Observable.interval(400, TimeUnit.MILLISECONDS), (item, interval) -> item)
      .subscribeOn(Schedulers.newThread())
      .observeOn(AndroidSchedulers.mainThread())
      .map(word -> {
        wordsSoFar.add(word);

        Collections.sort(wordsSoFar);

        return new ArrayList<>(wordsSoFar);
      })
      .doOnComplete(() ->
        Toast.makeText(getActivity(), R.string.done, Toast.LENGTH_SHORT)
          .show())
      .subscribe(adapter::submitList);
  }

  @Override
  public void onDestroy() {
    if (sub!=null) {
      sub.dispose();
    }

    super.onDestroy();
  }

  class StringDiffCallback extends DiffUtil.ItemCallback<String> {
    @Override
    public boolean areItemsTheSame(String oldItem, String newItem) {
      return oldItem.equals(newItem);
    }

    @Override
    public boolean areContentsTheSame(String oldItem, String newItem) {
      return areItemsTheSame(oldItem, newItem);
    }
  }

  class IconicAdapter extends ListAdapter<String, RowController> {
    IconicAdapter() {
      super(new StringDiffCallback());
    }

    @NonNull
    @Override
    public RowController onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      return(new RowController(getActivity().getLayoutInflater()
                                .inflate(R.layout.row, parent, false)));
    }

    @Override
    public void onBindViewHolder(@NonNull RowController holder, int position) {
      holder.bindModel(getItem(position));
    }
  }
}
