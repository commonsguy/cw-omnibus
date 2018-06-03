/***
 Copyright (c) 2016 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.retrofit;

import android.content.Context;
import android.os.OperationCanceledException;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import java.io.IOException;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuestionsLoader extends AsyncTaskLoader<SOQuestions> {
  final private StackOverflowInterface so;
  private SOQuestions lastResult;

  public QuestionsLoader(Context context) {
    super(context);

    Retrofit retrofit=
      new Retrofit.Builder()
        .baseUrl("https://api.stackexchange.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build();
    so=retrofit.create(StackOverflowInterface.class);
  }

  @Override
  protected void onStartLoading() {
    super.onStartLoading();

    if (lastResult!=null) {
      deliverResult(lastResult);
    }
    else {
      forceLoad();
    }
  }

  @Override
  synchronized public SOQuestions loadInBackground() {
    if (isLoadInBackgroundCanceled()) {
      throw new OperationCanceledException();
    }

    try {
      return(so.questions("android").execute().body());
    }
    catch (IOException e) {
      Log.e(getClass().getSimpleName(), "Exception loading questions", e);
      throw new OperationCanceledException();
    }
  }

  @Override
  public void deliverResult(SOQuestions data) {
    if (isReset()) {
      // actual cleanup, if any
    }

    lastResult=data;

    if (isStarted()) {
      super.deliverResult(data);
    }
  }

  @Override
  protected void onStopLoading() {
    super.onStopLoading();

    cancelLoad();
  }

  @Override
  protected void onReset() {
    super.onReset();

    onStopLoading();
    // plus any actual cleanup
  }
}
