/*
 * Copyright 2015 str4d
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

package info.guardianproject.netcipher.client;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.conn.HttpHostConnectException;
import ch.boye.httpclientandroidlib.conn.OperatedClientConnection;
import ch.boye.httpclientandroidlib.conn.scheme.Scheme;
import ch.boye.httpclientandroidlib.conn.scheme.SchemeRegistry;
import ch.boye.httpclientandroidlib.conn.scheme.SchemeSocketFactory;
import ch.boye.httpclientandroidlib.conn.scheme.SocketFactory;
import ch.boye.httpclientandroidlib.conn.ssl.SSLSocketFactory;
import ch.boye.httpclientandroidlib.impl.conn.DefaultClientConnectionOperator;
import ch.boye.httpclientandroidlib.params.HttpParams;
import ch.boye.httpclientandroidlib.protocol.HttpContext;

public class SocksAwareClientConnOperator extends DefaultClientConnectionOperator {

    private static final int CONNECT_TIMEOUT_MILLISECONDS = 60000;
    private static final int READ_TIMEOUT_MILLISECONDS = 60000;

    private HttpHost mProxyHost;
    private String mProxyType;
    private SocksAwareProxyRoutePlanner mRoutePlanner;

    public SocksAwareClientConnOperator(SchemeRegistry registry,
                                        HttpHost proxyHost,
                                        String proxyType,
                                        SocksAwareProxyRoutePlanner proxyRoutePlanner) {
        super(registry);

        mProxyHost = proxyHost;
        mProxyType = proxyType;
        mRoutePlanner = proxyRoutePlanner;
    }

    @Override
    public void openConnection(
            final OperatedClientConnection conn,
            final HttpHost target,
            final InetAddress local,
            final HttpContext context,
            final HttpParams params) throws IOException {
        if (mProxyHost != null) {
            if (mProxyType != null && mProxyType.equalsIgnoreCase("socks")) {
                Log.d("StrongHTTPS", "proxying using SOCKS");
                openSocksConnection(mProxyHost, conn, target, local, context, params);
            } else {
                Log.d("StrongHTTPS", "proxying with: " + mProxyType);
                openNonSocksConnection(conn, target, local, context, params);
            }
        } else if (mRoutePlanner != null) {
            if (mRoutePlanner.isProxy(target)) {
                // HTTP proxy, already handled by the route planner system
                Log.d("StrongHTTPS", "proxying using non-SOCKS");
                openNonSocksConnection(conn, target, local, context, params);
            } else {
                // Either SOCKS or direct
                HttpHost proxy = mRoutePlanner.determineRequiredProxy(target, null, context);
                if (proxy == null) {
                    Log.d("StrongHTTPS", "not proxying");
                    openNonSocksConnection(conn, target, local, context, params);
                } else if (mRoutePlanner.isSocksProxy(proxy)) {
                    Log.d("StrongHTTPS", "proxying using SOCKS");
                    openSocksConnection(proxy, conn, target, local, context, params);
                } else {
                    throw new IllegalStateException("Non-SOCKS proxy returned");
                }
            }
        } else {
            Log.d("StrongHTTPS", "not proxying");
            openNonSocksConnection(conn, target, local, context, params);
        }
    }

    private void openNonSocksConnection(
            final OperatedClientConnection conn,
            final HttpHost target,
            final InetAddress local,
            final HttpContext context,
            final HttpParams params) throws IOException {
        if (conn == null) {
            throw new IllegalArgumentException("Connection must not be null.");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target host must not be null.");
        }
        // local address may be null
        // @@@ is context allowed to be null?
        if (params == null) {
            throw new IllegalArgumentException("Parameters must not be null.");
        }
        if (conn.isOpen()) {
            throw new IllegalArgumentException("Connection must not be open.");
        }

        final Scheme schm = schemeRegistry.getScheme(target.getSchemeName());
        final SocketFactory sf = schm.getSocketFactory();

        Socket sock = sf.createSocket();
        conn.opening(sock, target);

        try {
            Socket connsock = sf.connectSocket(sock, target.getHostName(),
                    schm.resolvePort(target.getPort()),
                    local, 0, params);

            if (sock != connsock) {
                sock = connsock;
                conn.opening(sock, target);
            }
        } catch (ConnectException ex) {
            throw new HttpHostConnectException(target, ex);
        }
        prepareSocket(sock, context, params);
        conn.openCompleted(sf.isSecure(sock), params);
    }

    // Derived from the original DefaultClientConnectionOperator.java in Apache HttpClient 4.2
    private void openSocksConnection(
            final HttpHost proxy,
            final OperatedClientConnection conn,
            final HttpHost target,
            final InetAddress local,
            final HttpContext context,
            final HttpParams params) throws IOException {
        Socket socket = null;
        Socket sslSocket = null;
        try {
            if (conn == null || target == null || params == null) {
                throw new IllegalArgumentException("Required argument may not be null");
            }
            if (conn.isOpen()) {
                throw new IllegalStateException("Connection must not be open");
            }

            Scheme scheme = schemeRegistry.getScheme(target.getSchemeName());
            SchemeSocketFactory schemeSocketFactory = scheme.getSchemeSocketFactory();

            int port = scheme.resolvePort(target.getPort());
            String host = target.getHostName();

            // Perform explicit SOCKS4a connection request. SOCKS4a supports remote host name resolution
            // (i.e., Tor resolves the hostname, which may be an onion address).
            // The Android (Apache Harmony) Socket class appears to support only SOCKS4 and throws an
            // exception on an address created using INetAddress.createUnresolved() -- so the typical
            // technique for using Java SOCKS4a/5 doesn't appear to work on Android:
            // https://android.googlesource.com/platform/libcore/+/master/luni/src/main/java/java/net/PlainSocketImpl.java
            // See also: http://www.mit.edu/~foley/TinFoil/src/tinfoil/TorLib.java, for a similar implementation

            // From http://en.wikipedia.org/wiki/SOCKS#SOCKS4a:
            //
            // field 1: SOCKS version number, 1 byte, must be 0x04 for this version
            // field 2: command code, 1 byte:
            //     0x01 = establish a TCP/IP stream connection
            //     0x02 = establish a TCP/IP port binding
            // field 3: network byte order port number, 2 bytes
            // field 4: deliberate invalid IP address, 4 bytes, first three must be 0x00 and the last one must not be 0x00
            // field 5: the user ID string, variable length, terminated with a null (0x00)
            // field 6: the domain name of the host we want to contact, variable length, terminated with a null (0x00)


            socket = new Socket();
            conn.opening(socket, target);
            socket.setSoTimeout(READ_TIMEOUT_MILLISECONDS);
            socket.connect(new InetSocketAddress(proxy.getHostName(), proxy.getPort()), CONNECT_TIMEOUT_MILLISECONDS);

            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.write((byte) 0x04);
            outputStream.write((byte) 0x01);
            outputStream.writeShort((short) port);
            outputStream.writeInt(0x01);
            outputStream.write((byte) 0x00);
            outputStream.write(host.getBytes());
            outputStream.write((byte) 0x00);

            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            if (inputStream.readByte() != (byte) 0x00 || inputStream.readByte() != (byte) 0x5a) {
                throw new IOException("SOCKS4a connect failed");
            }
            inputStream.readShort();
            inputStream.readInt();

            if (schemeSocketFactory instanceof SSLSocketFactory) {
                sslSocket = ((SSLSocketFactory) schemeSocketFactory).createLayeredSocket(socket, host, port, params);
                conn.opening(sslSocket, target);
                sslSocket.setSoTimeout(READ_TIMEOUT_MILLISECONDS);
                prepareSocket(sslSocket, context, params);
                conn.openCompleted(schemeSocketFactory.isSecure(sslSocket), params);
            } else {
                conn.opening(socket, target);
                socket.setSoTimeout(READ_TIMEOUT_MILLISECONDS);
                prepareSocket(socket, context, params);
                conn.openCompleted(schemeSocketFactory.isSecure(socket), params);
            }
            // TODO: clarify which connection throws java.net.SocketTimeoutException?
        } catch (IOException e) {
            try {
                if (sslSocket != null) {
                    sslSocket.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ioe) {
            }
            throw e;
        }
    }

    @Override
    public void updateSecureConnection(
            final OperatedClientConnection conn,
            final HttpHost target,
            final HttpContext context,
            final HttpParams params) throws IOException {
        if (mProxyHost != null && mProxyType.equalsIgnoreCase("socks"))
            throw new RuntimeException("operation not supported");
        else
            super.updateSecureConnection(conn, target, context, params);
    }

    @Override
    protected InetAddress[] resolveHostname(final String host) throws UnknownHostException {
        if (mProxyHost != null && mProxyType.equalsIgnoreCase("socks"))
            throw new RuntimeException("operation not supported");
        else
            return super.resolveHostname(host);
    }
}
