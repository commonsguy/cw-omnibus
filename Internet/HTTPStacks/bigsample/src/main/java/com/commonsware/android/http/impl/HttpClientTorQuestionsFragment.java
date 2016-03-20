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

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.commonsware.android.http.AbstractQuestionStrategy;
import com.commonsware.android.http.AbstractTorQuestionsFragment;
import com.commonsware.android.http.AbstractTorStatusStrategy;
import java.io.IOException;
import java.io.StringReader;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import info.guardianproject.netcipher.httpclient.StrongHttpClientBuilder;

public class HttpClientTorQuestionsFragment extends
  AbstractTorQuestionsFragment {
  private StrongHttpClientBuilder builder;

  @Override
  protected AbstractQuestionStrategy buildStrategy() throws Exception {
    return(new HttpClientTorQuestionStrategy(
      buildClient(getActivity())));
  }

  @Override
  protected AbstractTorStatusStrategy buildStatusStrategy() throws Exception {
    return(new HttpClientTorStatusStrategy(
      buildClient(getActivity())));
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

  private StrongHttpClientBuilder buildClient(Context ctxt)
    throws Exception {
    if (builder==null) {
      builder=StrongHttpClientBuilder.forMaxSecurity(ctxt);
    }

    return(builder);
  }

  public static class HttpClientTorQuestionStrategy
    extends AbstractQuestionStrategy {
    final StrongHttpClientBuilder builder;

    HttpClientTorQuestionStrategy(StrongHttpClientBuilder builder) {
      this.builder=builder;
    }

    @Override
    protected void fetchQuestions(final Parser parser)
      throws IOException {
      builder.build(new StrongBuilderCallbackBase<HttpClient>() {
        @Override
        public void onConnected(HttpClient client) {
          HttpGet get=new HttpGet(SO_URL);

          try {
            String result=
              client.execute(get, new BasicResponseHandler());
            parser.parse(new StringReader(result));
          }
          catch (IOException e) {
            onConnectionException(e);
          }
        }
      });
    }
  }

  public static class HttpClientTorStatusStrategy
    extends AbstractTorStatusStrategy {
    final StrongHttpClientBuilder builder;

    HttpClientTorStatusStrategy(StrongHttpClientBuilder builder) {
      this.builder=builder;
    }

    @Override
    protected void fetchStatus(final Parser parser)
      throws IOException {
      builder.build(new StrongBuilderCallbackBase<HttpClient>() {
        @Override
        public void onConnected(HttpClient client) {
          HttpGet get=new HttpGet(TOR_CHECK_URL);

          try {
          String result=
            client.execute(get, new BasicResponseHandler());
          parser.parse(new StringReader(result));
          }
          catch (IOException e) {
            onConnectionException(e);
          }
        }
      });
    }
  }
}
