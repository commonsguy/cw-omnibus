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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import info.guardianproject.netcipher.hurl.StrongConnectionBuilder;

public class HURLTorQuestionsFragment
  extends AbstractTorQuestionsFragment {
  private StrongConnectionBuilder builder=null;

  @Override
  protected AbstractQuestionStrategy buildStrategy()
    throws Exception {
    return(new HURLTorQuestionStrategy(buildBuilder()));
  }

  @Override
  protected AbstractTorStatusStrategy buildStatusStrategy()
    throws Exception {
    return(new HURLTorStatusStrategy(buildBuilder()));
  }

  @Override
  protected void loadQuestions() {
    try {
      buildStrategy().load(this);
    }
    catch (Exception e) {
      Toast.makeText(getActivity(), "Exception loading questions",
        Toast.LENGTH_LONG).show();
      Log.e(getClass().getSimpleName(),
        "Exception loading questions", e);
    }
  }

  private StrongConnectionBuilder buildBuilder()
    throws Exception {
    if (builder==null) {
      builder=
        StrongConnectionBuilder.forMaxSecurity(getActivity());
    }

    return(builder);
  }

  public static class HURLTorQuestionStrategy
    extends AbstractQuestionStrategy {
    final StrongConnectionBuilder builder;

    HURLTorQuestionStrategy(StrongConnectionBuilder builder) {
      this.builder=builder;
    }

    @Override
    protected void fetchQuestions(final Parser parser)
      throws IOException {
      new StrongConnectionBuilder(builder)
        .connectTo(SO_URL)
        .build(new StrongBuilderCallbackBase<HttpURLConnection>() {
          @Override
          public void onConnected(HttpURLConnection c) {
            try {
              InputStream in=c.getInputStream();
              BufferedReader reader=
                new BufferedReader(new InputStreamReader(in));

              parser.parse(reader);
            }
            catch (IOException e) {
              onConnectionException(e);
            }
            finally {
              c.disconnect();
            }
          }
        });
    }
  }

  public static class HURLTorStatusStrategy
    extends AbstractTorStatusStrategy {
    final StrongConnectionBuilder builder;

    HURLTorStatusStrategy(StrongConnectionBuilder builder) {
      this.builder=builder;
    }

    @Override
    protected void fetchStatus(final Parser parser)
      throws IOException {
      new StrongConnectionBuilder(builder)
          .connectTo(TOR_CHECK_URL)
          .build(new StrongBuilderCallbackBase<HttpURLConnection>() {
              @Override
              public void onConnected(HttpURLConnection c) {
                try {
                  InputStream in=c.getInputStream();
                  BufferedReader reader=
                    new BufferedReader(new InputStreamReader(in));

                  parser.parse(reader);
                }
                catch (IOException e) {
                  e.printStackTrace();
                }
                finally {
                  c.disconnect();
                }
              }
            });
    }
  }
}
