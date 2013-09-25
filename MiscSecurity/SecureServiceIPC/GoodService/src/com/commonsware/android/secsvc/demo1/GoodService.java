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

package com.commonsware.android.secsvc.demo1;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.commonsware.android.secsvc.SomethingUseful;

public class GoodService extends Service {

  @Override
  public IBinder onBind(Intent arg0) {
    return(new GoodBinder());
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(getClass().getSimpleName(), "It's all good");

    stopSelf();
    
    return(START_NOT_STICKY);
  }

  static class GoodBinder extends SomethingUseful.Stub {
    @Override
    public void hi() throws RemoteException {
      Log.d(getClass().getSimpleName(), "It's all good");
    }
  }
}
