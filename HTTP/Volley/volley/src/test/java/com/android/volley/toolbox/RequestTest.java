/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.volley.toolbox;

import com.android.volley.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class RequestTest {

    @Test
    public void publicMethods() throws Exception {
        // Catch-all test to find API-breaking changes.
        assertNotNull(Request.class.getConstructor(int.class, String.class,
                Response.ErrorListener.class));

        assertNotNull(Request.class.getMethod("getMethod"));
        assertNotNull(Request.class.getMethod("setTag", Object.class));
        assertNotNull(Request.class.getMethod("getTag"));
        assertNotNull(Request.class.getMethod("getErrorListener"));
        assertNotNull(Request.class.getMethod("getTrafficStatsTag"));
        assertNotNull(Request.class.getMethod("setRetryPolicy", RetryPolicy.class));
        assertNotNull(Request.class.getMethod("addMarker", String.class));
        assertNotNull(Request.class.getDeclaredMethod("finish", String.class));
        assertNotNull(Request.class.getMethod("setRequestQueue", RequestQueue.class));
        assertNotNull(Request.class.getMethod("setSequence", int.class));
        assertNotNull(Request.class.getMethod("getSequence"));
        assertNotNull(Request.class.getMethod("getUrl"));
        assertNotNull(Request.class.getMethod("getCacheKey"));
        assertNotNull(Request.class.getMethod("setCacheEntry", Cache.Entry.class));
        assertNotNull(Request.class.getMethod("getCacheEntry"));
        assertNotNull(Request.class.getMethod("cancel"));
        assertNotNull(Request.class.getMethod("isCanceled"));
        assertNotNull(Request.class.getMethod("getHeaders"));
        assertNotNull(Request.class.getDeclaredMethod("getParams"));
        assertNotNull(Request.class.getDeclaredMethod("getParamsEncoding"));
        assertNotNull(Request.class.getMethod("getBodyContentType"));
        assertNotNull(Request.class.getMethod("getBody"));
        assertNotNull(Request.class.getMethod("setShouldCache", boolean.class));
        assertNotNull(Request.class.getMethod("shouldCache"));
        assertNotNull(Request.class.getMethod("getPriority"));
        assertNotNull(Request.class.getMethod("getTimeoutMs"));
        assertNotNull(Request.class.getMethod("getRetryPolicy"));
        assertNotNull(Request.class.getMethod("markDelivered"));
        assertNotNull(Request.class.getMethod("hasHadResponseDelivered"));
        assertNotNull(Request.class.getDeclaredMethod("parseNetworkResponse", NetworkResponse.class));
        assertNotNull(Request.class.getDeclaredMethod("parseNetworkError", VolleyError.class));
        assertNotNull(Request.class.getDeclaredMethod("deliverResponse", Object.class));
        assertNotNull(Request.class.getMethod("deliverError", VolleyError.class));
    }
}
