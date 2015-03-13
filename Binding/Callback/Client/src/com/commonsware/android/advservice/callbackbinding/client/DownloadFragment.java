/***
 Copyright (c) 2008-2014 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 From _The Busy Coder's Guide to Advanced Android Development_
 http://commonsware.com/AdvAndroid
 */

package com.commonsware.android.advservice.callbackbinding.client;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.commonsware.android.advservice.callbackbinding.IDownload;
import com.commonsware.android.advservice.callbackbinding.IDownloadCallback;
import java.util.List;
import de.greenrobot.event.EventBus;

public class DownloadFragment extends Fragment implements
    OnClickListener, ServiceConnection {
  private static final String TO_DOWNLOAD="https://commonsware.com/Android/excerpt.pdf";
  private IDownload binding=null;
  private Button btn=null;
  private Application appContext=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setRetainInstance(true);
    EventBus.getDefault().register(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    View result=inflater.inflate(R.layout.main, container, false);

    btn=(Button)result.findViewById(R.id.go);
    btn.setOnClickListener(this);
    btn.setEnabled(binding!=null);

    return(result);
  }

  @Override
  public void onAttach(Activity host) {
    super.onAttach(host);

    appContext=(Application)host.getApplicationContext();

    Intent implicit=new Intent(IDownload.class.getName());
    List<ResolveInfo> matches=host.getPackageManager()
                                  .queryIntentServices(implicit, 0);

    if (matches.size()==0) {
      Toast.makeText(host, "Cannot find a matching service!",
                      Toast.LENGTH_LONG).show();
    }
    else if (matches.size()>1) {
      Toast.makeText(host, "Found multiple matching services!",
                      Toast.LENGTH_LONG).show();
    }
    else {
      Intent explicit=new Intent(implicit);
      ServiceInfo svcInfo=matches.get(0).serviceInfo;
      ComponentName cn=new ComponentName(svcInfo.applicationInfo.packageName,
                                         svcInfo.name);

      explicit.setComponent(cn);
      appContext.bindService(explicit, this, Context.BIND_AUTO_CREATE);
    }
  }

  @Override
  public void onDestroy() {
    appContext.unbindService(this);
    disconnect();

    EventBus.getDefault().unregister(this);

    super.onDestroy();
  }

  @Override
  public void onClick(View view) {
    try {
      binding.download(TO_DOWNLOAD, cb);
    }
    catch (RemoteException e) {
      Log.e(getClass().getSimpleName(), "Exception requesting download", e);
      Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
    }
  }

  @Override
  public void onServiceConnected(ComponentName className, IBinder binder) {
    binding=IDownload.Stub.asInterface(binder);
    btn.setEnabled(true);
  }

  @Override
  public void onServiceDisconnected(ComponentName className) {
    disconnect();
  }

  public void onEventMainThread(CallbackEvent event) {
    if (getActivity()!=null) {
      if (event.succeeded) {
        Toast.makeText(getActivity(), "Download successful!", Toast.LENGTH_LONG).show();
      }
      else {
        Toast.makeText(getActivity(), event.msg, Toast.LENGTH_LONG).show();
      }
    }
  }

  private void disconnect() {
    binding=null;
    btn.setEnabled(false);
  }

  IDownloadCallback.Stub cb=new IDownloadCallback.Stub() {
    @Override
    public void onSuccess() throws RemoteException {
      EventBus.getDefault().post(new CallbackEvent(true, null));
    }

    @Override
    public void onFailure(String msg) throws RemoteException {
      EventBus.getDefault().post(new CallbackEvent(false, msg));
    }
  };

  static class CallbackEvent {
    boolean succeeded=false;
    String msg=null;

    CallbackEvent(boolean succeeded, String msg) {
      this.succeeded=succeeded;
      this.msg=msg;
    }
  }
}
