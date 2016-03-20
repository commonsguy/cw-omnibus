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
import com.commonsware.android.http.model.TorStatus;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.Reader;

abstract public class AbstractTorStatusStrategy
  implements TorStatusStrategy {
  public interface Parser {
    void parse(Reader reader) throws IOException;
  }

  protected abstract void fetchStatus(Parser parser)
    throws IOException;

  @Override
  public void checkStatus(TorStatusCallback callback) {
    new TorStatusLoadThread(this, callback).start();
  }

  static class TorStatusLoadThread extends Thread implements
    Parser {
    private final TorStatusCallback callback;
    private final AbstractTorStatusStrategy strategy;

    TorStatusLoadThread(AbstractTorStatusStrategy strategy,
                            TorStatusCallback callback) {
      this.strategy=strategy;
      this.callback=callback;
    }

    @Override
    public void run() {
      try {
        strategy.fetchStatus(this);
      }
      catch (IOException e) {
        Log.e(getClass().getSimpleName(),
          "Exception loading Tor status", e);
      }
    }

    @Override
    public void parse(Reader reader) throws IOException {
      TorStatus result=new Gson().fromJson(reader, TorStatus.class);

      reader.close();
      callback.onLoaded(result);
    }
  }
}
