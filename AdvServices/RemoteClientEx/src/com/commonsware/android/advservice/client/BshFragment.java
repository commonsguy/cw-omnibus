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
    http://commonsware.com/Android
 */

package com.commonsware.android.advservice.client;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.commonsware.android.advservice.IScript;
import com.commonsware.android.advservice.IScriptResult;

public class BshFragment extends Fragment implements OnClickListener,
    ServiceConnection {
  private IScript service=null;
  private Button btn=null;

  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    View result=inflater.inflate(R.layout.main, container, false);

    btn=(Button)result.findViewById(R.id.eval);
    btn.setOnClickListener(this);
    btn.setEnabled((service!=null));

    return(result);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    setRetainInstance(true);
    getActivity().getApplicationContext()
                 .bindService(new Intent(
                                         "com.commonsware.android.advservice.IScript"),
                              this, Context.BIND_AUTO_CREATE);
  }

  @Override
  public void onDestroy() {
    getActivity().getApplicationContext().unbindService(this);

    super.onDestroy();
  }

  @Override
  public void onClick(View view) {
    EditText script=(EditText)getView().findViewById(R.id.script);
    String src=script.getText().toString();

    try {
      service.executeScript(src, callback);
    }
    catch (RemoteException e) {
      Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG)
           .show();
    }
  }

  @Override
  public void onServiceConnected(ComponentName className, IBinder binder) {
    service=IScript.Stub.asInterface(binder);
    btn.setEnabled(true);
  }

  @Override
  public void onServiceDisconnected(ComponentName className) {
    service=null;
  }

  private final IScriptResult.Stub callback=new IScriptResult.Stub() {
    public void success(final String result) {
      getActivity().runOnUiThread(new Runnable() {
        public void run() {
          Toast.makeText(getActivity(), result, Toast.LENGTH_LONG)
               .show();
        }
      });
    }

    public void failure(final String error) {
      getActivity().runOnUiThread(new Runnable() {
        public void run() {
          Toast.makeText(getActivity(), error, Toast.LENGTH_LONG)
               .show();
        }
      });
    }
  };
}
