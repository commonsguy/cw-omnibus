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

package com.commonsware.android.http.tests;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import info.guardianproject.netcipher.httpclient.StrongHttpClientBuilder;
import info.guardianproject.netcipher.hurl.OrbotInitializer;
import info.guardianproject.netcipher.hurl.StrongBuilder;
import info.guardianproject.netcipher.hurl.StrongConnectionBuilder;
import info.guardianproject.netcipher.okhttp3.StrongOkHttpClientBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;

@RunWith(AndroidJUnit4.class)
public class BuilderTests {
  private static final String TEST_URL=
    "https://wares.commonsware.com/test.json";
  private static final String EXPECTED="{\"Hello\": \"world\"}";
  private CountDownLatch responseLatch;
  private Exception innerException=null;
  private String testResult=null;

  @BeforeClass
  public static void initOrbot() {
    Context ctxt=InstrumentationRegistry.getTargetContext();

    OrbotInitializer.get(ctxt).init();
  }

  @Before
  public void setUp() {
    responseLatch=new CountDownLatch(1);
  }

  @Test
  public void testDefaultHURL() throws IOException {
    testHURL(
      (HttpURLConnection)new URL(TEST_URL).openConnection());
  }

  @Test
  public void testStrongConnectionBuilder()
    throws Exception {
    Context ctxt=InstrumentationRegistry.getTargetContext();
    StrongConnectionBuilder builder=
      StrongConnectionBuilder.forMaxSecurity(ctxt);

    testStrongBuilder(builder.connectTo(TEST_URL),
      new TestBuilderCallback<HttpURLConnection>() {
        @Override
        protected void loadResult(HttpURLConnection c)
          throws Exception {
          try {
            testResult=slurp(c.getInputStream());
          }
          finally {
            c.disconnect();
          }
        }
      });
  }

  @Test
  public void testStrongHttpClientBuilder()
    throws Exception {
    Context ctxt=InstrumentationRegistry.getTargetContext();
    StrongHttpClientBuilder builder=
      StrongHttpClientBuilder.forMaxSecurity(ctxt);

    testStrongBuilder(builder,
      new TestBuilderCallback<HttpClient>() {
        @Override
        protected void loadResult(HttpClient client)
          throws Exception {
          HttpGet get=new HttpGet(TEST_URL);
          testResult=
            client.execute(get, new BasicResponseHandler());
        }
      });
  }

  @Test
  public void testStrongOkHttpClientBuilder()
    throws Exception {
    Context ctxt=InstrumentationRegistry.getTargetContext();
    StrongOkHttpClientBuilder builder=
      StrongOkHttpClientBuilder.forMaxSecurity(ctxt);

    testStrongBuilder(builder,
      new TestBuilderCallback<OkHttpClient>() {
        @Override
        protected void loadResult(OkHttpClient client)
          throws Exception {
          Request request=new Request.Builder().url(TEST_URL).build();

          testResult=client.newCall(request).execute().body().string();
        }
      });
  }

  private void testHURL(HttpURLConnection c) throws IOException {
    try {
      String result=slurp(c.getInputStream());

      Assert.assertEquals(EXPECTED, result);
    }
    finally {
      c.disconnect();
    }
  }

  // based on http://stackoverflow.com/a/309718/115145

  public static String slurp(final InputStream is)
    throws IOException {
    final char[] buffer = new char[128];
    final StringBuilder out = new StringBuilder();
    final Reader in = new InputStreamReader(is, "UTF-8");

    for (;;) {
      int rsz = in.read(buffer, 0, buffer.length);
      if (rsz < 0)
        break;
      out.append(buffer, 0, rsz);
    }

    return out.toString();
  }

  private void testStrongBuilder(StrongBuilder builder,
                                 TestBuilderCallback callback)
    throws Exception {
    builder.build(callback);

    if (innerException!=null) {
      throw innerException;
    }

    Assert.assertEquals(EXPECTED, testResult);
  }

  private abstract class TestBuilderCallback<C>
    implements StrongBuilder.Callback<C> {

    abstract protected void loadResult(C connection)
      throws Exception;

    @Override
    public void onConnected(C connection) {
      try {
        loadResult(connection);
        responseLatch.countDown();
      }
      catch (Exception e) {
        innerException=e;
        responseLatch.countDown();
      }
    }

    @Override
    public void onConnectionException(IOException e) {
      innerException=e;
      responseLatch.countDown();
    }

    @Override
    public void onTimeout() {
      responseLatch.countDown();
    }
  }
}
