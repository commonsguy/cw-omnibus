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
public class RequestQueueTest {

    @Test
    public void publicMethods() throws Exception {
        // Catch-all test to find API-breaking changes.
        assertNotNull(RequestQueue.class.getConstructor(Cache.class, Network.class, int.class,
                ResponseDelivery.class));
        assertNotNull(RequestQueue.class.getConstructor(Cache.class, Network.class, int.class));
        assertNotNull(RequestQueue.class.getConstructor(Cache.class, Network.class));

        assertNotNull(RequestQueue.class.getMethod("start"));
        assertNotNull(RequestQueue.class.getMethod("stop"));
        assertNotNull(RequestQueue.class.getMethod("getSequenceNumber"));
        assertNotNull(RequestQueue.class.getMethod("getCache"));
        assertNotNull(RequestQueue.class.getMethod("cancelAll", RequestQueue.RequestFilter.class));
        assertNotNull(RequestQueue.class.getMethod("cancelAll", Object.class));
        assertNotNull(RequestQueue.class.getMethod("add", Request.class));
        assertNotNull(RequestQueue.class.getDeclaredMethod("finish", Request.class));
    }
}
