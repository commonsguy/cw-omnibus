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

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import info.guardianproject.netcipher.client.TlsOnlySocketFactory;

public class SniFriendlySocketFactory extends
  TlsOnlySocketFactory {
  public SniFriendlySocketFactory() {
    super();
  }

  public SniFriendlySocketFactory(SSLSocketFactory delegate) {
    super(delegate);
  }

  public SniFriendlySocketFactory(SSLSocketFactory delegate,
                                  boolean compatible) {
    super(delegate, compatible);
  }

  @Override
  public Socket createSocket(InetAddress address, int port,
                             InetAddress localAddress,
                             int localPort) throws IOException {
    return(fixSni(super.createSocket(address, port, localAddress,
      localPort), address.getHostName()));
  }

  @Override
  public Socket createSocket(InetAddress host, int port)
    throws IOException {
    return(fixSni(super.createSocket(host, port),
      host.getHostName()));
  }

  @Override
  public Socket createSocket(String host, int port)
    throws IOException {
    return(fixSni(super.createSocket(host, port), host));
  }

  @Override
  public Socket createSocket(String host, int port,
                             InetAddress localHost,
                             int localPort) throws IOException {
    return(fixSni(
      super.createSocket(host, port, localHost, localPort), host));
  }

  @Override
  public Socket createSocket(Socket s, String host, int port,
                             boolean autoClose)
    throws IOException {
    return(fixSni(super.createSocket(s, host, port, autoClose),
      host));
  }

  // inspired by https://github.com/k9mail/k-9/commit/54f9fd36a77423a55f63fbf9b1bcea055a239768

  private Socket fixSni(Socket socket, String host) {
    if (socket instanceof DelegateSSLSocket) {
      ((DelegateSSLSocket)socket)._setHostname(host);
    }
    else if (socket instanceof SSLSocket) {
      try {
        socket
          .getClass()
          .getMethod("setHostname", String.class)
          .invoke(socket, host);
      }
      catch (Exception e) {
        throw new IllegalStateException("Could not enable SNI", e);
      }
    }

    return(socket);
  }
}
