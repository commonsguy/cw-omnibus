/*
 * Copyright 2015 Anthony Restaino
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

package info.guardianproject.netcipher.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.http.HttpHost;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Proxy;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.util.ArrayMap;
import android.util.Log;
import android.webkit.WebView;

public class WebkitProxy {

    private final static String DEFAULT_HOST = "localhost";//"127.0.0.1";
    private final static int DEFAULT_PORT = 8118;
    private final static int DEFAULT_SOCKS_PORT = 9050;

    private final static int REQUEST_CODE = 0;

    private final static String TAG = "OrbotHelpher";

    public static boolean setProxy(String appClass, Context ctx, WebView wView, String host, int port) throws Exception
    {
      
      setSystemProperties(host, port);

        boolean worked = false;

        if (Build.VERSION.SDK_INT < 13)
        {
//            worked = setWebkitProxyGingerbread(ctx, host, port);
            setProxyUpToHC(wView, host, port);
        }
        else if (Build.VERSION.SDK_INT < 19)
        {
            worked = setWebkitProxyICS(ctx, host, port);
        }
        else if (Build.VERSION.SDK_INT < 20)
        {
            worked = setKitKatProxy(appClass, ctx, host, port);
        
            if (!worked) //some kitkat's still use ICS browser component (like Cyanogen 11)
              worked = setWebkitProxyICS(ctx, host, port);
            
        }
        else if (Build.VERSION.SDK_INT >= 21)
        {
          worked = setWebkitProxyLollipop(ctx, host, port);
            
        }
        
        return worked;
    }

    private static void setSystemProperties(String host, int port)
    {

      System.setProperty("proxyHost", host);
        System.setProperty("proxyPort", port + "");

        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxyPort", port + "");

        System.setProperty("https.proxyHost", host);
        System.setProperty("https.proxyPort", port + "");

        
        System.setProperty("socks.proxyHost", host);
        System.setProperty("socks.proxyPort", DEFAULT_SOCKS_PORT + "");

        System.setProperty("socksProxyHost", host);
        System.setProperty("socksProxyPort", DEFAULT_SOCKS_PORT + "");
        
        
        /*
        ProxySelector pSelect = new ProxySelector();
        pSelect.addProxy(Proxy.Type.HTTP, host, port);
        ProxySelector.setDefault(pSelect);
        */
        /*
        System.setProperty("http_proxy", "http://" + host + ":" + port);
        System.setProperty("proxy-server", "http://" + host + ":" + port);
        System.setProperty("host-resolver-rules","MAP * 0.0.0.0 , EXCLUDE myproxy");

        System.getProperty("networkaddress.cache.ttl", "-1");
        */

    }

    private static void resetSystemProperties()
    {

        System.setProperty("proxyHost", "");
        System.setProperty("proxyPort", "");

        System.setProperty("http.proxyHost", "");
        System.setProperty("http.proxyPort", "");

        System.setProperty("https.proxyHost", "");
        System.setProperty("https.proxyPort", "");


        System.setProperty("socks.proxyHost", "");
        System.setProperty("socks.proxyPort", DEFAULT_SOCKS_PORT + "");

        System.setProperty("socksProxyHost", "");
        System.setProperty("socksProxyPort", DEFAULT_SOCKS_PORT + "");

    }

    /**
     * Override WebKit Proxy settings
     * 
     * @param ctx Android ApplicationContext
     * @param host
     * @param port
     * @return true if Proxy was successfully set
     */
    private static boolean setWebkitProxyGingerbread(Context ctx, String host, int port)
            throws Exception
            {
      
        boolean ret = false;

        Object requestQueueObject = getRequestQueue(ctx);
        if (requestQueueObject != null) {
            // Create Proxy config object and set it into request Q
            HttpHost httpHost = new HttpHost(host, port, "http");
            setDeclaredField(requestQueueObject, "mProxyHost", httpHost);
            return true;
        }
        return false;

    }
    

/**
 * Set Proxy for Android 3.2 and below.
 */
@SuppressWarnings("all")
private static boolean setProxyUpToHC(WebView webview, String host, int port) {
    Log.d(TAG, "Setting proxy with <= 3.2 API.");

    HttpHost proxyServer = new HttpHost(host, port);
    // Getting network
    Class networkClass = null;
    Object network = null;
    try {
        networkClass = Class.forName("android.webkit.Network");
        if (networkClass == null) {
            Log.e(TAG, "failed to get class for android.webkit.Network");
            return false;
        }
        Method getInstanceMethod = networkClass.getMethod("getInstance", Context.class);
        if (getInstanceMethod == null) {
            Log.e(TAG, "failed to get getInstance method");
        }
        network = getInstanceMethod.invoke(networkClass, new Object[]{webview.getContext()});
    } catch (Exception ex) {
        Log.e(TAG, "error getting network: " + ex);
        return false;
    }
    if (network == null) {
        Log.e(TAG, "error getting network: network is null");
        return false;
    }
    Object requestQueue = null;
    try {
        Field requestQueueField = networkClass
                .getDeclaredField("mRequestQueue");
        requestQueue = getFieldValueSafely(requestQueueField, network);
    } catch (Exception ex) {
        Log.e(TAG, "error getting field value");
        return false;
    }
    if (requestQueue == null) {
        Log.e(TAG, "Request queue is null");
        return false;
    }
    Field proxyHostField = null;
    try {
        Class requestQueueClass = Class.forName("android.net.http.RequestQueue");
        proxyHostField = requestQueueClass
                .getDeclaredField("mProxyHost");
    } catch (Exception ex) {
        Log.e(TAG, "error getting proxy host field");
        return false;
    }

    boolean temp = proxyHostField.isAccessible();
    try {
        proxyHostField.setAccessible(true);
        proxyHostField.set(requestQueue, proxyServer);
    } catch (Exception ex) {
        Log.e(TAG, "error setting proxy host");
    } finally {
        proxyHostField.setAccessible(temp);
    }

    Log.d(TAG, "Setting proxy with <= 3.2 API successful!");
    return true;
}


private static Object getFieldValueSafely(Field field, Object classInstance) throws IllegalArgumentException, IllegalAccessException {
    boolean oldAccessibleValue = field.isAccessible();
    field.setAccessible(true);
    Object result = field.get(classInstance);
    field.setAccessible(oldAccessibleValue);
    return result;
}

    private static boolean setWebkitProxyICS(Context ctx, String host, int port)
    {

        // PSIPHON: added support for Android 4.x WebView proxy
        try
        {
            Class webViewCoreClass = Class.forName("android.webkit.WebViewCore");

            Class proxyPropertiesClass = Class.forName("android.net.ProxyProperties");
            if (webViewCoreClass != null && proxyPropertiesClass != null)
            {
                Method m = webViewCoreClass.getDeclaredMethod("sendStaticMessage", Integer.TYPE,
                        Object.class);
                Constructor c = proxyPropertiesClass.getConstructor(String.class, Integer.TYPE,
                        String.class);

                if (m != null && c != null)
                {
                    m.setAccessible(true);
                    c.setAccessible(true);
                    Object properties = c.newInstance(host, port, null);

                    // android.webkit.WebViewCore.EventHub.PROXY_CHANGED = 193;
                    m.invoke(null, 193, properties);
                    
                 
                    return true;
                }

                
           }
        } catch (Exception e)
        {
            Log.e("ProxySettings",
                    "Exception setting WebKit proxy through android.net.ProxyProperties: "
                            + e.toString());
        } catch (Error e)
        {
            Log.e("ProxySettings",
                    "Exception setting WebKit proxy through android.webkit.Network: "
                            + e.toString());
        }

        return false;

    }
    
    @TargetApi(19)
  public static boolean resetKitKatProxy(String appClass, Context appContext) {
    
      return setKitKatProxy(appClass, appContext,null,0);
    }
    
    @TargetApi(19)
  private static boolean setKitKatProxy(String appClass, Context appContext, String host, int port) {
      //Context appContext = webView.getContext().getApplicationContext();
      
      if (host != null)
      {
          System.setProperty("http.proxyHost", host);
          System.setProperty("http.proxyPort", port + "");
          System.setProperty("https.proxyHost", host);
          System.setProperty("https.proxyPort", port + "");
      }
        
        try {
            Class applictionCls = Class.forName(appClass);
            Field loadedApkField = applictionCls.getField("mLoadedApk");
            loadedApkField.setAccessible(true);
            Object loadedApk = loadedApkField.get(appContext);
            Class loadedApkCls = Class.forName("android.app.LoadedApk");
            Field receiversField = loadedApkCls.getDeclaredField("mReceivers");
            receiversField.setAccessible(true);
            ArrayMap receivers = (ArrayMap) receiversField.get(loadedApk);
            for (Object receiverMap : receivers.values()) {
                for (Object rec : ((ArrayMap) receiverMap).keySet()) {
                    Class clazz = rec.getClass();
                    if (clazz.getName().contains("ProxyChangeListener")) {
                        Method onReceiveMethod = clazz.getDeclaredMethod("onReceive", Context.class, Intent.class);
                        Intent intent = new Intent(Proxy.PROXY_CHANGE_ACTION);
                        
                        if (host != null)
                        {
                          /*********** optional, may be need in future *************/
                          final String CLASS_NAME = "android.net.ProxyProperties";
                          Class cls = Class.forName(CLASS_NAME);
                          Constructor constructor = cls.getConstructor(String.class, Integer.TYPE, String.class);
                          constructor.setAccessible(true);
                          Object proxyProperties = constructor.newInstance(host, port, null);
                          intent.putExtra("proxy", (Parcelable) proxyProperties);
                          /*********** optional, may be need in future *************/
                        }

                        onReceiveMethod.invoke(rec, appContext, intent);
                    }
                }
            }
            return true;
        } catch (ClassNotFoundException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Log.v(TAG, e.getMessage());
            Log.v(TAG, exceptionAsString);
        } catch (NoSuchFieldException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Log.v(TAG, e.getMessage());
            Log.v(TAG, exceptionAsString);
        } catch (IllegalAccessException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Log.v(TAG, e.getMessage());
            Log.v(TAG, exceptionAsString);
        } catch (IllegalArgumentException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Log.v(TAG, e.getMessage());
            Log.v(TAG, exceptionAsString);
        } catch (NoSuchMethodException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Log.v(TAG, e.getMessage());
            Log.v(TAG, exceptionAsString);
        } catch (InvocationTargetException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Log.v(TAG, e.getMessage());
            Log.v(TAG, exceptionAsString);
        } catch (InstantiationException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Log.v(TAG, e.getMessage());
            Log.v(TAG, exceptionAsString);
        }
        return false;    }
    
    @TargetApi(21)
  public static boolean resetLollipopProxy(String appClass, Context appContext) {
    
      return setWebkitProxyLollipop(appContext,null,0);
    }
    
 // http://stackanswers.com/questions/25272393/android-webview-set-proxy-programmatically-on-android-l
    @TargetApi(21) // for android.util.ArrayMap methods
    @SuppressWarnings("rawtypes")
    private static boolean setWebkitProxyLollipop(Context appContext, String host, int port)
    {
        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxyPort", port + "");
        System.setProperty("https.proxyHost", host);
        System.setProperty("https.proxyPort", port + "");
        try {
            Class applictionClass = Class.forName("android.app.Application");
            Field mLoadedApkField = applictionClass.getDeclaredField("mLoadedApk");
            mLoadedApkField.setAccessible(true);
            Object mloadedApk = mLoadedApkField.get(appContext);
            Class loadedApkClass = Class.forName("android.app.LoadedApk");
            Field mReceiversField = loadedApkClass.getDeclaredField("mReceivers");
            mReceiversField.setAccessible(true);
            ArrayMap receivers = (ArrayMap) mReceiversField.get(mloadedApk);
            for (Object receiverMap : receivers.values())
            {
                for (Object receiver : ((ArrayMap) receiverMap).keySet())
                {
                    Class clazz = receiver.getClass();
                    if (clazz.getName().contains("ProxyChangeListener"))
                    {
                        Method onReceiveMethod = clazz.getDeclaredMethod("onReceive", Context.class, Intent.class);
                        Intent intent = new Intent(Proxy.PROXY_CHANGE_ACTION);
                        onReceiveMethod.invoke(receiver, appContext, intent);
                    }
                }
            }
            return true;
        }
        catch (ClassNotFoundException e)
        {
            Log.d("ProxySettings","Exception setting WebKit proxy on Lollipop through ProxyChangeListener: " + e.toString());
        }
        catch (NoSuchFieldException e)
        {
            Log.d("ProxySettings","Exception setting WebKit proxy on Lollipop through ProxyChangeListener: " + e.toString());
        }
        catch (IllegalAccessException e)
        {
            Log.d("ProxySettings","Exception setting WebKit proxy on Lollipop through ProxyChangeListener: " + e.toString());
        }
        catch (NoSuchMethodException e)
        {
            Log.d("ProxySettings","Exception setting WebKit proxy on Lollipop through ProxyChangeListener: " + e.toString());
        }
        catch (InvocationTargetException e)
        {
            Log.d("ProxySettings","Exception setting WebKit proxy on Lollipop through ProxyChangeListener: " + e.toString());
        }
        return false;
     }
    
    private static boolean sendProxyChangedIntent(Context ctx, String host, int port) 
    {

        try
        {
            Class proxyPropertiesClass = Class.forName("android.net.ProxyProperties");
            if (proxyPropertiesClass != null)
            {
                Constructor c = proxyPropertiesClass.getConstructor(String.class, Integer.TYPE,
                        String.class);
                
                if (c != null)
                {
                    c.setAccessible(true);
                    Object properties = c.newInstance(host, port, null);

                    Intent intent = new Intent(android.net.Proxy.PROXY_CHANGE_ACTION);
                    intent.putExtra("proxy",(Parcelable)properties);
                    ctx.sendBroadcast(intent);
                 
                }
                                
           }
        } catch (Exception e)
        {
            Log.e("ProxySettings",
                    "Exception sending Intent ",e);
        } catch (Error e)
        {
            Log.e("ProxySettings",
                    "Exception sending Intent ",e);
        }

        return false;

    }
    
    /**
    private static boolean setKitKatProxy0(Context ctx, String host, int port) 
    {
      
      try
        {
            Class cmClass = Class.forName("android.net.ConnectivityManager");

            Class proxyPropertiesClass = Class.forName("android.net.ProxyProperties");
            if (cmClass != null && proxyPropertiesClass != null)
            {
                Constructor c = proxyPropertiesClass.getConstructor(String.class, Integer.TYPE,
                        String.class);

                if (c != null)
                {
                    c.setAccessible(true);

                    Object proxyProps = c.newInstance(host, port, null);
                    ConnectivityManager cm =
                            (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

                    Method mSetGlobalProxy = cmClass.getDeclaredMethod("setGlobalProxy", proxyPropertiesClass);
                    
                    mSetGlobalProxy.invoke(cm, proxyProps);
                 
                    return true;
                }
                
           }
        } catch (Exception e)
        {
            Log.e("ProxySettings",
                    "ConnectivityManager.setGlobalProxy ",e);
        }

        return false;

    }
    */
  //CommandLine.initFromFile(COMMAND_LINE_FILE);
  
    /**
    private static boolean setKitKatProxy2 (Context ctx, String host, int port)
    {

      String commandLinePath = "/data/local/tmp/orweb.conf";
       try
         {
             Class webViewCoreClass = Class.forName("org.chromium.content.common.CommandLine");

             if (webViewCoreClass != null)
             {
               for (Method method : webViewCoreClass.getDeclaredMethods())
               {
                 Log.d("Orweb","Proxy methods: " + method.getName());
               }
               
                 Method m = webViewCoreClass.getDeclaredMethod("initFromFile", 
                     String.class);
                 
                 if (m != null)
                 {
                     m.setAccessible(true);
                     m.invoke(null, commandLinePath);
                     return true;
                 }
                 else
                     return false;
             }
         } catch (Exception e)
         {
             Log.e("ProxySettings",
                     "Exception setting WebKit proxy through android.net.ProxyProperties: "
                             + e.toString());
         } catch (Error e)
         {
             Log.e("ProxySettings",
                     "Exception setting WebKit proxy through android.webkit.Network: "
                             + e.toString());
         }
       
       return false;
    }
    
    /**
    private static boolean setKitKatProxy (Context ctx, String host, int port)
    {
      
       try
         {
             Class webViewCoreClass = Class.forName("android.net.Proxy");

             Class proxyPropertiesClass = Class.forName("android.net.ProxyProperties");
             if (webViewCoreClass != null && proxyPropertiesClass != null)
             {
               for (Method method : webViewCoreClass.getDeclaredMethods())
               {
                 Log.d("Orweb","Proxy methods: " + method.getName());
               }
               
                 Method m = webViewCoreClass.getDeclaredMethod("setHttpProxySystemProperty", 
                     proxyPropertiesClass);
                 Constructor c = proxyPropertiesClass.getConstructor(String.class, Integer.TYPE,
                         String.class);

                 if (m != null && c != null)
                 {
                     m.setAccessible(true);
                     c.setAccessible(true);
                     Object properties = c.newInstance(host, port, null);

                     m.invoke(null, properties);
                     return true;
                 }
                 else
                     return false;
             }
         } catch (Exception e)
         {
             Log.e("ProxySettings",
                     "Exception setting WebKit proxy through android.net.ProxyProperties: "
                             + e.toString());
         } catch (Error e)
         {
             Log.e("ProxySettings",
                     "Exception setting WebKit proxy through android.webkit.Network: "
                             + e.toString());
         }
       
       return false;
    }
    
    private static boolean resetProxyForKitKat ()
    {
      
       try
         {
             Class webViewCoreClass = Class.forName("android.net.Proxy");

             Class proxyPropertiesClass = Class.forName("android.net.ProxyProperties");
             if (webViewCoreClass != null && proxyPropertiesClass != null)
             {
               for (Method method : webViewCoreClass.getDeclaredMethods())
               {
                 Log.d("Orweb","Proxy methods: " + method.getName());
               }
               
                 Method m = webViewCoreClass.getDeclaredMethod("setHttpProxySystemProperty", 
                     proxyPropertiesClass);

                 if (m != null)
                 {
                     m.setAccessible(true);

                     m.invoke(null, null);
                     return true;
                 }
                 else
                     return false;
             }
         } catch (Exception e)
         {
             Log.e("ProxySettings",
                     "Exception setting WebKit proxy through android.net.ProxyProperties: "
                             + e.toString());
         } catch (Error e)
         {
             Log.e("ProxySettings",
                     "Exception setting WebKit proxy through android.webkit.Network: "
                             + e.toString());
         }
       
       return false;
    }**/

    public static void resetProxy(String appClass, Context ctx) throws Exception {

        resetSystemProperties();

        if (Build.VERSION.SDK_INT < 14)
        {
            resetProxyForGingerBread(ctx);
        }
        else  if (Build.VERSION.SDK_INT < 19)
        {
            resetProxyForICS();
        }
        else
        {
            resetKitKatProxy(appClass, ctx);
        }
         
    }

    private static void resetProxyForICS() throws Exception{
        try
        {
            Class webViewCoreClass = Class.forName("android.webkit.WebViewCore");
            Class proxyPropertiesClass = Class.forName("android.net.ProxyProperties");
            if (webViewCoreClass != null && proxyPropertiesClass != null)
            {
                Method m = webViewCoreClass.getDeclaredMethod("sendStaticMessage", Integer.TYPE,
                        Object.class);

                if (m != null)
                {
                    m.setAccessible(true);

                    // android.webkit.WebViewCore.EventHub.PROXY_CHANGED = 193;
                    m.invoke(null, 193, null);
                }
            }
        } catch (Exception e)
        {
            Log.e("ProxySettings",
                    "Exception setting WebKit proxy through android.net.ProxyProperties: "
                            + e.toString());
            throw e;
        } catch (Error e)
        {
            Log.e("ProxySettings",
                    "Exception setting WebKit proxy through android.webkit.Network: "
                            + e.toString());
            throw e;
        }
    }

    private static void resetProxyForGingerBread(Context ctx) throws Exception {
        Object requestQueueObject = getRequestQueue(ctx);
        if (requestQueueObject != null) {
            setDeclaredField(requestQueueObject, "mProxyHost", null);
        }
    }

    public static Object getRequestQueue(Context ctx) throws Exception {
        Object ret = null;
        Class networkClass = Class.forName("android.webkit.Network");
        if (networkClass != null) {
            Object networkObj = invokeMethod(networkClass, "getInstance", new Object[] {
                ctx
            }, Context.class);
            if (networkObj != null) {
                ret = getDeclaredField(networkObj, "mRequestQueue");
            }
        }
        return ret;
    }

    private static Object getDeclaredField(Object obj, String name)
            throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        Object out = f.get(obj);
        // System.out.println(obj.getClass().getName() + "." + name + " = "+
        // out);
        return out;
    }

    private static void setDeclaredField(Object obj, String name, Object value)
            throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(obj, value);
    }

    private static Object invokeMethod(Object object, String methodName, Object[] params,
            Class... types) throws Exception {
        Object out = null;
        Class c = object instanceof Class ? (Class) object : object.getClass();
        if (types != null) {
            Method method = c.getMethod(methodName, types);
            out = method.invoke(object, params);
        } else {
            Method method = c.getMethod(methodName);
            out = method.invoke(object);
        }
        // System.out.println(object.getClass().getName() + "." + methodName +
        // "() = "+ out);
        return out;
    }

    public static Socket getSocket(Context context, String proxyHost, int proxyPort)
            throws IOException
    {
        Socket sock = new Socket();

        sock.connect(new InetSocketAddress(proxyHost, proxyPort), 10000);

        return sock;
    }

    public static Socket getSocket(Context context) throws IOException
    {
        return getSocket(context, DEFAULT_HOST, DEFAULT_SOCKS_PORT);

    }

    public static AlertDialog initOrbot(Activity activity,
            CharSequence stringTitle,
            CharSequence stringMessage,
            CharSequence stringButtonYes,
            CharSequence stringButtonNo,
            CharSequence stringDesiredBarcodeFormats) {
        Intent intentScan = new Intent("org.torproject.android.START_TOR");
        intentScan.addCategory(Intent.CATEGORY_DEFAULT);

        try {
            activity.startActivityForResult(intentScan, REQUEST_CODE);
            return null;
        } catch (ActivityNotFoundException e) {
            return showDownloadDialog(activity, stringTitle, stringMessage, stringButtonYes,
                    stringButtonNo);
        }
    }

    private static AlertDialog showDownloadDialog(final Activity activity,
            CharSequence stringTitle,
            CharSequence stringMessage,
            CharSequence stringButtonYes,
            CharSequence stringButtonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(activity);
        downloadDialog.setTitle(stringTitle);
        downloadDialog.setMessage(stringMessage);
        downloadDialog.setPositiveButton(stringButtonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:org.torproject.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                activity.startActivity(intent);
            }
        });
        downloadDialog.setNegativeButton(stringButtonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }
    
    

}
