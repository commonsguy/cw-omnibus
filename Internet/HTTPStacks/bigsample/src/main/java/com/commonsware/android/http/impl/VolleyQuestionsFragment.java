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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.commonsware.android.http.AbstractQuestionsFragment;
import com.commonsware.android.http.QuestionStrategy;
import com.commonsware.android.http.model.SOQuestions;
import com.commonsware.android.http.util.GsonRequest;

public class VolleyQuestionsFragment extends
  AbstractQuestionsFragment {
  @Override
  protected QuestionStrategy buildStrategy() {
    return(new VolleyQuestionStrategy(getActivity()));
  }

  @Override
  public void onViewCreated(View view,
                            Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    buildStrategy().load(this);
  }

  public static class VolleyQuestionStrategy implements QuestionStrategy,
    Response.ErrorListener {
    private final RequestQueue queue;

    VolleyQuestionStrategy(Context ctxt) {
      queue=Volley.newRequestQueue(ctxt);
    }

    @Override
    public void load(final QuestionsCallback callback) {
      GsonRequest<SOQuestions> request=
        new GsonRequest<SOQuestions>(SO_URL,
          SOQuestions.class, null,
          new Response.Listener<SOQuestions>() {
            @Override
            public void onResponse(SOQuestions response) {
              callback.onLoaded(response);
            }
          }, this);

      queue.add(request);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
      Log.e(getClass().getSimpleName(),
        "Exception from Volley request", error);
    }
  }
}
