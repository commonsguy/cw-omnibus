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

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;

public class PsiphonHelper implements ProxyHelper {

    public final static String PACKAGE_NAME = "com.psiphon3";
    public final static String COMPONENT_NAME = "com.psiphon3.StatusActivity";
    
    
    public final static String MARKET_URI = "market://details?id=" + PACKAGE_NAME;
    public final static String FDROID_URI = "https://f-droid.org/repository/browse/?fdid="
            + PACKAGE_NAME;
    public final static String ORBOT_PLAY_URI = "https://play.google.com/store/apps/details?id="
            + PACKAGE_NAME;
    
    public final static int DEFAULT_SOCKS_PORT = 1080;
    public final static int DEFAULT_HTTP_PORT = 8080;
    
  @Override
  public boolean isInstalled(Context context) {
        return isAppInstalled(context, PACKAGE_NAME);
  }
  

    private static boolean isAppInstalled(Context context, String uri) {
        try {
            PackageManager pm = context.getPackageManager();
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

  @Override
  public void requestStatus(final Context context) {
  
    Thread thread = new Thread ()
    {			
      public void run ()
      {
        //can connect to default HTTP proxy port?
        boolean isSocksOpen = false;
        boolean isHttpOpen = false;
        
        int socksPort = DEFAULT_SOCKS_PORT;
        int httpPort = DEFAULT_HTTP_PORT;
        
        for (int i = 0; i < 10 && (!isSocksOpen); i++)				
          isSocksOpen = isPortOpen("127.0.0.1",socksPort++,100);								
        
        for (int i = 0; i < 10 && (!isHttpOpen); i++)				
          isHttpOpen = isPortOpen("127.0.0.1",httpPort++,100);								

        //any other check?
        
        Intent intent = new Intent(ProxyHelper.ACTION_STATUS);
        intent.putExtra(EXTRA_PACKAGE_NAME, PACKAGE_NAME);
        
        if (isSocksOpen && isHttpOpen)
        {				
          intent.putExtra(EXTRA_STATUS, STATUS_ON);
          
          intent.putExtra(EXTRA_PROXY_PORT_HTTP, httpPort-1);
          intent.putExtra(EXTRA_PROXY_PORT_SOCKS, socksPort-1);
          
        
        }
        else
        {
          intent.putExtra(EXTRA_STATUS, STATUS_OFF);
        }
        
          context.sendBroadcast(intent);
      }
    };
    
    thread.start();
    
  }

  @Override
  public boolean requestStart(Context context) {

    Intent intent = getStartIntent(context);
  //	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
    
    return true;
  }

  @Override
  public Intent getInstallIntent(Context context) {
     final Intent intent = new Intent(Intent.ACTION_VIEW);
          intent.setData(Uri.parse(MARKET_URI));

          PackageManager pm = context.getPackageManager();
          List<ResolveInfo> resInfos = pm.queryIntentActivities(intent, 0);

          String foundPackageName = null;
          for (ResolveInfo r : resInfos) {
              if (TextUtils.equals(r.activityInfo.packageName, FDROID_PACKAGE_NAME)
                      || TextUtils.equals(r.activityInfo.packageName, PLAY_PACKAGE_NAME)) {
                  foundPackageName = r.activityInfo.packageName;
                  break;
              }
          }

          if (foundPackageName == null) {
              intent.setData(Uri.parse(FDROID_URI));
          } else {
              intent.setPackage(foundPackageName);
          }
          return intent;
  }

  @Override
  public Intent getStartIntent(Context context) {
     Intent intent = new Intent();
     intent.setComponent(new ComponentName(PACKAGE_NAME, COMPONENT_NAME));

       return intent;
  }
  
  public static boolean isPortOpen(final String ip, final int port, final int timeout) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), timeout);
            socket.close();
            return true;
        } 

        catch(ConnectException ce){
            ce.printStackTrace();
            return false;
        }

        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }


  @Override
  public String getName() {
    return PACKAGE_NAME;
  }

}
