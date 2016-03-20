/*
 * Copyright (c) 2016 CommonsWare, LLC
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

package info.guardianproject.netcipher.hurl;

import android.content.Intent;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public interface StrongBuilder<T extends StrongBuilder, C> {
  /**
   * Callback to get a connection handed to you for use,
   * already set up for NetCipher.
   *
   * @param <C> the type of connection created by this builder
   */
  interface Callback<C> {
    /**
     * Called when the NetCipher-enhanced connection is ready
     * for use.
     *
     * @param connection the connection
     */
    void onConnected(C connection);

    /**
     * Called if we tried to connect through to Orbot but failed
     * for some reason
     *
     * @param e the reason
     */
    void onConnectionException(IOException e);

    /**
     * Called if our attempt to get a status from Orbot failed
     * after a defined period of time. See statusTimeout() on
     * OrbotInitializer.
     */
    void onTimeout();
  }

  /**
   * Call this to configure the Tor proxy from the results
   * returned by Orbot, using the best available proxy
   * (SOCKS if possible, else HTTP)
   *
   * @return the builder
   */
  T withBestProxy();

  /**
   * @return true if this builder supports HTTP proxies, false
   * otherwise
   */
  boolean supportsHttpProxy();

  /**
   * Call this to configure the Tor proxy from the results
   * returned by Orbot, using the HTTP proxy.
   *
   * @return the builder
   */
   T withHttpProxy();

  /**
   * @return true if this builder supports SOCKS proxies, false
   * otherwise
   */
   boolean supportsSocksProxy();

  /**
   * Call this to configure the Tor proxy from the results
   * returned by Orbot, using the SOCKS proxy.
   *
   * @return the builder
   */
  T withSocksProxy();

  /**
   * Replaces system-supplied keystore with one based on Debian.
   * Use this if you are keeping your app up to date with the
   * latest NetCipher library and are supporting older devices
   * (e.g., Android 4.4 and lower).
   *
   * @return the builder
   * @throws CertificateException
   * @throws NoSuchAlgorithmException
   * @throws KeyStoreException
   * @throws IOException
   * @throws UnrecoverableKeyException
   * @throws KeyManagementException
   */
  T withDefaultKeystore()
    throws CertificateException, NoSuchAlgorithmException,
    KeyStoreException, IOException, UnrecoverableKeyException,
    KeyManagementException;

  /**
   * Applies your own custom keystore, instead of either the
   * system-supplied keystore or the default NetCipher keystore.
   *
   * @param keystore a loaded KeyStore ready for use
   * @return the builder
   */
  T withKeystore(KeyStore keystore)
    throws KeyStoreException, NoSuchAlgorithmException,
    IOException, CertificateException,
    UnrecoverableKeyException, KeyManagementException;

  /**
   * Call this if you want a weaker set of supported ciphers,
   * because you are running into compatibility problems with
   * some server due to a cipher mismatch. The better solution
   * is to fix the server.
   *
   * @return the builder
   */
  T withWeakCiphers();

  /**
   * Builds a connection, applying the configuration already
   * specified in the builder.
   *
   * @param status status Intent from OrbotInitializer
   * @return the connection
   * @throws IOException
   */
  C build(Intent status) throws IOException;

  /**
   * Asynchronous version of build(), one that uses OrbotInitializer
   * internally to get the status.
   *
   * @param callback Callback to get a connection handed to you
   *                 for use, already set up for NetCipher
   */
  void build(Callback<C> callback);
}
