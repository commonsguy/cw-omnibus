/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Advanced Android Development_
    http://commonsware.com/AdvAndroid
*/

package com.commonsware.android.advservice;

import android.app.Activity;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import bsh.Interpreter;

public class BshService extends IntentService {
  private static final String SCRIPT="com.commonsware.SCRIPT";
  private static final String BROADCAST_ACTION="com.commonsware.BROADCAST_ACTION";
  private static final String BROADCAST_PACKAGE="com.commonsware.BROADCAST_PACKAGE";
  private static final String PENDING_RESULT="com.commonsware.PENDING_RESULT";
  private static final String PAYLOAD="com.commonsware.PAYLOAD";
  private static final String RESULT_CODE="com.commonsware.RESULT_CODE";
  private static final int SUCCESS=1337;
  private Interpreter i=new Interpreter();
  
  public BshService() {
    super("BshService");
  }
  
  @Override
  public void onCreate() {
    super.onCreate();
    
    try {
      i.set("context", this);
    }
    catch (bsh.EvalError e) {
      Log.e("BshService", "Error executing script", e);
    }
  }
  
  @Override
  protected void onHandleIntent(Intent intent) {
    String script=intent.getStringExtra(SCRIPT);
    
    if (script!=null) {
      try {
        success(intent, i.eval(script).toString());
      }
      catch (Throwable e) {
        Log.e("BshService", "Error executing script", e);
        
        try {
          failure(intent, e.getMessage());
        }
        catch (Throwable t) {
          Log.e("BshService",
                "Error returning exception to client",
                t);
        }
      }
    }
  }
  
  private void success(Intent intent, String result) {
    send(intent, result, SUCCESS);
  }
  
  private void failure(Intent intent, String error) {
    send(intent, error, -1);
  }
  
  private void send(Intent intent, String result, int code) {
    String broadcast=intent.getStringExtra(BROADCAST_ACTION);
    Intent data=new Intent();
    
    data.putExtra(PAYLOAD, result);
    data.putExtra(RESULT_CODE, code);
    
    if (broadcast==null) {
      PendingIntent pi=(PendingIntent)intent.getParcelableExtra(PENDING_RESULT);
      
      if (pi!=null) {
        try {
          pi.send(this, Activity.RESULT_OK, data);
        }
        catch (PendingIntent.CanceledException e) {
          // no-op -- client must be gone
        }
      }
    }
    else {
      data.setPackage(intent.getStringExtra(BROADCAST_PACKAGE));
      data.setAction(broadcast);

      sendBroadcast(data);
    }
  }
}