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

package com.commonsware.android.advservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import bsh.EvalError;
import bsh.Interpreter;

public class BshService extends Service {
  @Override
  public IBinder onBind(Intent intent) {
    return(new BshBinder(this));
  }

  private static class BshBinder extends IScript.Stub {
    private Interpreter i=new Interpreter();

    BshBinder(Context ctxt) {
      try {
        i.set("context", ctxt);
      }
      catch (EvalError e) {
        Log.e("BshService", "Error executing script", e);
      }
    }

    @Override
    public void executeScript(String script) {
      try {
        i.eval(script);
      }
      catch (bsh.EvalError e) {
        Log.e("BshService", "Error executing script", e);
      }
    }
  };
}