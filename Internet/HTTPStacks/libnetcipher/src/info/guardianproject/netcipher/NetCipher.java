/*
 * Copyright 2014-2016 Hans-Christoph Steiner
 * Copyright 2012-2016 Nathan Freitas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.guardianproject.netcipher;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import info.guardianproject.netcipher.client.TlsOnlySocketFactory;
import info.guardianproject.netcipher.proxy.OrbotHelper;

public class NetCipher {
    private static final String TAG ="NetCipher";

    private NetCipher() {
        // this is a utility class with only static methods
    }

    public final static Proxy ORBOT_HTTP_PROXY = new Proxy(Proxy.Type.HTTP,
            new InetSocketAddress("127.0.0.1", 8118));

    private static Proxy proxy;

    /**
     * Set the global HTTP proxy for all new {@link HttpURLConnection}s and
     * {@link HttpsURLConnection}s that are created after this is called.
     * <p/>
     * {@link #useTor()} will override this setting.  Traffic must be directed
     * to Tor using the proxy settings, and Orbot has its own proxy settings
     * for connections that need proxies to work.  So if "use Tor" is enabled,
     * as tested by looking for the static instance of Proxy, then no other
     * proxy settings are allowed to override the current Tor proxy.
     *
     * @param host the IP address for the HTTP proxy to use globally
     * @param port the port number for the HTTP proxy to use globally
     */
    public static void setProxy(String host, int port) {
        if (!TextUtils.isEmpty(host) && port > 0) {
            InetSocketAddress isa = new InetSocketAddress(host, port);
            setProxy(new Proxy(Proxy.Type.HTTP, isa));
        } else if (NetCipher.proxy != ORBOT_HTTP_PROXY) {
            setProxy(null);
        }
    }

    /**
     * Set the global HTTP proxy for all new {@link HttpURLConnection}s and
     * {@link HttpsURLConnection}s that are created after this is called.
     * <p/>
     * {@link #useTor()} will override this setting.  Traffic must be directed
     * to Tor using the proxy settings, and Orbot has its own proxy settings
     * for connections that need proxies to work.  So if "use Tor" is enabled,
     * as tested by looking for the static instance of Proxy, then no other
     * proxy settings are allowed to override the current Tor proxy.
     *
     * @param proxy the HTTP proxy to use globally
     */
    public static void setProxy(Proxy proxy) {
        if (proxy != null && NetCipher.proxy == ORBOT_HTTP_PROXY) {
            Log.w(TAG, "useTor is enabled, ignoring new proxy settings!");
        } else {
            NetCipher.proxy = proxy;
        }
    }

    /**
     * Get the currently active global HTTP {@link Proxy}.
     *
     * @return the active HTTP {@link Proxy}
     */
    public static Proxy getProxy() {
        return proxy;
    }

    /**
     * Clear the global HTTP proxy for all new {@link HttpURLConnection}s and
     * {@link HttpsURLConnection}s that are created after this is called. This
     * returns things to the default, proxy-less state.
     */
    public static void clearProxy() {
        setProxy(null);
    }

    /**
     * Set Orbot as the global HTTP proxy for all new {@link HttpURLConnection}
     * s and {@link HttpsURLConnection}s that are created after this is called.
     * This overrides all future calls to {@link #setProxy(Proxy)}, except to
     * clear the proxy, e.g. {@code #setProxy(null)} or {@link #clearProxy()}.
     * <p/>
     * Traffic must be directed to Tor using the proxy settings, and Orbot has its
     * own proxy settings for connections that need proxies to work.  So if "use
     * Tor" is enabled, as tested by looking for the static instance of Proxy,
     * then no other proxy settings are allowed to override the current Tor proxy.
     */
    public static void useTor() {
        setProxy(ORBOT_HTTP_PROXY);
    }

    /**
     * Get a {@link HttpURLConnection} from a {@link URL}, and specify whether
     * it should use a more compatible, but less strong, suite of ciphers.
     *
     * @param url
     * @param compatible
     * @return the {@code url} in an instance of {@link HttpURLConnection}
     * @throws IOException
     * @throws IllegalArgumentException if the proxy or TLS setup is incorrect
     */
    public static HttpURLConnection getHttpURLConnection(URL url, boolean compatible)
            throws IOException {
        SSLContext sslcontext;
        try {
            sslcontext = SSLContext.getInstance("TLSv1");
            sslcontext.init(null, null, null); // null means use default
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        } catch (KeyManagementException e) {
            throw new IllegalArgumentException(e);
        }
        SSLSocketFactory tlsOnly = new TlsOnlySocketFactory(sslcontext.getSocketFactory(),
                compatible);
        HttpsURLConnection.setDefaultSSLSocketFactory(tlsOnly);

        // .onion addresses only work via Tor, so force Tor for all of them
        Proxy proxy = NetCipher.proxy;
        if (OrbotHelper.isOnionAddress(url))
            proxy = ORBOT_HTTP_PROXY;

        if (proxy != null) {
            return (HttpURLConnection) url.openConnection(proxy);
        } else {
            return (HttpURLConnection) url.openConnection();
        }
    }

    /**
     * Get a {@link HttpsURLConnection} from a URL {@link String} using the best
     * TLS configuration available on the device.
     *
     * @param urlString
     * @return the URL in an instance of {@link HttpsURLConnection}
     * @throws IOException
     * @throws IllegalArgumentException if the proxy or TLS setup is incorrect,
     *             or if an HTTP URL is given that does not support HTTPS
     */
    public static HttpsURLConnection getHttpsURLConnection(String urlString) throws IOException {
        URL url = new URL(urlString.replaceFirst("^[Hh][Tt][Tt][Pp]:", "https:"));
        return getHttpsURLConnection(url, false);
    }

    /**
     * Get a {@link HttpsURLConnection} from a {@link Uri} using the best TLS
     * configuration available on the device.
     *
     * @param uri
     * @return the {@code uri} in an instance of {@link HttpsURLConnection}
     * @throws IOException
     * @throws IllegalArgumentException if the proxy or TLS setup is incorrect,
     *             or if an HTTP URL is given that does not support HTTPS
     */
    public static HttpsURLConnection getHttpsURLConnection(Uri uri) throws IOException {
        return getHttpsURLConnection(uri.toString());
    }

    /**
     * Get a {@link HttpsURLConnection} from a {@link URI} using the best TLS
     * configuration available on the device.
     *
     * @param uri
     * @return the {@code uri} in an instance of {@link HttpsURLConnection}
     * @throws IOException
     * @throws IllegalArgumentException if the proxy or TLS setup is incorrect,
     *             or if an HTTP URL is given that does not support HTTPS
     */
    public static HttpsURLConnection getHttpsURLConnection(URI uri) throws IOException {
        if (TextUtils.equals(uri.getScheme(), "https"))
            return getHttpsURLConnection(uri.toURL(), false);
        else
            // otherwise force scheme to https
            return getHttpsURLConnection(uri.toString());
    }

    /**
     * Get a {@link HttpsURLConnection} from a {@link URL} using the best TLS
     * configuration available on the device.
     *
     * @param url
     * @return the {@code url} in an instance of {@link HttpsURLConnection}
     * @throws IOException
     * @throws IllegalArgumentException if the proxy or TLS setup is incorrect,
     *             or if an HTTP URL is given that does not support HTTPS
     */
    public static HttpsURLConnection getHttpsURLConnection(URL url) throws IOException {
        return getHttpsURLConnection(url, false);
    }

    /**
     * Get a {@link HttpsURLConnection} from a {@link URL} using a more
     * compatible, but less strong, suite of ciphers.
     *
     * @param url
     * @return the {@code url} in an instance of {@link HttpsURLConnection}
     * @throws IOException
     * @throws IllegalArgumentException if the proxy or TLS setup is incorrect,
     *             or if an HTTP URL is given that does not support HTTPS
     */
    public static HttpsURLConnection getCompatibleHttpsURLConnection(URL url) throws IOException {
        return getHttpsURLConnection(url, true);
    }

    /**
     * Get a {@link HttpsURLConnection} from a {@link URL}, and specify whether
     * it should use a more compatible, but less strong, suite of ciphers.
     *
     * @param url
     * @param compatible
     * @return the {@code url} in an instance of {@link HttpsURLConnection}
     * @throws IOException
     * @throws IllegalArgumentException if the proxy or TLS setup is incorrect,
     *             or if an HTTP URL is given that does not support HTTPS
     */
    public static HttpsURLConnection getHttpsURLConnection(URL url, boolean compatible)
            throws IOException {
        // use default method, but enforce a HttpsURLConnection
        HttpURLConnection connection = getHttpURLConnection(url, compatible);
        if (connection instanceof HttpsURLConnection) {
            return (HttpsURLConnection) connection;
        } else {
            throw new IllegalArgumentException("not an HTTPS connection!");
        }
    }

    /**
     * Get a {@link HttpURLConnection} from a {@link URL}. If the connection is
     * {@code https://}, it will use a more compatible, but less strong, TLS
     * configuration.
     *
     * @param url
     * @return the {@code url} in an instance of {@link HttpsURLConnection}
     * @throws IOException
     * @throws IllegalArgumentException if the proxy or TLS setup is incorrect
     */
    public static HttpURLConnection getCompatibleHttpURLConnection(URL url) throws IOException {
        return getHttpURLConnection(url, true);
    }

    /**
     * Get a {@link HttpURLConnection} from a URL {@link String}. If it is an
     * {@code https://} link, then this will use the best TLS configuration
     * available on the device.
     *
     * @param urlString
     * @return the URL in an instance of {@link HttpURLConnection}
     * @throws IOException
     * @throws IllegalArgumentException if the proxy or TLS setup is incorrect
     */
    public static HttpURLConnection getHttpURLConnection(String urlString) throws IOException {
        return getHttpURLConnection(new URL(urlString));
    }

    /**
     * Get a {@link HttpURLConnection} from a {@link Uri}. If it is an
     * {@code https://} link, then this will use the best TLS configuration
     * available on the device.
     *
     * @param uri
     * @return the {@code uri} in an instance of {@link HttpURLConnection}
     * @throws IOException
     * @throws IllegalArgumentException if the proxy or TLS setup is incorrect
     */
    public static HttpURLConnection getHttpURLConnection(Uri uri) throws IOException {
        return getHttpURLConnection(uri.toString());
    }

    /**
     * Get a {@link HttpURLConnection} from a {@link URI}. If it is an
     * {@code https://} link, then this will use the best TLS configuration
     * available on the device.
     *
     * @param uri
     * @return the {@code uri} in an instance of {@link HttpURLConnection}
     * @throws IOException
     * @throws IllegalArgumentException if the proxy or TLS setup is incorrect
     */
    public static HttpURLConnection getHttpURLConnection(URI uri) throws IOException {
        return getHttpURLConnection(uri.toURL());
    }

    /**
     * Get a {@link HttpURLConnection} from a {@link URL}. If it is an
     * {@code https://} link, then this will use the best TLS configuration
     * available on the device.
     *
     * @param url
     * @return the {@code url} in an instance of {@link HttpURLConnection}
     * @throws IOException
     * @throws IllegalArgumentException if the proxy or TLS setup is incorrect
     */
    public static HttpURLConnection getHttpURLConnection(URL url) throws IOException {
        return (HttpURLConnection) getHttpURLConnection(url, false);
    }
}
