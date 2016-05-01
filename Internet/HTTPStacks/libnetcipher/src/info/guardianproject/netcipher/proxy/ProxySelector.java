/*
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
package info.guardianproject.netcipher.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class ProxySelector extends java.net.ProxySelector {

  private ArrayList<Proxy> listProxies;
  
  public ProxySelector ()
  {
    super ();
    
    listProxies = new ArrayList<Proxy>();
    
    
  }
  
  public void addProxy (Proxy.Type type,String host, int port)
  {
    Proxy proxy = new Proxy(type,new InetSocketAddress(host, port));
    listProxies.add(proxy);
  }
  
  @Override
  public void connectFailed(URI uri, SocketAddress address,
      IOException failure) {
    Log.w("ProxySelector","could not connect to " + address.toString() + ": " + failure.getMessage());
  }

  @Override
  public List<Proxy> select(URI uri) {
    
    return listProxies;
  }

}
