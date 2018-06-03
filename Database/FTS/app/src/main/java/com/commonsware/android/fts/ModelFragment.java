/***
 Copyright (c) 2014 CommonsWare, LLC
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

package com.commonsware.android.fts;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ModelFragment extends Fragment {
  private Context app=null;

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);

    setRetainInstance(true);
  }

  @Override
  public void onAttach(Activity host) {
    super.onAttach(host);

    EventBus.getDefault().register(this);

    if (app==null) {
      app=host.getApplicationContext();
      new FetchQuestionsThread().start();
    }
  }

  @Override
  public void onDetach() {
    EventBus.getDefault().unregister(this);

    super.onDetach();
  }

  @Subscribe(threadMode =ThreadMode.BACKGROUND)
  public void onSearchRequested(SearchRequestedEvent event) {
    try {
      Cursor results=DatabaseHelper.getInstance(app).loadQuestions(app, event.match);

      EventBus.getDefault().postSticky(new ModelLoadedEvent(results));
    }
    catch (Exception e) {
      Log.e(getClass().getSimpleName(),
          "Exception searching database", e);
    }
  }

  class FetchQuestionsThread extends Thread {
    @Override
    public void run() {
      Retrofit retrofit=
        new Retrofit.Builder()
          .baseUrl("https://api.stackexchange.com")
          .addConverterFactory(GsonConverterFactory.create())
          .build();
      StackOverflowInterface so=
        retrofit.create(StackOverflowInterface.class);

      try {
        SOQuestions questions=so.questions("android").execute().body();

        DatabaseHelper
            .getInstance(app)
            .insertQuestions(app, questions.items);
      }
      catch (Exception e) {
        Log.e(getClass().getSimpleName(),
            "Exception populating database", e);
      }

      try {
        Cursor results=DatabaseHelper.getInstance(app).loadQuestions(app, null);

        EventBus.getDefault().postSticky(new ModelLoadedEvent(results));
      }
      catch (Exception e) {
        Log.e(getClass().getSimpleName(),
            "Exception populating database", e);
      }
    }
  }
}
