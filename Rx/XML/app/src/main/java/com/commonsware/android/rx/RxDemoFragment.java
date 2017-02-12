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

import android.app.ListFragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParser;
import java.util.ArrayList;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RxDemoFragment extends ListFragment {
  private ArrayList<String> model=new ArrayList<>();
  private ArrayAdapter<String> adapter;
  private Disposable sub=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    adapter=new ArrayAdapter<>(getActivity(),
      android.R.layout.simple_list_item_1, model);

    Observable<String> observable=Observable
      .create(new WordSource(getActivity()))
      .subscribeOn(Schedulers.io())
      .map(s -> (s.toUpperCase()))
      .observeOn(AndroidSchedulers.mainThread())
      .doOnComplete(() -> {
        Toast.makeText(getActivity(), R.string.done, Toast.LENGTH_SHORT)
          .show();
      });

    sub=observable.subscribe(s -> adapter.add(s),
        error ->
          Toast
            .makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG)
            .show());
  }

  @Override
  public void onViewCreated(View v, Bundle savedInstanceState) {
    super.onViewCreated(v, savedInstanceState);

    getListView().setScrollbarFadingEnabled(false);
    setListAdapter(adapter);
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
}
