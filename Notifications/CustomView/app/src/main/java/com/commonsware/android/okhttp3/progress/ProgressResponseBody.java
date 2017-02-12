/***
 Copyright (c) 2008-2016 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.okhttp3.progress;

import java.io.IOException;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.ForwardingSource;
import okio.Source;

// inspired by https://github.com/square/okhttp/blob/master/samples/guide/src/main/java/okhttp3/recipes/Progress.java

class ProgressResponseBody extends ResponseBodyWrapper {
  private final Listener listener;

  ProgressResponseBody(ResponseBody wrapped, Listener listener) {
    super(wrapped);

    this.listener=listener;
  }

  @Override
  Source wrapSource(Source original) {
    return(new ProgressSource(original, listener));
  }

  class ProgressSource extends ForwardingSource {
    private final Listener listener;
    private long totalRead=0L;

    public ProgressSource(Source delegate, Listener listener) {
      super(delegate);

      this.listener=listener;
    }

    @Override
    public long read(Buffer sink, long byteCount)
      throws IOException {
      long bytesRead=super.read(sink, byteCount);
      boolean done=(bytesRead==-1);

      if (!done) {
        totalRead+=bytesRead;
      }

      listener.onProgressChange(totalRead,
        ProgressResponseBody.this.contentLength(), done);

      return(bytesRead);
    }
  }

  interface Listener {
    void onProgressChange(long bytesRead, long contentLength,
                          boolean done);
  }
}
