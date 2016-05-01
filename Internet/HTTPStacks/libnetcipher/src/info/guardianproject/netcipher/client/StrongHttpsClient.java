/*
 * Copyright 2012-2016 Nathan Freitas
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

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.TrustManagerFactory;

import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.conn.ClientConnectionOperator;
import ch.boye.httpclientandroidlib.conn.params.ConnRoutePNames;
import ch.boye.httpclientandroidlib.conn.scheme.PlainSocketFactory;
import ch.boye.httpclientandroidlib.conn.scheme.Scheme;
import ch.boye.httpclientandroidlib.conn.scheme.SchemeRegistry;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.impl.conn.tsccm.ThreadSafeClientConnManager;
import info.guardianproject.onionkit.R;

public class StrongHttpsClient extends DefaultHttpClient {

    final Context context;
    private HttpHost proxyHost;
    private String proxyType;
    private SocksAwareProxyRoutePlanner routePlanner;

    private StrongSSLSocketFactory sFactory;
    private SchemeRegistry mRegistry;

    private final static String TRUSTSTORE_TYPE = "BKS";
    private final static String TRUSTSTORE_PASSWORD = "changeit";

    public StrongHttpsClient(Context context) {
        this.context = context;

        mRegistry = new SchemeRegistry();
        mRegistry.register(
                new Scheme(TYPE_HTTP, 80, PlainSocketFactory.getSocketFactory()));


        try {
            KeyStore keyStore = loadKeyStore();
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sFactory = new StrongSSLSocketFactory(context, trustManagerFactory.getTrustManagers(), keyStore, TRUSTSTORE_PASSWORD);
            mRegistry.register(new Scheme("https", 443, sFactory));
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    private KeyStore loadKeyStore () throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException
    {

        KeyStore trustStore = KeyStore.getInstance(TRUSTSTORE_TYPE);
        // load our bundled cacerts from raw assets
        InputStream in = context.getResources().openRawResource(R.raw.debiancacerts);
        trustStore.load(in, TRUSTSTORE_PASSWORD.toCharArray());

        return trustStore;
    }

    public StrongHttpsClient(Context context, KeyStore keystore) {
        this.context = context;

        mRegistry = new SchemeRegistry();
        mRegistry.register(
                new Scheme(TYPE_HTTP, 80, PlainSocketFactory.getSocketFactory()));

        try {
          TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            sFactory = new StrongSSLSocketFactory(context, trustManagerFactory.getTrustManagers(), keystore, TRUSTSTORE_PASSWORD);
            mRegistry.register(new Scheme("https", 443, sFactory));
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    @Override
    protected ThreadSafeClientConnManager createClientConnectionManager() {

        return new ThreadSafeClientConnManager(getParams(), mRegistry)
        {
            @Override
            protected ClientConnectionOperator createConnectionOperator(
                    SchemeRegistry schreg) {

                return new SocksAwareClientConnOperator(schreg, proxyHost, proxyType,
                        routePlanner);
            }
        };
    }

    public void useProxy(boolean enableTor, String type, String host, int port)
    {
        if (enableTor)
        {
            this.proxyType = type;

            if (type.equalsIgnoreCase(TYPE_SOCKS))
            {
                proxyHost = new HttpHost(host, port);
            }
            else
            {
              proxyHost = new HttpHost(host, port, type);
                getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHost);
            }
        }
        else
        {
          getParams().removeParameter(ConnRoutePNames.DEFAULT_PROXY);
            proxyHost = null;
        }

    }

    public void disableProxy ()
    {
      getParams().removeParameter(ConnRoutePNames.DEFAULT_PROXY);
        proxyHost = null;
    }

    public void useProxyRoutePlanner(SocksAwareProxyRoutePlanner proxyRoutePlanner)
    {
        routePlanner = proxyRoutePlanner;
        setRoutePlanner(proxyRoutePlanner);
    }
    
    /**
     * NOT ADVISED, but some sites don't yet have latest protocols and ciphers available, and some
     * apps still need to support them
     * https://dev.guardianproject.info/issues/5644
     */
    public void enableSSLCompatibilityMode() {
        sFactory.setEnableStongerDefaultProtocalVersion(false);
        sFactory.setEnableStongerDefaultSSLCipherSuite(false);
    }

    public final static String TYPE_SOCKS = "socks";
    public final static String TYPE_HTTP = "http";

}
