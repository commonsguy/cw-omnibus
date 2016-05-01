/***
  Copyright (c) 2008-2011 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _Tuning Android Applications_
    https://commonsware.com/AndTuning
*/

package com.commonsware.android.tuning.aidl;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import com.commonsware.android.tuning.ITestService;

public class AIDLOverheadClientActivity extends Activity {
  TextView out=null;
  
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        out=(TextView)findViewById(R.id.out);
        bindService(new Intent(AIDLOverheadClientActivity.this, TestService.class),
            svcConnLocal, BIND_AUTO_CREATE);
    }
    
    private ServiceConnection svcConn=new ServiceConnection() {
      @Override
      public void onServiceConnected(ComponentName name, IBinder service) {
        new RemoteTask(ITestService.Stub.asInterface(service)).execute();
      }

      @Override
      public void onServiceDisconnected(ComponentName name) {
      }
    };
    
    private ServiceConnection svcConnLocal=new ServiceConnection() {
      @Override
      public void onServiceConnected(ComponentName name, IBinder service) {
        new LocalTask((TestService.TestBinder)service).execute();
      }

      @Override
      public void onServiceDisconnected(ComponentName name) {
      }
    };
    
    private class RemoteTask extends AsyncTask<Void, Void, Void> {
      ITestService binder=null;
      long delta=0;
      
      RemoteTask(ITestService binder) {
        this.binder=binder;
      }
      
      @Override
      protected Void doInBackground(Void... params) {
        long start=SystemClock.uptimeMillis();
        
        for (int i=0;i<1000000;i++) {
          try {
            binder.doSomething();
          }
          catch (RemoteException e) {
            Log.e("AIDLOverhead", "Exception when calling remote service", e);
          }
        }
        
        delta=SystemClock.uptimeMillis()-start;
        
        return(null);
      }
      
      @Override
      protected void onPostExecute(Void unused) {
        out.setText(out.getText()+"\nRemote = "+String.valueOf(delta));
        unbindService(svcConn);
      }
    }
    
    private class LocalTask extends AsyncTask<Void, Void, Void> {
      TestService.TestBinder binder=null;
      long delta=0;
      
      LocalTask(TestService.TestBinder binder) {
        this.binder=binder;
      }
      
      @Override
      protected Void doInBackground(Void... params) {
        long start=SystemClock.uptimeMillis();
        
        for (int i=0;i<1000000;i++) {
          binder.doSomething();
        }
        
        delta=SystemClock.uptimeMillis()-start;
        
        return(null);
      }
      
      @Override
      protected void onPostExecute(Void unused) {
        out.setText(out.getText()+"\nLocal = "+String.valueOf(delta));
        unbindService(svcConnLocal);
        bindService(new Intent("com.commonsware.android.tuning.aidl2.TEST_SERVICE"),
            svcConn, BIND_AUTO_CREATE);
      }
    }
}