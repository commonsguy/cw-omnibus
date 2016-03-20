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

import android.content.Context;
import android.content.Intent;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import info.guardianproject.netcipher.hurl.StrongBuilderBase;

/**
 * Builds an HttpUrlConnection that connects via Tor through
 * Orbot.
 */
public class StrongVolleyQueueBuilder extends
  StrongBuilderBase<StrongVolleyQueueBuilder, RequestQueue> {
  /**
   * Creates a StrongVolleyQueueBuilder using the strongest set
   * of options for security. Use this if the strongest set of
   * options is what you want; otherwise, create a
   * builder via the constructor and configure it as you see fit.
   *
   * @param ctxt any Context will do
   * @return a configured StrongVolleyQueueBuilder
   * @throws Exception
   */
  static public StrongVolleyQueueBuilder forMaxSecurity(Context ctxt)
    throws Exception {
    return(new StrongVolleyQueueBuilder(ctxt)
      .withDefaultKeystore()
      .withBestProxy());
  }

  /**
   * Creates a builder instance.
   *
   * @param ctxt any Context will do; builder will hold onto
   *             Application context
   */
  public StrongVolleyQueueBuilder(Context ctxt) {
    super(ctxt);
  }

  /**
   * Copy constructor.
   *
   * @param original builder to clone
   */
  public StrongVolleyQueueBuilder(StrongVolleyQueueBuilder original) {
    super(original);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RequestQueue build(Intent status) {
    return(Volley.newRequestQueue(ctxt,
      new StrongHurlStack(buildSocketFactory(), buildProxy(status))));
  }
}