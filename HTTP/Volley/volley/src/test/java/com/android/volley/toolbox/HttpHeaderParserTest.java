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

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class HttpHeaderParserTest {

    private static long ONE_MINUTE_MILLIS = 1000L * 60;
    private static long ONE_HOUR_MILLIS = 1000L * 60 * 60;
    private static long ONE_DAY_MILLIS = ONE_HOUR_MILLIS * 24;
    private static long ONE_WEEK_MILLIS = ONE_DAY_MILLIS * 7;

    private NetworkResponse response;
    private Map<String, String> headers;

    @Before public void setUp() throws Exception {
        headers = new HashMap<String, String>();
        response = new NetworkResponse(0, null, headers, false);
    }

    @Test public void parseCacheHeaders_noHeaders() {
        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);

        assertNotNull(entry);
        assertNull(entry.etag);
        assertEquals(0, entry.serverDate);
        assertEquals(0, entry.lastModified);
        assertEquals(0, entry.ttl);
        assertEquals(0, entry.softTtl);
    }

    @Test public void parseCacheHeaders_headersSet() {
        headers.put("MyCustomHeader", "42");

        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);

        assertNotNull(entry);
        assertNotNull(entry.responseHeaders);
        assertEquals(1, entry.responseHeaders.size());
        assertEquals("42", entry.responseHeaders.get("MyCustomHeader"));
    }

    @Test public void parseCacheHeaders_etag() {
        headers.put("ETag", "Yow!");

        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);

        assertNotNull(entry);
        assertEquals("Yow!", entry.etag);
    }

    @Test public void parseCacheHeaders_normalExpire() {
        long now = System.currentTimeMillis();
        headers.put("Date", rfc1123Date(now));
        headers.put("Last-Modified", rfc1123Date(now - ONE_DAY_MILLIS));
        headers.put("Expires", rfc1123Date(now + ONE_HOUR_MILLIS));

        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);

        assertNotNull(entry);
        assertNull(entry.etag);
        assertEqualsWithin(entry.serverDate, now, ONE_MINUTE_MILLIS);
        assertEqualsWithin(entry.lastModified, (now - ONE_DAY_MILLIS), ONE_MINUTE_MILLIS);
        assertTrue(entry.softTtl >= (now + ONE_HOUR_MILLIS));
        assertTrue(entry.ttl == entry.softTtl);
    }

    @Test public void parseCacheHeaders_expiresInPast() {
        long now = System.currentTimeMillis();
        headers.put("Date", rfc1123Date(now));
        headers.put("Expires", rfc1123Date(now - ONE_HOUR_MILLIS));

        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);

        assertNotNull(entry);
        assertNull(entry.etag);
        assertEqualsWithin(entry.serverDate, now, ONE_MINUTE_MILLIS);
        assertEquals(0, entry.ttl);
        assertEquals(0, entry.softTtl);
    }

    @Test public void parseCacheHeaders_serverRelative() {

        long now = System.currentTimeMillis();
        // Set "current" date as one hour in the future
        headers.put("Date", rfc1123Date(now + ONE_HOUR_MILLIS));
        // TTL four hours in the future, so should be three hours from now
        headers.put("Expires", rfc1123Date(now + 4 * ONE_HOUR_MILLIS));

        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);

        assertEqualsWithin(now + 3 * ONE_HOUR_MILLIS, entry.ttl, ONE_MINUTE_MILLIS);
        assertEquals(entry.softTtl, entry.ttl);
    }

    @Test public void parseCacheHeaders_cacheControlOverridesExpires() {
        long now = System.currentTimeMillis();
        headers.put("Date", rfc1123Date(now));
        headers.put("Expires", rfc1123Date(now + ONE_HOUR_MILLIS));
        headers.put("Cache-Control", "public, max-age=86400");

        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);

        assertNotNull(entry);
        assertNull(entry.etag);
        assertEqualsWithin(now + ONE_DAY_MILLIS, entry.ttl, ONE_MINUTE_MILLIS);
        assertEquals(entry.softTtl, entry.ttl);
    }

    @Test public void testParseCacheHeaders_staleWhileRevalidate() {
        long now = System.currentTimeMillis();
        headers.put("Date", rfc1123Date(now));
        headers.put("Expires", rfc1123Date(now + ONE_HOUR_MILLIS));

        // - max-age (entry.softTtl) indicates that the asset is fresh for 1 day
        // - stale-while-revalidate (entry.ttl) indicates that the asset may
        // continue to be served stale for up to additional 7 days
        headers.put("Cache-Control", "max-age=86400, stale-while-revalidate=604800");

        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);

        assertNotNull(entry);
        assertNull(entry.etag);
        assertEqualsWithin(now + ONE_DAY_MILLIS, entry.softTtl, ONE_MINUTE_MILLIS);
        assertEqualsWithin(now + ONE_DAY_MILLIS + ONE_WEEK_MILLIS, entry.ttl, ONE_MINUTE_MILLIS);
    }

    @Test public void parseCacheHeaders_cacheControlNoCache() {
        long now = System.currentTimeMillis();
        headers.put("Date", rfc1123Date(now));
        headers.put("Expires", rfc1123Date(now + ONE_HOUR_MILLIS));
        headers.put("Cache-Control", "no-cache");

        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);

        assertNull(entry);
    }

    @Test public void parseCacheHeaders_cacheControlMustRevalidateNoMaxAge() {
        long now = System.currentTimeMillis();
        headers.put("Date", rfc1123Date(now));
        headers.put("Expires", rfc1123Date(now + ONE_HOUR_MILLIS));
        headers.put("Cache-Control", "must-revalidate");

        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);
        assertNotNull(entry);
        assertNull(entry.etag);
        assertEqualsWithin(now, entry.ttl, ONE_MINUTE_MILLIS);
        assertEquals(entry.softTtl, entry.ttl);
    }

    @Test public void parseCacheHeaders_cacheControlMustRevalidateWithMaxAge() {
        long now = System.currentTimeMillis();
        headers.put("Date", rfc1123Date(now));
        headers.put("Expires", rfc1123Date(now + ONE_HOUR_MILLIS));
        headers.put("Cache-Control", "must-revalidate, max-age=3600");

        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);
        assertNotNull(entry);
        assertNull(entry.etag);
        assertEqualsWithin(now + ONE_HOUR_MILLIS, entry.ttl, ONE_MINUTE_MILLIS);
        assertEquals(entry.softTtl, entry.ttl);
    }

    @Test public void parseCacheHeaders_cacheControlMustRevalidateWithMaxAgeAndStale() {
        long now = System.currentTimeMillis();
        headers.put("Date", rfc1123Date(now));
        headers.put("Expires", rfc1123Date(now + ONE_HOUR_MILLIS));

        // - max-age (entry.softTtl) indicates that the asset is fresh for 1 day
        // - stale-while-revalidate (entry.ttl) indicates that the asset may
        // continue to be served stale for up to additional 7 days, but this is
        // ignored in this case because of the must-revalidate header.
        headers.put("Cache-Control",
                "must-revalidate, max-age=86400, stale-while-revalidate=604800");

        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);
        assertNotNull(entry);
        assertNull(entry.etag);
        assertEqualsWithin(now + ONE_DAY_MILLIS, entry.softTtl, ONE_MINUTE_MILLIS);
        assertEquals(entry.softTtl, entry.ttl);
    }

    private void assertEqualsWithin(long expected, long value, long fudgeFactor) {
        long diff = Math.abs(expected - value);
        assertTrue(diff < fudgeFactor);
    }

    private static String rfc1123Date(long millis) {
        DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
        return df.format(new Date(millis));
    }

    // --------------------------

    @Test public void parseCharset() {
        // Like the ones we usually see
        headers.put("Content-Type", "text/plain; charset=utf-8");
        assertEquals("utf-8", HttpHeaderParser.parseCharset(headers));

        // Charset specified, ignore default charset
        headers.put("Content-Type", "text/plain; charset=utf-8");
        assertEquals("utf-8", HttpHeaderParser.parseCharset(headers, "ISO-8859-1"));

        // Extra whitespace
        headers.put("Content-Type", "text/plain;    charset=utf-8 ");
        assertEquals("utf-8", HttpHeaderParser.parseCharset(headers));

        // Extra parameters
        headers.put("Content-Type", "text/plain; charset=utf-8; frozzle=bar");
        assertEquals("utf-8", HttpHeaderParser.parseCharset(headers));

        // No Content-Type header
        headers.clear();
        assertEquals("ISO-8859-1", HttpHeaderParser.parseCharset(headers));

        // No Content-Type header, use default charset
        headers.clear();
        assertEquals("utf-8", HttpHeaderParser.parseCharset(headers, "utf-8"));

        // Empty value
        headers.put("Content-Type", "text/plain; charset=");
        assertEquals("ISO-8859-1", HttpHeaderParser.parseCharset(headers));

        // None specified
        headers.put("Content-Type", "text/plain");
        assertEquals("ISO-8859-1", HttpHeaderParser.parseCharset(headers));

        // None charset specified, use default charset
        headers.put("Content-Type", "application/json");
        assertEquals("utf-8", HttpHeaderParser.parseCharset(headers, "utf-8"));

        // None specified, extra semicolon
        headers.put("Content-Type", "text/plain;");
        assertEquals("ISO-8859-1", HttpHeaderParser.parseCharset(headers));
    }

    @Test public void parseCaseInsensitive() {

        long now = System.currentTimeMillis();

        Header[] headersArray = new Header[5];
        headersArray[0] = new BasicHeader("eTAG", "Yow!");
        headersArray[1] = new BasicHeader("DATE", rfc1123Date(now));
        headersArray[2] = new BasicHeader("expires", rfc1123Date(now + ONE_HOUR_MILLIS));
        headersArray[3] = new BasicHeader("cache-control", "public, max-age=86400");
        headersArray[4] = new BasicHeader("content-type", "text/plain");

        Map<String, String> headers = BasicNetwork.convertHeaders(headersArray);
        NetworkResponse response = new NetworkResponse(0, null, headers, false);
        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);

        assertNotNull(entry);
        assertEquals("Yow!", entry.etag);
        assertEqualsWithin(now + ONE_DAY_MILLIS, entry.ttl, ONE_MINUTE_MILLIS);
        assertEquals(entry.softTtl, entry.ttl);
        assertEquals("ISO-8859-1", HttpHeaderParser.parseCharset(headers));
    }
}
