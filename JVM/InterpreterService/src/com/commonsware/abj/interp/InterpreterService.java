/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  Covered in detail in the book _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
*/

package com.commonsware.abj.interp;

import android.app.Activity;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

public class InterpreterService extends IntentService {
  public static final String SCRIPT="_script";
  public static final String BUNDLE="_bundle";
  public static final String RESULT="_result";
  public static final String BROADCAST_ACTION="com.commonsware.abj.interp.BROADCAST_ACTION";
  public static final String BROADCAST_PACKAGE="com.commonsware.abj.interp.BROADCAST_PACKAGE";
  public static final String PENDING_RESULT="com.commonsware.abj.interp.PENDING_RESULT";
  public static final String RESULT_CODE="com.commonsware.abj.interp.RESULT_CODE";
  public static final String ERROR="com.commonsware.abj.interp.ERROR";
  public static final String TRACE="com.commonsware.abj.interp.TRACE";
  public static final int SUCCESS=1337;
  public static final int FAILURE=-1;
  private HashMap<String, I_Interpreter> interpreters=new HashMap<String, I_Interpreter>();
  
  public InterpreterService() {
    super("InterpreterService");
  }
  
  @Override
  protected void onHandleIntent(Intent intent) {
    String action=intent.getAction();
    I_Interpreter interpreter=interpreters.get(action);  
    
    if (interpreter==null) {
      try {
        interpreter=(I_Interpreter)Class.forName(action).newInstance();
        interpreters.put(action, interpreter);
      }
      catch (Throwable t) {
        Log.e("InterpreterService", "Error creating interpreter", t);
      }
    }
    
    if (interpreter==null) {
      failure(intent, "Could not create interpreter: "+intent.getAction());
    }
    else {
      try {
        success(intent, interpreter.executeScript(intent.getBundleExtra(BUNDLE)));
      }
      catch (Throwable t) {
        Log.e("InterpreterService", "Error executing script", t);
        
        try {
          failure(intent, t);
        }
        catch (Throwable t2) {
          Log.e("InterpreterService",
                "Error returning exception to client",
                t2);
        }
      }
    }
  }
  
  private void success(Intent intent, Bundle result) {
    Intent data=new Intent();

    data.putExtras(result);
    data.putExtra(RESULT_CODE, SUCCESS);

    send(intent, data);
  }
  
  private void failure(Intent intent, String message) {
    Intent data=new Intent();

    data.putExtra(ERROR, message);
    data.putExtra(RESULT_CODE, FAILURE);

    send(intent, data);
  }
  
  private void failure(Intent intent, Throwable t) {
    Intent data=new Intent();

    data.putExtra(ERROR, t.getMessage());
    data.putExtra(TRACE, getStackTrace(t));
    data.putExtra(RESULT_CODE, FAILURE);

    send(intent, data);
  }
  
  private void send(Intent intent, Intent data) {
    String broadcast=intent.getStringExtra(BROADCAST_ACTION);
    
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
  
  private String getStackTrace(Throwable t) {
    final StringWriter result=new StringWriter();
    final PrintWriter printWriter=new PrintWriter(result);
    
    t.printStackTrace(printWriter);
    
    return(result.toString());
  }
}