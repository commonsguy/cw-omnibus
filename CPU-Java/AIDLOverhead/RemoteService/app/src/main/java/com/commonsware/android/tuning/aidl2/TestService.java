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

package com.commonsware.android.tuning.aidl2;

import com.commonsware.android.tuning.ITestService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TestService extends Service {
  private final ITestService.Stub binder=new ITestService.Stub() {
    public void doSomething() {
      // TODO
    }
  };

  @Override
  public IBinder onBind(Intent intent) {
    return(binder);
  }
}
