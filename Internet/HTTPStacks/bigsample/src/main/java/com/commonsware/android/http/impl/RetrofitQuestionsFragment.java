/***
 Copyright (c) 2013-2016 CommonsWare, LLC
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

package com.commonsware.android.http.impl;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.commonsware.android.http.AbstractQuestionsFragment;
import com.commonsware.android.http.QuestionStrategy;
import com.commonsware.android.http.model.SOQuestions;
import com.commonsware.android.http.util.StackOverflowInterface;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RetrofitQuestionsFragment extends
  AbstractQuestionsFragment {
  @Override
  protected QuestionStrategy buildStrategy() {
    return(new RetrofitQuestionStrategy());
  }

  @Override
  public void onViewCreated(View view,
                            Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    buildStrategy().load(this);
  }

  public static class RetrofitQuestionStrategy implements QuestionStrategy {
    @Override
    public void load(final QuestionsCallback callback) {
      RestAdapter restAdapter=
        new RestAdapter.Builder().setEndpoint("https://api.stackexchange.com")
          .build();
      StackOverflowInterface so=
        restAdapter.create(StackOverflowInterface.class);

      so.questions(new Callback<SOQuestions>() {
        @Override
        public void success(SOQuestions soQuestions,
                            Response response) {
          callback.onLoaded(soQuestions);
        }

        @Override
        public void failure(RetrofitError error) {
          Log.e(getClass().getSimpleName(),
            "Error trying to load questions", error);
        }
      });
    }
  }
}
