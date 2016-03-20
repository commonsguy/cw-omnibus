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
import android.view.View;
import com.commonsware.android.http.AbstractQuestionStrategy;
import com.commonsware.android.http.AbstractQuestionsFragment;
import com.commonsware.android.http.QuestionStrategy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttp3QuestionsFragment extends
  AbstractQuestionsFragment {
  @Override
  protected QuestionStrategy buildStrategy() {
    return(new OkHttp3QuestionStrategy());
  }

  @Override
  public void onViewCreated(View view,
                            Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    buildStrategy().load(this);
  }

  public static class OkHttp3QuestionStrategy extends
    AbstractQuestionStrategy {
    @Override
    protected void fetchQuestions(Parser parser)
      throws IOException {
      OkHttpClient client=new OkHttpClient();
      Request request=new Request.Builder().url(SO_URL).build();
      Response response=client.newCall(request).execute();

      if (response.isSuccessful()) {
        Reader in=response.body().charStream();
        BufferedReader reader=new BufferedReader(in);

        parser.parse(reader);
      }
    }
  }
}
