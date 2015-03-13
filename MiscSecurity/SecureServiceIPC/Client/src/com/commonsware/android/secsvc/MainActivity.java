/***
  Copyright (c) 2013 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
 */

package com.commonsware.android.secsvc;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity implements
    ServiceConnection, OnCheckedChangeListener {
  private static final String ACTION_SERVICE=
      "com.commonsware.android.secsvc.SOMETHING";
  private static final Intent INTENT_SERVICE=new Intent(ACTION_SERVICE);
  private CompoundButton verify=null;
  private View command=null;
  private View binding=null;
  private Intent serviceIntent=null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    verify=(CompoundButton)findViewById(R.id.verify);
    verify.setOnCheckedChangeListener(this);
    
    command=findViewById(R.id.command);
    binding=findViewById(R.id.binding);
    
    serviceIntent=buildServiceIntent();
  }

  public void testCommand(View v) {
    startService(serviceIntent);
  }

  public void testBinding(View v) {
    bindService(serviceIntent, this, BIND_AUTO_CREATE);
  }

  @Override
  public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
    serviceIntent=buildServiceIntent();

    command.setEnabled(serviceIntent != null);
    binding.setEnabled(serviceIntent != null);
  }

  @Override
  public void onServiceConnected(ComponentName arg0, IBinder arg1) {
    SomethingUseful binder=SomethingUseful.Stub.asInterface(arg1);

    try {
      binder.hi();
    }
    catch (RemoteException e) {
      Log.e(getClass().getSimpleName(),
            "Exception communicating with remote service", e);
    }

    unbindService(this);
  }

  @Override
  public void onServiceDisconnected(ComponentName arg0) {
    // no-op
  }

  Intent buildServiceIntent() {
    Intent result=null;

    if (verify.isChecked()) {
      PackageManager mgr=getPackageManager();

      for (ResolveInfo info : mgr.queryIntentServices(INTENT_SERVICE, 0)) {
        try {
          if (validate(info.serviceInfo.packageName,
                       R.raw.valid_signature)) {
            result=new Intent(INTENT_SERVICE);
            result.setComponent(new ComponentName(
                                                  info.serviceInfo.packageName,
                                                  info.serviceInfo.name));
            
            break;
          }
        }
        catch (Exception e) {
          Log.e(getClass().getSimpleName(),
                "Exception finding valid service", e);
        }
      }
    }
    else {
      result=INTENT_SERVICE;
    }

    return(result);
  }

  boolean validate(String pkg, int raw) throws NameNotFoundException,
                                       NotFoundException, IOException {
    PackageManager mgr=getPackageManager();
    PackageInfo pkgInfo=
        mgr.getPackageInfo(pkg, PackageManager.GET_SIGNATURES);
    Signature[] signatures=pkgInfo.signatures;
    byte[] local=signatures[0].toByteArray();

    return(isEqual(new ByteArrayInputStream(local),
                   getResources().openRawResource(raw)));
  }

  // from http://stackoverflow.com/a/4245881/115145

  private boolean isEqual(InputStream i1, InputStream i2)
                                                         throws IOException {
    byte[] buf1=new byte[1024];
    byte[] buf2=new byte[1024];

    try {
      DataInputStream d2=new DataInputStream(i2);
      int len;
      while ((len=i1.read(buf1)) >= 0) {
        d2.readFully(buf2, 0, len);
        for (int i=0; i < len; i++)
          if (buf1[i] != buf2[i])
            return false;
      }
      return d2.read() < 0; // is the end of the second file
                            // also.
    }
    catch (EOFException ioe) {
      return false;
    }
    finally {
      i1.close();
      i2.close();
    }
  }
}
