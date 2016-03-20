/*
 * Copyright 2012-2016 Nathan Freitas
 * Portions Copyright (c) 2016 CommonsWare, LLC
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

package info.guardianproject.netcipher.httpclient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import cz.msebera.android.httpclient.HttpHost;
import cz.msebera.android.httpclient.conn.socket.LayeredConnectionSocketFactory;
import cz.msebera.android.httpclient.protocol.HttpContext;
import info.guardianproject.netcipher.client.StrongConstants;

public class StrongSSLSocketFactory extends
  SSLConnectionSocketFactory
  implements LayeredConnectionSocketFactory {
  private boolean mEnableStongerDefaultSSLCipherSuite = true;
  private boolean mEnableStongerDefaultProtocalVersion = true;
  private String[] mProtocols;
  private String[] mCipherSuites;
  private Proxy socksProxy=null;

  static StrongSSLSocketFactory newInstance(TrustManager[] trustManagers,
                                            KeyStore keyStore,
                                            String keyStorePassword)
    throws NoSuchAlgorithmException, UnrecoverableKeyException,
    KeyStoreException, KeyManagementException {
    SSLContext sslContext = SSLContext.getInstance("TLSv1");
    KeyManager[] km = createKeyManagers(
      keyStore,
      keyStorePassword);

    sslContext.init(km, trustManagers, new SecureRandom());

    return(new StrongSSLSocketFactory(sslContext));
  }

  StrongSSLSocketFactory(SSLContext sslContext) {
    super(sslContext);
  }

  StrongSSLSocketFactory(SSLContext sslContext, int socksPort) {
    super(sslContext);

    InetSocketAddress socksAddr=
      new InetSocketAddress(StrongHttpClientBuilder.PROXY_HOST,
        socksPort);

    socksProxy=new Proxy(Proxy.Type.SOCKS, socksAddr);
  }

  private void readSSLParameters(SSLSocket sslSocket) {
    List<String> protocolsToEnable = new ArrayList<String>();
    List<String> supportedProtocols = Arrays.asList(sslSocket.getSupportedProtocols());
    for(String enabledProtocol : StrongConstants.ENABLED_PROTOCOLS) {
      if(supportedProtocols.contains(enabledProtocol)) {
        protocolsToEnable.add(enabledProtocol);
      }
    }
    this.mProtocols = protocolsToEnable.toArray(new String[protocolsToEnable.size()]);

    List<String> cipherSuitesToEnable = new ArrayList<String>();
    List<String> supportedCipherSuites = Arrays.asList(
      sslSocket.getSupportedCipherSuites());
    for(String enabledCipherSuite : StrongConstants.ENABLED_CIPHERS) {
      if(supportedCipherSuites.contains(enabledCipherSuite)) {
        cipherSuitesToEnable.add(enabledCipherSuite);
      }
    }
    this.mCipherSuites = cipherSuitesToEnable.toArray(new String[cipherSuitesToEnable.size()]);
  }

  private static KeyManager[] createKeyManagers(final KeyStore keystore,
                                         final String password) throws KeyStoreException,
    NoSuchAlgorithmException, UnrecoverableKeyException {
    if (keystore == null) {
      throw new IllegalArgumentException("Keystore may not be null");
    }
    KeyManagerFactory kmfactory = KeyManagerFactory
      .getInstance(KeyManagerFactory.getDefaultAlgorithm());
    kmfactory.init(keystore,
      password!=null ? password.toCharArray()
        : null);
    return kmfactory.getKeyManagers();
  }

  @Override
  public Socket createSocket(HttpContext context)
    throws IOException {
    Socket result;

    if (socksProxy==null) {
      result=super.createSocket(context);
    }
    else {
      result=new Socket(socksProxy);
    }

    enableStrongerDefaults(result);

    return(result);
  }

  @Override
  public Socket createLayeredSocket(Socket socket, String target,
                                    int port,
                                    HttpContext context)
    throws IOException {
    Socket result;

    result=super.createLayeredSocket(socket, target, port,
      context);

    enableStrongerDefaults(result);

    return(result);
  }

  @Override
  public Socket connectSocket(int connectTimeout, Socket socket,
                              HttpHost host,
                              InetSocketAddress remoteAddress,
                              InetSocketAddress localAddress,
                              HttpContext context)
    throws IOException {
    return(super.connectSocket(connectTimeout, socket, host,
      remoteAddress, localAddress, context));
  }

  /**
   * Defaults the SSL connection to use a strong cipher suite and TLS version
   *
   * @param socket
   */
  private void enableStrongerDefaults(Socket socket) {
    if (isSecure(socket)) {
      SSLSocket sslSocket = (SSLSocket) socket;
      readSSLParameters(sslSocket);

      if (mEnableStongerDefaultProtocalVersion && mProtocols != null) {
        sslSocket.setEnabledProtocols(mProtocols);
      }

      if (mEnableStongerDefaultSSLCipherSuite && mCipherSuites != null) {
        sslSocket.setEnabledCipherSuites(mCipherSuites);
      }
    }
  }

  public boolean isSecure(Socket sock) throws IllegalArgumentException {
    return(sock instanceof SSLSocket);
  }

  public boolean isEnableStongerDefaultSSLCipherSuite() {
    return mEnableStongerDefaultSSLCipherSuite;
  }

  public void setEnableStongerDefaultSSLCipherSuite(boolean enable) {
    this.mEnableStongerDefaultSSLCipherSuite = enable;
  }

  public boolean isEnableStongerDefaultProtocalVersion() {
    return mEnableStongerDefaultProtocalVersion;
  }

  public void setEnableStongerDefaultProtocalVersion(boolean enable) {
    this.mEnableStongerDefaultProtocalVersion = enable;
  }
}
