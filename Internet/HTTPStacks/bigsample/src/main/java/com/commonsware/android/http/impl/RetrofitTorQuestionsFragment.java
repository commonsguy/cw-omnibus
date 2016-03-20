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
import com.commonsware.android.http.AbstractTorQuestionsFragment;
import com.commonsware.android.http.QuestionStrategy;
import com.commonsware.android.http.TorStatusStrategy;
import com.commonsware.android.http.model.SOQuestions;
import com.commonsware.android.http.model.TorStatus;
import com.commonsware.android.http.util.StackOverflowInterface;
import com.commonsware.android.http.util.TorStatusInterface;
import com.jakewharton.retrofit.Ok3Client;
import info.guardianproject.netcipher.okhttp3.StrongOkHttpClientBuilder;
import okhttp3.OkHttpClient;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RetrofitTorQuestionsFragment
  extends AbstractTorQuestionsFragment {
  private StrongOkHttpClientBuilder builder;

  @Override
  protected QuestionStrategy buildStrategy()
    throws Exception {
    return(new RetrofitQuestionStrategy(buildClient()));
  }

  @Override
  protected TorStatusStrategy buildStatusStrategy()
    throws Exception {
    return(new RetrofitTorStatusStrategy(buildClient()));
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

  public static class RetrofitQuestionStrategy implements QuestionStrategy {
    private final StrongOkHttpClientBuilder builder;

    RetrofitQuestionStrategy(StrongOkHttpClientBuilder builder) {
      this.builder=builder;
    }

    @Override
    public void load(final QuestionsCallback callback) {
      builder.build(
        new StrongBuilderCallbackBase<OkHttpClient>() {
          @Override
          public void onConnected(OkHttpClient client) {
            RestAdapter restAdapter=new RestAdapter.Builder()
              .setClient(new Ok3Client(client))
              .setEndpoint("https://api.stackexchange.com")
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
        });
    }
  }

  public static class RetrofitTorStatusStrategy
    implements TorStatusStrategy {
    private final StrongOkHttpClientBuilder builder;

    RetrofitTorStatusStrategy(StrongOkHttpClientBuilder builder) {
      this.builder=builder;
    }

    @Override
    public void checkStatus(final TorStatusCallback callback) {
      builder.build(
        new StrongBuilderCallbackBase<OkHttpClient>() {
          @Override
          public void onConnected(OkHttpClient client) {
            RestAdapter restAdapter=new RestAdapter.Builder()
              .setClient(new Ok3Client(client))
              .setEndpoint("https://check.torproject.org")
              .build();
            TorStatusInterface tor=
              restAdapter.create(TorStatusInterface.class);

            tor.status(new Callback<TorStatus>() {
              @Override
              public void success(TorStatus torStatus,
                                  Response response) {
                callback.onLoaded(torStatus);
              }

              @Override
              public void failure(RetrofitError error) {
                Log.e(getClass().getSimpleName(),
                  "Error trying to load Tor status", error);
              }
            });
          }
        });
    }
  }
}
