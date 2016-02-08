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

import com.android.volley.mock.ShadowSystemClock;
import com.android.volley.toolbox.NoCache;
import com.android.volley.utils.ImmediateResponseDelivery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit tests for RequestQueue, with all dependencies mocked out
 */
@RunWith(RobolectricTestRunner.class)
@Config(shadows = {ShadowSystemClock.class})
public class RequestQueueTest {

    private ResponseDelivery mDelivery;
    @Mock private Network mMockNetwork;

    @Before public void setUp() throws Exception {
        mDelivery = new ImmediateResponseDelivery();
        initMocks(this);
    }

    @Test public void cancelAll_onlyCorrectTag() throws Exception {
        RequestQueue queue = new RequestQueue(new NoCache(), mMockNetwork, 0, mDelivery);
        Object tagA = new Object();
        Object tagB = new Object();
        Request req1 = mock(Request.class);
        when(req1.getTag()).thenReturn(tagA);
        Request req2 = mock(Request.class);
        when(req2.getTag()).thenReturn(tagB);
        Request req3 = mock(Request.class);
        when(req3.getTag()).thenReturn(tagA);
        Request req4 = mock(Request.class);
        when(req4.getTag()).thenReturn(tagA);

        queue.add(req1); // A
        queue.add(req2); // B
        queue.add(req3); // A
        queue.cancelAll(tagA);
        queue.add(req4); // A

        verify(req1).cancel(); // A cancelled
        verify(req3).cancel(); // A cancelled
        verify(req2, never()).cancel(); // B not cancelled
        verify(req4, never()).cancel(); // A added after cancel not cancelled
    }
}
