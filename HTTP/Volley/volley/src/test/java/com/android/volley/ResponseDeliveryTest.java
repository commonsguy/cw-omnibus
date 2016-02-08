/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.android.volley;

import com.android.volley.mock.MockRequest;
import com.android.volley.utils.CacheTestUtils;
import com.android.volley.utils.ImmediateResponseDelivery;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class ResponseDeliveryTest {

    private ExecutorDelivery mDelivery;
    private MockRequest mRequest;
    private Response<byte[]> mSuccessResponse;

    @Before public void setUp() throws Exception {
        // Make the delivery just run its posted responses immediately.
        mDelivery = new ImmediateResponseDelivery();
        mRequest = new MockRequest();
        mRequest.setSequence(1);
        byte[] data = new byte[16];
        Cache.Entry cacheEntry = CacheTestUtils.makeRandomCacheEntry(data);
        mSuccessResponse = Response.success(data, cacheEntry);
    }

    @Test public void postResponseCallsDeliverResponse() {
        mDelivery.postResponse(mRequest, mSuccessResponse);
        assertTrue(mRequest.deliverResponse_called);
        assertFalse(mRequest.deliverError_called);
    }

    @Test public void postResponseSuppressesCanceled() {
        mRequest.cancel();
        mDelivery.postResponse(mRequest, mSuccessResponse);
        assertFalse(mRequest.deliverResponse_called);
        assertFalse(mRequest.deliverError_called);
    }

    @Test public void postErrorCallsDeliverError() {
        Response<byte[]> errorResponse = Response.error(new ServerError());

        mDelivery.postResponse(mRequest, errorResponse);
        assertTrue(mRequest.deliverError_called);
        assertFalse(mRequest.deliverResponse_called);
    }
}
