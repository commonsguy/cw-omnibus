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
import com.android.volley.mock.MockRequest;
import com.android.volley.mock.MockResponseDelivery;
import com.android.volley.mock.WaitableQueue;
import com.android.volley.utils.CacheTestUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@SuppressWarnings("rawtypes")
public class CacheDispatcherTest {
    private CacheDispatcher mDispatcher;
    private WaitableQueue mCacheQueue;
    private WaitableQueue mNetworkQueue;
    private MockCache mCache;
    private MockResponseDelivery mDelivery;
    private MockRequest mRequest;

    private static final long TIMEOUT_MILLIS = 5000;

    @Before public void setUp() throws Exception {
        mCacheQueue = new WaitableQueue();
        mNetworkQueue = new WaitableQueue();
        mCache = new MockCache();
        mDelivery = new MockResponseDelivery();

        mRequest = new MockRequest();

        mDispatcher = new CacheDispatcher(mCacheQueue, mNetworkQueue, mCache, mDelivery);
        mDispatcher.start();
    }

    @After public void tearDown() throws Exception {
        mDispatcher.quit();
        mDispatcher.join();
    }

    // A cancelled request should not be processed at all.
    @Test public void cancelledRequest() throws Exception {
        mRequest.cancel();
        mCacheQueue.add(mRequest);
        mCacheQueue.waitUntilEmpty(TIMEOUT_MILLIS);
        assertFalse(mCache.getCalled);
        assertFalse(mDelivery.wasEitherResponseCalled());
    }

    // A cache miss does not post a response and puts the request on the network queue.
    @Test public void cacheMiss() throws Exception {
        mCacheQueue.add(mRequest);
        mCacheQueue.waitUntilEmpty(TIMEOUT_MILLIS);
        assertFalse(mDelivery.wasEitherResponseCalled());
        assertTrue(mNetworkQueue.size() > 0);
        Request request = mNetworkQueue.take();
        assertNull(request.getCacheEntry());
    }

    // A non-expired cache hit posts a response and does not queue to the network.
    @Test public void nonExpiredCacheHit() throws Exception {
        Cache.Entry entry = CacheTestUtils.makeRandomCacheEntry(null, false, false);
        mCache.setEntryToReturn(entry);
        mCacheQueue.add(mRequest);
        mCacheQueue.waitUntilEmpty(TIMEOUT_MILLIS);
        assertTrue(mDelivery.postResponse_called);
        assertFalse(mDelivery.postError_called);
    }

    // A soft-expired cache hit posts a response and queues to the network.
    @Test public void softExpiredCacheHit() throws Exception {
        Cache.Entry entry = CacheTestUtils.makeRandomCacheEntry(null, false, true);
        mCache.setEntryToReturn(entry);
        mCacheQueue.add(mRequest);
        mCacheQueue.waitUntilEmpty(TIMEOUT_MILLIS);
        assertTrue(mDelivery.postResponse_called);
        assertFalse(mDelivery.postError_called);
        assertTrue(mNetworkQueue.size() > 0);
        Request request = mNetworkQueue.take();
        assertSame(entry, request.getCacheEntry());
    }

    // An expired cache hit does not post a response and queues to the network.
    @Test public void expiredCacheHit() throws Exception {
        Cache.Entry entry = CacheTestUtils.makeRandomCacheEntry(null, true, true);
        mCache.setEntryToReturn(entry);
        mCacheQueue.add(mRequest);
        mCacheQueue.waitUntilEmpty(TIMEOUT_MILLIS);
        assertFalse(mDelivery.wasEitherResponseCalled());
        assertTrue(mNetworkQueue.size() > 0);
        Request request = mNetworkQueue.take();
        assertSame(entry, request.getCacheEntry());
    }
}
