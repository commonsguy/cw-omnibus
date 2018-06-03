/***
  Copyright (c) 2013-2014 CommonsWare, LLC
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

package com.commonsware.android.hurl;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;
import org.greenrobot.eventbus.EventBus;

class LoadThread extends Thread {
  static final String SO_URL=
      "https://api.stackexchange.com/2.1/questions?"
          + "order=desc&sort=creation&site=stackoverflow&tagged=android";

  @Override
  public void run() {
    try {
      HttpURLConnection c=
          (HttpURLConnection)new URL(SO_URL).openConnection();

      try {
        InputStream in=c.getInputStream();
        BufferedReader reader=
            new BufferedReader(new InputStreamReader(in));
        SOQuestions questions=
            new Gson().fromJson(reader, SOQuestions.class);

        reader.close();
        
        EventBus.getDefault().post(new QuestionsLoadedEvent(questions));
      }
      catch (IOException e) {
        Log.e(getClass().getSimpleName(), "Exception parsing JSON", e);
      }
      finally {
        c.disconnect();
      }
    }
    catch (Exception e) {
      Log.e(getClass().getSimpleName(), "Exception parsing JSON", e);
    }
  }
}