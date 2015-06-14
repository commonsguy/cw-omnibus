/***
 Copyright (c) 2014 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 From _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.fts;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import de.greenrobot.event.EventBus;
import retrofit.RestAdapter;

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

  public void onEventBackgroundThread(SearchRequestedEvent event) {
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
      RestAdapter restAdapter=
          new RestAdapter.Builder().setEndpoint("https://api.stackexchange.com")
              .build();
      StackOverflowInterface so=
          restAdapter.create(StackOverflowInterface.class);

      SOQuestions questions=so.questions("android");

      try {
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
