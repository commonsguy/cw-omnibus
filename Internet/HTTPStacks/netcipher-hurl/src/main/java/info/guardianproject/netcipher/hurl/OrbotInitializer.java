/***
 Copyright (c) 2016 CommonsWare, LLC
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

package info.guardianproject.netcipher.hurl;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import com.commonsware.cwac.security.SignatureUtils;
import java.util.ArrayList;
import info.guardianproject.netcipher.proxy.OrbotHelper;

/**
 * More helper code, to make using NetCipher simpler. Could
 * be integrated with OrbotHelper, at least in theory.
 */
public class OrbotInitializer {
  private final Context ctxt;
  private final Handler handler;
  private boolean isInstalled=false;
  private Intent lastStatusIntent=null;
  private WeakSet<StatusCallback> statusCallbacks=new WeakSet<>();
  private WeakSet<InstallCallback> installCallbacks=new WeakSet<>();
  private long statusTimeoutMs=30000L;
  private long installTimeoutMs=60000L;

  abstract public static class SimpleStatusCallback
    implements StatusCallback {
    @Override
    public void onEnabled(Intent statusIntent) {
      // no-op; extend and override if needed
    }

    @Override
    public void onStarting() {
      // no-op; extend and override if needed
    }

    @Override
    public void onStopping() {
      // no-op; extend and override if needed
    }

    @Override
    public void onDisabled() {
      // no-op; extend and override if needed
    }

    @Override
    public void onNotYetInstalled() {
      // no-op; extend and override if needed
    }
  }

  /**
   * Callback interface used for reporting the results of an
   * attempt to install Orbot
   */
  public interface InstallCallback {
    void onInstalled();
    void onInstallTimeout();
  }

  private static volatile OrbotInitializer INSTANCE;

  /**
   * Retrieves the singleton, initializing if if needed
   *
   * @param ctxt any Context will do, as we will hold onto
   *             the Application
   * @return the singleton
   */
  synchronized public static OrbotInitializer get(Context ctxt) {
    if (INSTANCE==null) {
      INSTANCE=new OrbotInitializer(ctxt);
    }

    return(INSTANCE);
  }

  /**
   * Standard constructor
   *
   * @param ctxt any Context will do; OrbotInitializer will hold
   *             onto the Application context
   */
  private OrbotInitializer(Context ctxt) {
    this.ctxt=ctxt.getApplicationContext();
    this.handler=new Handler(Looper.getMainLooper());
  }

  /**
   * Adds a StatusCallback to be called when we find out that
   * Orbot is ready. If Orbot is ready for use, your callback
   * will be called with onEnabled() immediately, before this
   * method returns.
   *
   * @param cb a callback
   * @return the singleton, for chaining
   */
  public OrbotInitializer addStatusCallback(StatusCallback cb) {
    statusCallbacks.add(cb);

    if (lastStatusIntent!=null) {
      String status=
        lastStatusIntent.getStringExtra(OrbotHelper.EXTRA_STATUS);

      if (status.equals(OrbotHelper.STATUS_ON)) {
        cb.onEnabled(lastStatusIntent);
      }
    }

    return(this);
  }

  /**
   * Removes an existing registered StatusCallback.
   *
   * @param cb the callback to remove
   * @return the singleton, for chaining
   */
  public OrbotInitializer removeStatusCallback(StatusCallback cb) {
    statusCallbacks.remove(cb);

    return(this);
  }


  /**
   * Adds an InstallCallback to be called when we find out that
   * Orbot is installed
   *
   * @param cb a callback
   * @return the singleton, for chaining
   */
  public OrbotInitializer addInstallCallback(InstallCallback cb) {
    installCallbacks.add(cb);

    return(this);
  }

  /**
   * Removes an existing registered InstallCallback.
   *
   * @param cb the callback to remove
   * @return the singleton, for chaining
   */
  public OrbotInitializer removeInstallCallback(InstallCallback cb) {
    installCallbacks.remove(cb);

    return(this);
  }

  /**
   * Sets how long of a delay, in milliseconds, after trying
   * to get a status from Orbot before we give up.
   * Defaults to 30000ms = 30 seconds = 0.000347222 days
   *
   * @param timeoutMs delay period in milliseconds
   * @return the singleton, for chaining
   */
  public OrbotInitializer statusTimeout(long timeoutMs) {
    statusTimeoutMs=timeoutMs;

    return(this);
  }

  /**
   * Sets how long of a delay, in milliseconds, after trying
   * to install Orbot do we assume that it's not happening.
   * Defaults to 60000ms = 60 seconds = 1 minute = 1.90259e-6 years
   *
   * @param timeoutMs delay period in milliseconds
   * @return the singleton, for chaining
   */
  public OrbotInitializer installTimeout(long timeoutMs) {
    installTimeoutMs=timeoutMs;

    return(this);
  }

  /**
   * @return true if Orbot is installed (the last time we checked),
   * false otherwise
   */
  public boolean isInstalled() {
    return(isInstalled);
  }

  /**
   * Initializes the connection to Orbot, revalidating that it
   * is installed and requesting fresh status broadcasts.
   *
   * @return true if initialization is proceeding, false if
   * Orbot is not installed
   */
  public boolean init() {
    Intent orbot=OrbotHelper.getOrbotStartIntent(ctxt);
    ArrayList<String> hashes=new ArrayList<String>();

    hashes.add("A4:54:B8:7A:18:47:A8:9E:D7:F5:E7:0F:BA:6B:BA:96:F3:EF:29:C2:6E:09:81:20:4F:E3:47:BF:23:1D:FD:5B");
    hashes.add("A7:02:07:92:4F:61:FF:09:37:1D:54:84:14:5C:4B:EE:77:2C:55:C1:9E:EE:23:2F:57:70:E1:82:71:F7:CB:AE");

    orbot=
      SignatureUtils.validateBroadcastIntent(ctxt, orbot,
        hashes, false);

    if (orbot!=null) {
      isInstalled=true;
      handler.postDelayed(onStatusTimeout, statusTimeoutMs);
      ctxt.registerReceiver(orbotStatusReceiver,
        new IntentFilter(OrbotHelper.ACTION_STATUS));
      ctxt.sendBroadcast(orbot);
    }
    else {
      isInstalled=false;

      for (StatusCallback cb : statusCallbacks) {
        cb.onNotYetInstalled();
      }
    }

    return(isInstalled);
  }

  /**
   * Given that init() returned false, calling installOrbot()
   * will trigger an attempt to install Orbot from an available
   * distribution channel (e.g., the Play Store). Only call this
   * if the user is expecting it, such as in response to tapping
   * a dialog button or an action bar item.
   *
   * Note that installation may take a long time, even if
   * the user is proceeding with the installation, due to network
   * speeds, waiting for user input, and so on. Either specify
   * a long timeout, or consider the timeout to be merely advisory
   * and use some other user input to cause you to try
   * init() again after, presumably, Orbot has been installed
   * and configured by the user.
   *
   * If the user does install Orbot, we will attempt init()
   * again automatically. Hence, you will probably need user input
   * to tell you when the user has gotten Orbot up and going.
   *
   * @param host the Activity that is triggering this work
   */
  public void installOrbot(Activity host) {
    handler.postDelayed(onInstallTimeout, installTimeoutMs);

    IntentFilter filter=
      new IntentFilter(Intent.ACTION_PACKAGE_ADDED);

    filter.addDataScheme("package");

    ctxt.registerReceiver(orbotInstallReceiver, filter);
    host.startActivity(OrbotHelper.getOrbotInstallIntent(ctxt));
  }

  private BroadcastReceiver orbotStatusReceiver=new BroadcastReceiver() {
    @Override
    public void onReceive(Context ctxt, Intent intent) {
      if (TextUtils.equals(intent.getAction(),
        OrbotHelper.ACTION_STATUS)) {
        String status=intent.getStringExtra(OrbotHelper.EXTRA_STATUS);

        if (status.equals(OrbotHelper.STATUS_ON)) {
          lastStatusIntent=intent;
          handler.removeCallbacks(onStatusTimeout);

          for (StatusCallback cb : statusCallbacks) {
            cb.onEnabled(intent);
          }
        }
        else if (status.equals(OrbotHelper.STATUS_OFF)) {
          for (StatusCallback cb : statusCallbacks) {
            cb.onDisabled();
          }
        }
        else if (status.equals(OrbotHelper.STATUS_STARTING)) {
          for (StatusCallback cb : statusCallbacks) {
            cb.onStarting();
          }
        }
        else if (status.equals(OrbotHelper.STATUS_STOPPING)) {
          for (StatusCallback cb : statusCallbacks) {
            cb.onStopping();
          }
        }
      }
    }
  };

  private Runnable onStatusTimeout=new Runnable() {
    @Override
    public void run() {
      ctxt.unregisterReceiver(orbotStatusReceiver);

      for (StatusCallback cb : statusCallbacks) {
        cb.onStatusTimeout();
      }
    }
  };

  private BroadcastReceiver orbotInstallReceiver=new BroadcastReceiver() {
    @Override
    public void onReceive(Context ctxt, Intent intent) {
      if (TextUtils.equals(intent.getAction(),
        Intent.ACTION_PACKAGE_ADDED)) {
        String pkgName=intent.getData().getEncodedSchemeSpecificPart();

        if (OrbotHelper.ORBOT_PACKAGE_NAME.equals(pkgName)) {
          isInstalled=true;
          handler.removeCallbacks(onInstallTimeout);
          ctxt.unregisterReceiver(orbotInstallReceiver);

          for (InstallCallback cb : installCallbacks) {
            cb.onInstalled();
          }

          init();
        }
      }
    }
  };

  private Runnable onInstallTimeout=new Runnable() {
    @Override
    public void run() {
      ctxt.unregisterReceiver(orbotInstallReceiver);

      for (InstallCallback cb : installCallbacks) {
        cb.onInstallTimeout();
      }
    }
  };
}
