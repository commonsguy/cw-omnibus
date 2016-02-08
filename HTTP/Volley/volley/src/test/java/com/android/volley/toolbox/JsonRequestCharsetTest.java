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

package com.android.volley.toolbox;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.lang.Exception;
import java.lang.String;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class JsonRequestCharsetTest {

    /**
     * String in Czech - "Retezec v cestine."
     */
    private static final String TEXT_VALUE = "\u0158et\u011bzec v \u010de\u0161tin\u011b.";
    private static final String TEXT_NAME = "text";
    private static final int TEXT_INDEX = 0;

    /**
     * Copyright symbol has different encoding in utf-8 and ISO-8859-1,
     * and it doesn't exists in ISO-8859-2
     */
    private static final String COPY_VALUE = "\u00a9";
    private static final String COPY_NAME = "copyright";
    private static final int COPY_INDEX = 1;

    @Test public void defaultCharsetJsonObject() throws Exception {
        // UTF-8 is default charset for JSON
        byte[] data = jsonObjectString().getBytes(Charset.forName("UTF-8"));
        NetworkResponse network = new NetworkResponse(data);
        JsonObjectRequest objectRequest = new JsonObjectRequest("", null, null, null);
        Response<JSONObject> objectResponse = objectRequest.parseNetworkResponse(network);

        assertNotNull(objectResponse);
        assertTrue(objectResponse.isSuccess());
        assertEquals(TEXT_VALUE, objectResponse.result.getString(TEXT_NAME));
        assertEquals(COPY_VALUE, objectResponse.result.getString(COPY_NAME));
    }

    @Test public void defaultCharsetJsonArray() throws Exception {
        // UTF-8 is default charset for JSON
        byte[] data = jsonArrayString().getBytes(Charset.forName("UTF-8"));
        NetworkResponse network = new NetworkResponse(data);
        JsonArrayRequest arrayRequest = new JsonArrayRequest("", null, null);
        Response<JSONArray> arrayResponse = arrayRequest.parseNetworkResponse(network);

        assertNotNull(arrayResponse);
        assertTrue(arrayResponse.isSuccess());
        assertEquals(TEXT_VALUE, arrayResponse.result.getString(TEXT_INDEX));
        assertEquals(COPY_VALUE, arrayResponse.result.getString(COPY_INDEX));
    }

    @Test public void specifiedCharsetJsonObject() throws Exception {
        byte[] data = jsonObjectString().getBytes(Charset.forName("ISO-8859-1"));
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=iso-8859-1");
        NetworkResponse network = new NetworkResponse(data, headers);
        JsonObjectRequest objectRequest = new JsonObjectRequest("", null, null, null);
        Response<JSONObject> objectResponse = objectRequest.parseNetworkResponse(network);

        assertNotNull(objectResponse);
        assertTrue(objectResponse.isSuccess());
        //don't check the text in Czech, ISO-8859-1 doesn't support some Czech characters
        assertEquals(COPY_VALUE, objectResponse.result.getString(COPY_NAME));
    }

    @Test public void specifiedCharsetJsonArray() throws Exception {
        byte[] data = jsonArrayString().getBytes(Charset.forName("ISO-8859-2"));
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=iso-8859-2");
        NetworkResponse network = new NetworkResponse(data, headers);
        JsonArrayRequest arrayRequest = new JsonArrayRequest("", null, null);
        Response<JSONArray> arrayResponse = arrayRequest.parseNetworkResponse(network);

        assertNotNull(arrayResponse);
        assertTrue(arrayResponse.isSuccess());
        assertEquals(TEXT_VALUE, arrayResponse.result.getString(TEXT_INDEX));
        // don't check the copyright symbol, ISO-8859-2 doesn't have it, but it has Czech characters
    }

    private static String jsonObjectString() throws Exception {
        JSONObject json = new JSONObject().put(TEXT_NAME, TEXT_VALUE).put(COPY_NAME, COPY_VALUE);
        return json.toString();
    }

    private static String jsonArrayString() throws Exception {
        JSONArray json = new JSONArray().put(TEXT_INDEX, TEXT_VALUE).put(COPY_INDEX, COPY_VALUE);
        return json.toString();
    }
}
