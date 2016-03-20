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
import java.io.IOException;
import java.io.StringReader;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.impl.conn.PoolingHttpClientConnectionManager;

public class HttpClientQuestionsFragment extends
  AbstractQuestionsFragment {
  @Override
  protected QuestionStrategy buildStrategy() {
    return(new HttpClientQuestionStrategy());
  }

  @Override
  public void onViewCreated(View view,
                            Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    buildStrategy().load(this);
  }

  public static class HttpClientQuestionStrategy
    extends AbstractQuestionStrategy {
    final private HttpClient client;

    HttpClientQuestionStrategy() {
      client=HttpClientBuilder.create()
        .setConnectionManager(
          new PoolingHttpClientConnectionManager())
        .build();
    }

    @Override
    protected void fetchQuestions(Parser parser)
      throws IOException {
      HttpGet get=new HttpGet(SO_URL);
      String result=client.execute(get, new BasicResponseHandler());

      parser.parse(new StringReader(result));
    }
  }
}
