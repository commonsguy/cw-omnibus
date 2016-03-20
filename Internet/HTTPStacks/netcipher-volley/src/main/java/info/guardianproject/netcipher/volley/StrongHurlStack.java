/*
 * Copyright 2012-2016 Nathan Freitas
 * Copyright 2015 str4d
 * Portions Copyright (c) 2016 CommonsWare, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.guardianproject.netcipher.volley;

import com.android.volley.toolbox.HurlStack;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import javax.net.ssl.SSLSocketFactory;

/**
 * Volley HurlStack subclass that adds in NetCipher protections.
 * It is simplest to create one through StrongVolleyQueueBuilder.
 */
public class StrongHurlStack extends HurlStack {
  private final Proxy proxy;

  StrongHurlStack(SSLSocketFactory sslSocketFactory, Proxy proxy) {
    super(null, sslSocketFactory);

    this.proxy=proxy;
  }

  @Override
  protected HttpURLConnection createConnection(URL url)
    throws IOException {
    HttpURLConnection result;

    if (proxy==null) {
      result=(HttpURLConnection)url.openConnection();
    }
    else {
      result=(HttpURLConnection)url.openConnection(proxy);
    }

    // following from original HurlStack
    // Workaround for the M release HttpURLConnection not observing the
    // HttpURLConnection.setFollowRedirects() property.
    // https://code.google.com/p/android/issues/detail?id=194495
    result.setInstanceFollowRedirects(HttpURLConnection.getFollowRedirects());

    return(result);
  }
}
