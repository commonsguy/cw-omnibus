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

package com.commonsware.android.sms.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

public class Monitor extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    Object[] rawMsgs=(Object[])intent.getExtras().get("pdus");
    
    for (Object raw : rawMsgs) {
      SmsMessage msg=SmsMessage.createFromPdu((byte[])raw);
      
      if (msg.getMessageBody().toUpperCase().contains("SEKRIT")) {
        Log.w("SMS:"+msg.getOriginatingAddress(),
              msg.getMessageBody());
        
        abortBroadcast();
      }
    }
  }
}