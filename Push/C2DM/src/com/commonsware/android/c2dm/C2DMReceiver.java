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

package com.commonsware.android.c2dm;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.android.c2dm.C2DMBaseReceiver;

public class C2DMReceiver extends C2DMBaseReceiver {
  public C2DMReceiver() {
    super("this.is.not@real.biz");
  }

  @Override
  public void onRegistered(Context context, String registrationId) {
    Log.w("C2DMReceiver-onRegistered", registrationId);
  }
  
  @Override
  public void onUnregistered(Context context) {
    Log.w("C2DMReceiver-onUnregistered", "got here!");
  }
  
  @Override
  public void onError(Context context, String errorId) {
    Log.w("C2DMReceiver-onError", errorId);
  }
  
  @Override
  protected void onMessage(Context context, Intent intent) {
    Log.w("C2DMReceiver", intent.getStringExtra("payload"));
  }
}