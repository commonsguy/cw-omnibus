/*
 * Copyright (C) 2012 The Android Open Source Project
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

import com.android.volley.Request.Method;
import com.android.volley.mock.MockHttpURLConnection;
import com.android.volley.mock.TestRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class HurlStackTest {

    private MockHttpURLConnection mMockConnection;

    @Before public void setUp() throws Exception {
        mMockConnection = new MockHttpURLConnection();
    }

    @Test public void connectionForDeprecatedGetRequest() throws Exception {
        TestRequest.DeprecatedGet request = new TestRequest.DeprecatedGet();
        assertEquals(request.getMethod(), Method.DEPRECATED_GET_OR_POST);

        HurlStack.setConnectionParametersForRequest(mMockConnection, request);
        assertEquals("GET", mMockConnection.getRequestMethod());
        assertFalse(mMockConnection.getDoOutput());
    }

    @Test public void connectionForDeprecatedPostRequest() throws Exception {
        TestRequest.DeprecatedPost request = new TestRequest.DeprecatedPost();
        assertEquals(request.getMethod(), Method.DEPRECATED_GET_OR_POST);

        HurlStack.setConnectionParametersForRequest(mMockConnection, request);
        assertEquals("POST", mMockConnection.getRequestMethod());
        assertTrue(mMockConnection.getDoOutput());
    }

    @Test public void connectionForGetRequest() throws Exception {
        TestRequest.Get request = new TestRequest.Get();
        assertEquals(request.getMethod(), Method.GET);

        HurlStack.setConnectionParametersForRequest(mMockConnection, request);
        assertEquals("GET", mMockConnection.getRequestMethod());
        assertFalse(mMockConnection.getDoOutput());
    }

    @Test public void connectionForPostRequest() throws Exception {
        TestRequest.Post request = new TestRequest.Post();
        assertEquals(request.getMethod(), Method.POST);

        HurlStack.setConnectionParametersForRequest(mMockConnection, request);
        assertEquals("POST", mMockConnection.getRequestMethod());
        assertFalse(mMockConnection.getDoOutput());
    }

    @Test public void connectionForPostWithBodyRequest() throws Exception {
        TestRequest.PostWithBody request = new TestRequest.PostWithBody();
        assertEquals(request.getMethod(), Method.POST);

        HurlStack.setConnectionParametersForRequest(mMockConnection, request);
        assertEquals("POST", mMockConnection.getRequestMethod());
        assertTrue(mMockConnection.getDoOutput());
    }

    @Test public void connectionForPutRequest() throws Exception {
        TestRequest.Put request = new TestRequest.Put();
        assertEquals(request.getMethod(), Method.PUT);

        HurlStack.setConnectionParametersForRequest(mMockConnection, request);
        assertEquals("PUT", mMockConnection.getRequestMethod());
        assertFalse(mMockConnection.getDoOutput());
    }

    @Test public void connectionForPutWithBodyRequest() throws Exception {
        TestRequest.PutWithBody request = new TestRequest.PutWithBody();
        assertEquals(request.getMethod(), Method.PUT);

        HurlStack.setConnectionParametersForRequest(mMockConnection, request);
        assertEquals("PUT", mMockConnection.getRequestMethod());
        assertTrue(mMockConnection.getDoOutput());
    }

    @Test public void connectionForDeleteRequest() throws Exception {
        TestRequest.Delete request = new TestRequest.Delete();
        assertEquals(request.getMethod(), Method.DELETE);

        HurlStack.setConnectionParametersForRequest(mMockConnection, request);
        assertEquals("DELETE", mMockConnection.getRequestMethod());
        assertFalse(mMockConnection.getDoOutput());
    }

    @Test public void connectionForHeadRequest() throws Exception {
        TestRequest.Head request = new TestRequest.Head();
        assertEquals(request.getMethod(), Method.HEAD);

        HurlStack.setConnectionParametersForRequest(mMockConnection, request);
        assertEquals("HEAD", mMockConnection.getRequestMethod());
        assertFalse(mMockConnection.getDoOutput());
    }

    @Test public void connectionForOptionsRequest() throws Exception {
        TestRequest.Options request = new TestRequest.Options();
        assertEquals(request.getMethod(), Method.OPTIONS);

        HurlStack.setConnectionParametersForRequest(mMockConnection, request);
        assertEquals("OPTIONS", mMockConnection.getRequestMethod());
        assertFalse(mMockConnection.getDoOutput());
    }

    @Test public void connectionForTraceRequest() throws Exception {
        TestRequest.Trace request = new TestRequest.Trace();
        assertEquals(request.getMethod(), Method.TRACE);

        HurlStack.setConnectionParametersForRequest(mMockConnection, request);
        assertEquals("TRACE", mMockConnection.getRequestMethod());
        assertFalse(mMockConnection.getDoOutput());
    }

    @Test public void connectionForPatchRequest() throws Exception {
        TestRequest.Patch request = new TestRequest.Patch();
        assertEquals(request.getMethod(), Method.PATCH);

        HurlStack.setConnectionParametersForRequest(mMockConnection, request);
        assertEquals("PATCH", mMockConnection.getRequestMethod());
        assertFalse(mMockConnection.getDoOutput());
    }

    @Test public void connectionForPatchWithBodyRequest() throws Exception {
        TestRequest.PatchWithBody request = new TestRequest.PatchWithBody();
        assertEquals(request.getMethod(), Method.PATCH);

        HurlStack.setConnectionParametersForRequest(mMockConnection, request);
        assertEquals("PATCH", mMockConnection.getRequestMethod());
        assertTrue(mMockConnection.getDoOutput());
    }
}
