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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.commonsware.android.http.AbstractTorQuestionsFragment;
import com.commonsware.android.http.QuestionStrategy;
import com.commonsware.android.http.TorStatusStrategy;
import com.commonsware.android.http.model.SOQuestions;
import com.commonsware.android.http.model.TorStatus;
import com.commonsware.android.http.util.GsonRequest;
import info.guardianproject.netcipher.volley.StrongVolleyQueueBuilder;

public class VolleyTorQuestionsFragment
  extends AbstractTorQuestionsFragment {
  private StrongVolleyQueueBuilder builder;

  @Override
  protected QuestionStrategy buildStrategy()
    throws Exception {
    return(new VolleyQuestionStrategy(buildQueue(getActivity())));
  }

  @Override
  protected TorStatusStrategy buildStatusStrategy() throws Exception {
    return(new VolleyTorStatusStrategy(buildQueue(getActivity())));
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

  StrongVolleyQueueBuilder buildQueue(Context ctxt)
    throws Exception {
    if (builder==null) {
      builder=StrongVolleyQueueBuilder.forMaxSecurity(ctxt);
    }

    return(builder);
  }

  public static class VolleyQuestionStrategy implements QuestionStrategy,
    Response.ErrorListener {
    private final StrongVolleyQueueBuilder builder;

    VolleyQuestionStrategy(StrongVolleyQueueBuilder builder) {
      this.builder=builder;
    }

    @Override
    public void load(final QuestionsCallback callback) {
      builder.build(
        new StrongBuilderCallbackBase<RequestQueue>() {
          @Override
          public void onConnected(RequestQueue queue) {
            GsonRequest<SOQuestions> request=
              new GsonRequest<SOQuestions>(SO_URL,
                SOQuestions.class, null,
                new Response.Listener<SOQuestions>() {
                  @Override
                  public void onResponse(SOQuestions response) {
                    callback.onLoaded(response);
                  }
                }, VolleyQuestionStrategy.this);

            queue.add(request);
          }
        });
    }

    @Override
    public void onErrorResponse(VolleyError error) {
      Log.e(getClass().getSimpleName(),
        "Exception from Volley request", error);
    }
  }

  public static class VolleyTorStatusStrategy
    implements TorStatusStrategy, Response.ErrorListener {
    private final StrongVolleyQueueBuilder builder;

    VolleyTorStatusStrategy(StrongVolleyQueueBuilder builder) {
      this.builder=builder;
    }

    @Override
    public void checkStatus(final TorStatusCallback callback) {
      builder.build(
        new StrongBuilderCallbackBase<RequestQueue>() {
          @Override
          public void onConnected(RequestQueue queue) {
            GsonRequest<TorStatus> request=
              new GsonRequest<TorStatus>(TOR_CHECK_URL,
                TorStatus.class, null,
                new Response.Listener<TorStatus>() {
                  @Override
                  public void onResponse(TorStatus response) {
                    callback.onLoaded(response);
                  }
                }, VolleyTorStatusStrategy.this);

            queue.add(request);
          }
        });
    }

    @Override
    public void onErrorResponse(VolleyError error) {
      Log.e(getClass().getSimpleName(),
        "Exception from Volley request", error);
    }
  }
}
