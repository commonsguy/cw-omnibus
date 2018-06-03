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

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
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
import org.xmlpull.v1.XmlPullParser;
import java.util.ArrayList;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RxDemoFragment extends Fragment {
  private ArrayList<String> model=new ArrayList<>();
  private RVArrayAdapter adapter;
  private Disposable sub=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    adapter=new RVArrayAdapter(model, getLayoutInflater());

    Observable<String> observable=Observable
      .create(new WordSource(getActivity()))
      .subscribeOn(Schedulers.io())
      .map(s -> (s.toUpperCase()))
      .observeOn(AndroidSchedulers.mainThread())
      .doOnComplete(() ->
        Toast.makeText(getActivity(), R.string.done, Toast.LENGTH_SHORT).show());

    sub=observable.subscribe(s -> adapter.add(s),
        error ->
          Toast
            .makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG)
            .show());
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
    if (sub!=null && !sub.isDisposed()) {
      sub.dispose();
    }

    super.onDestroy();
  }

  private static class WordSource implements ObservableOnSubscribe<String> {
    private final Resources resources;

    WordSource(Context ctxt) {
      resources=ctxt.getResources();
    }

    @Override
    public void subscribe(ObservableEmitter<String> emitter) {
      try {
        XmlPullParser xpp=resources.getXml(R.xml.words);

        while (xpp.getEventType()!=XmlPullParser.END_DOCUMENT) {
          if (xpp.getEventType()==XmlPullParser.START_TAG) {
            if (xpp.getName().equals("word")) {
              emitter.onNext(xpp.getAttributeValue(0));
            }
          }

          xpp.next();
        }

        emitter.onComplete();
      }
      catch (Exception e) {
        emitter.onError(e);
      }
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
