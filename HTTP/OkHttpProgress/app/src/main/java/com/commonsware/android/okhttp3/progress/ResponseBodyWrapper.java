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

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

// inspired by https://github.com/square/okhttp/blob/master/samples/guide/src/main/java/okhttp3/recipes/Progress.java

abstract class ResponseBodyWrapper extends ResponseBody {
  abstract Source wrapSource(Source original);

  private final ResponseBody wrapped;
  private BufferedSource buffer;

  ResponseBodyWrapper(ResponseBody wrapped) {
    this.wrapped=wrapped;
  }

  @Override
  public MediaType contentType() {
    return(wrapped.contentType());
  }

  @Override
  public long contentLength() {
    return(wrapped.contentLength());
  }

  @Override
  public BufferedSource source() {
    if (buffer==null) {
      buffer=Okio.buffer(wrapSource(wrapped.source()));
    }

    return(buffer);
  }
}
