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

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Map;

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class ResponseTest {

    @Test
    public void publicMethods() throws Exception {
        // Catch-all test to find API-breaking changes.
        assertNotNull(Response.class.getMethod("success", Object.class, Cache.Entry.class));
        assertNotNull(Response.class.getMethod("error", VolleyError.class));
        assertNotNull(Response.class.getMethod("isSuccess"));

        assertNotNull(Response.Listener.class.getDeclaredMethod("onResponse", Object.class));

        assertNotNull(Response.ErrorListener.class.getDeclaredMethod("onErrorResponse",
                VolleyError.class));

        assertNotNull(NetworkResponse.class.getConstructor(int.class, byte[].class, Map.class,
                boolean.class, long.class));
        assertNotNull(NetworkResponse.class.getConstructor(int.class, byte[].class, Map.class,
                boolean.class));
        assertNotNull(NetworkResponse.class.getConstructor(byte[].class));
        assertNotNull(NetworkResponse.class.getConstructor(byte[].class, Map.class));
    }
}
