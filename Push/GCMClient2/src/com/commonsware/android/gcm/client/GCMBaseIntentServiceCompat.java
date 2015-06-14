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
    https://commonsware.com/Android
 */

package com.commonsware.android.gcm.client;

import android.content.Intent;
import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.google.android.gms.gcm.GoogleCloudMessaging;

abstract public class GCMBaseIntentServiceCompat extends
    WakefulIntentService {
  abstract protected void onMessage(Intent message);

  abstract protected void onError(Intent message);

  abstract protected void onDeleted(Intent message);

  public GCMBaseIntentServiceCompat(String name) {
    super(name);
  }

  @Override
  protected void doWakefulWork(Intent i) {
    GoogleCloudMessaging gcm=GoogleCloudMessaging.getInstance(this);
    String messageType=gcm.getMessageType(i);

    if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
      onError(i);
    }
    else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
      onDeleted(i);
    }
    else {
      onMessage(i);
    }
  }
}
