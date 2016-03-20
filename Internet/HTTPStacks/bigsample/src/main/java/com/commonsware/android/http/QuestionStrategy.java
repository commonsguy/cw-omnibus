/***
 Copyright (c) 2016 CommonsWare, LLC
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

package com.commonsware.android.http;

import com.commonsware.android.http.model.SOQuestions;

public interface QuestionStrategy {
  interface QuestionsCallback {
    void onLoaded(SOQuestions questions);
  }

  String SO_URL=
    "https://api.stackexchange.com/2.1/questions?"
      + "order=desc&sort=creation&site=stackoverflow&tagged=android";

  void load(QuestionsCallback callback);
}
