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

import com.android.volley.mock.MockCache;
import com.android.volley.mock.MockNetwork;
import com.android.volley.mock.MockRequest;
import com.android.volley.mock.MockResponseDelivery;
import com.android.volley.mock.WaitableQueue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class NetworkDispatcherTest {
    private NetworkDispatcher mDispatcher;
    private MockResponseDelivery mDelivery;
    private WaitableQueue mNetworkQueue;
    private MockNetwork mNetwork;
    private MockCache mCache;
    private MockRequest mRequest;

    private static final byte[] CANNED_DATA = "Ceci n'est pas une vraie reponse".getBytes();
    private static final long TIMEOUT_MILLIS = 5000;

    @Before public void setUp() throws Exception {
        mDelivery = new MockResponseDelivery();
        mNetworkQueue = new WaitableQueue();
        mNetwork = new MockNetwork();
        mCache = new MockCache();
        mRequest = new MockRequest();
        mDispatcher = new NetworkDispatcher(mNetworkQueue, mNetwork, mCache, mDelivery);
        mDispatcher.start();
    }

    @After public void tearDown() throws Exception {
        mDispatcher.quit();
        mDispatcher.join();
    }

    @Test public void successPostsResponse() throws Exception {
        mNetwork.setDataToReturn(CANNED_DATA);
        mNetwork.setNumExceptionsToThrow(0);
        mNetworkQueue.add(mRequest);
        mNetworkQueue.waitUntilEmpty(TIMEOUT_MILLIS);
        assertFalse(mDelivery.postError_called);
        assertTrue(mDelivery.postResponse_called);
        Response<?> response = mDelivery.responsePosted;
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertTrue(Arrays.equals((byte[])response.result, CANNED_DATA));
    }

    @Test public void exceptionPostsError() throws Exception {
        mNetwork.setNumExceptionsToThrow(MockNetwork.ALWAYS_THROW_EXCEPTIONS);
        mNetworkQueue.add(mRequest);
        mNetworkQueue.waitUntilEmpty(TIMEOUT_MILLIS);
        assertFalse(mDelivery.postResponse_called);
        assertTrue(mDelivery.postError_called);
    }

    @Test public void shouldCacheFalse() throws Exception {
        mRequest.setShouldCache(false);
        mNetworkQueue.add(mRequest);
        mNetworkQueue.waitUntilEmpty(TIMEOUT_MILLIS);
        assertFalse(mCache.putCalled);
    }

    @Test public void shouldCacheTrue() throws Exception {
        mNetwork.setDataToReturn(CANNED_DATA);
        mRequest.setShouldCache(true);
        mRequest.setCacheKey("bananaphone");
        mNetworkQueue.add(mRequest);
        mNetworkQueue.waitUntilEmpty(TIMEOUT_MILLIS);
        assertTrue(mCache.putCalled);
        assertNotNull(mCache.entryPut);
        assertTrue(Arrays.equals(mCache.entryPut.data, CANNED_DATA));
        assertEquals("bananaphone", mCache.keyPut);
    }
}
