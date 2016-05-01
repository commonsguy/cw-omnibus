/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.clipip;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import android.app.Activity;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.Log;
import android.widget.Toast;

public class IPClipper extends Activity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    try {
      String addr=getLocalIPAddress();

      if (addr == null) {
        Toast.makeText(this,
                       "IP address not available -- are you online?",
                       Toast.LENGTH_LONG).show();
      }
      else {
        ClipboardManager cm=
            (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);

        try {
          cm.setText(addr);

          Toast.makeText(this, "IP Address clipped!",
                         Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
          Log.e(getClass().getSimpleName(), "Exception clipping IP", e);
          Toast.makeText(this, "Exception: " + e.getMessage(),
                         Toast.LENGTH_SHORT).show();
        }
      }
    }
    catch (Exception e) {
      Log.e("IPClipper", "Exception getting IP address", e);
      Toast.makeText(this, "Could not obtain IP address",
                     Toast.LENGTH_LONG).show();
    }

    finish();
  }

  public String getLocalIPAddress() throws SocketException {
    Enumeration<NetworkInterface> nics=
        NetworkInterface.getNetworkInterfaces();

    while (nics.hasMoreElements()) {
      NetworkInterface intf=nics.nextElement();
      Enumeration<InetAddress> addrs=intf.getInetAddresses();

      while (addrs.hasMoreElements()) {
        InetAddress addr=addrs.nextElement();

        if (!addr.isLoopbackAddress()) {
          return(addr.getHostAddress().toString());
        }
      }
    }

    return(null);
  }
}