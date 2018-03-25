/***
 Copyright (c) 2015-2018 CommonsWare, LLC
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

package com.commonsware.android.button;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.os.strictmode.Violation;
import org.acra.ACRA;
import org.acra.annotation.AcraCore;
import org.acra.annotation.AcraHttpSender;
import org.acra.data.StringFormat;

@AcraCore(
  buildConfigClass = BuildConfig.class,
  reportFormat=StringFormat.JSON
)
@AcraHttpSender(
  uri=BuildConfig.ACRA_URL,
  httpMethod=org.acra.sender.HttpSender.Method.PUT
)
public class ACRAApplication extends Application {
  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);

    if (BuildConfig.ACRA_INSTALL) {
      ACRA.init(this);
    }
  }

  @Override
  public void onCreate() {
    super.onCreate();

    new Handler().postAtFrontOfQueue(this::enableStrictMode);
  }

  private void enableStrictMode() {
    StrictMode.ThreadPolicy.Builder b=new StrictMode.ThreadPolicy.Builder()
      .detectNetwork()
      .penaltyListener(this::onThreadViolation, AsyncTask.THREAD_POOL_EXECUTOR);

    StrictMode.setThreadPolicy(b.build());
  }

  private void onThreadViolation(Violation violation) {
    ACRA.getErrorReporter().handleSilentException(violation);
  }
}
