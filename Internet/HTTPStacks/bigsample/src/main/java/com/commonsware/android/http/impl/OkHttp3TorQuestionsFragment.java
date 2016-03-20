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

import android.util.Log;
import android.widget.Toast;
import com.commonsware.android.http.AbstractQuestionStrategy;
import com.commonsware.android.http.AbstractTorQuestionsFragment;
import com.commonsware.android.http.AbstractTorStatusStrategy;
import com.commonsware.android.http.QuestionStrategy;
import com.commonsware.android.http.TorStatusStrategy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import info.guardianproject.netcipher.okhttp3.StrongOkHttpClientBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttp3TorQuestionsFragment
  extends AbstractTorQuestionsFragment {
  private StrongOkHttpClientBuilder builder;

  @Override
  protected QuestionStrategy buildStrategy()
    throws Exception {
    return(new OkHttp3QuestionStrategy(buildClient()));
  }

  @Override
  protected TorStatusStrategy buildStatusStrategy()
    throws Exception {
    return(new OkHttp3TorStatusStrategy(buildClient()));
  }

  @Override
  protected void loadQuestions() {
    try {
      buildStrategy().load(this);
    }
    catch (Exception e) {
      Toast.makeText(getActivity(),
        "Exception loading questions",
        Toast.LENGTH_LONG).show();
      Log.e(getClass().getSimpleName(),
        "Exception loading questions", e);
    }
  }

  StrongOkHttpClientBuilder buildClient()
    throws Exception {
    if (builder==null) {
      builder=StrongOkHttpClientBuilder
        .forMaxSecurity(getActivity());
    }

    return(builder);
  }

  public static class OkHttp3QuestionStrategy extends
    AbstractQuestionStrategy {
    final StrongOkHttpClientBuilder builder;

    OkHttp3QuestionStrategy(StrongOkHttpClientBuilder builder) {
      this.builder=builder;
    }

    @Override
    protected void fetchQuestions(final Parser parser)
      throws IOException {
      builder.build(
        new StrongBuilderCallbackBase<OkHttpClient>() {
          @Override
          public void onConnected(OkHttpClient client) {
            Request request=new Request.Builder().url(SO_URL).build();

            try {
              Response response=
                client.newCall(request).execute();

              if (response.isSuccessful()) {
                Reader in=response.body().charStream();
                BufferedReader reader=new BufferedReader(in);

                parser.parse(reader);
              }
            }
            catch (IOException e) {
              onConnectionException(e);
            }
          }
        });
    }
  }

  public static class OkHttp3TorStatusStrategy
    extends AbstractTorStatusStrategy {
    final StrongOkHttpClientBuilder builder;

    OkHttp3TorStatusStrategy(StrongOkHttpClientBuilder builder) {
      this.builder=builder;
    }

    @Override
    protected void fetchStatus(final Parser parser)
      throws IOException {
      builder.build(
        new StrongBuilderCallbackBase<OkHttpClient>() {
          @Override
          public void onConnected(OkHttpClient client) {
            try {
              Request request=new Request.Builder().url(TOR_CHECK_URL).build();
              Response response=client.newCall(request).execute();

              if (response.isSuccessful()) {
                Reader in=response.body().charStream();
                BufferedReader reader=new BufferedReader(in);

                parser.parse(reader);
              }
            }
            catch (IOException e) {
              onConnectionException(e);
            }
          }
        });
    }
  }
}
