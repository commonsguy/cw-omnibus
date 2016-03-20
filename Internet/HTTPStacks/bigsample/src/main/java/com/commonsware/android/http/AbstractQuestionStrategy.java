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

import android.util.Log;
import com.commonsware.android.http.model.SOQuestions;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.Reader;

abstract public class AbstractQuestionStrategy
  implements QuestionStrategy {
  public interface Parser {
    void parse(Reader reader) throws IOException;
  }

  protected abstract void fetchQuestions(Parser parser)
    throws IOException;

  @Override
  public void load(QuestionsCallback callback) {
    new QuestionsHTTPLoadThread(this, callback).start();
  }

  static class QuestionsHTTPLoadThread extends Thread implements
    Parser {
    private final QuestionsCallback callback;
    private final AbstractQuestionStrategy strategy;

    QuestionsHTTPLoadThread(AbstractQuestionStrategy strategy,
                            QuestionsCallback callback) {
      this.strategy=strategy;
      this.callback=callback;
    }

    @Override
    public void run() {
      try {
        strategy.fetchQuestions(this);
      }
      catch (IOException e) {
        Log.e(getClass().getSimpleName(),
          "Exception loading questions", e);
      }
    }

    @Override
    public void parse(Reader reader) throws IOException {
      SOQuestions result=new Gson().fromJson(reader, SOQuestions.class);

      reader.close();
      callback.onLoaded(result);
    }
  }
}
