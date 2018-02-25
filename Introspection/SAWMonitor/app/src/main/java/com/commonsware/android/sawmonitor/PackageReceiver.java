/***
 Copyright (c) 2016-2017 CommonsWare, LLC
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

package com.commonsware.android.sawmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import java.util.HashMap;
import static android.content.Intent.ACTION_PACKAGE_ADDED;
import static android.content.Intent.ACTION_PACKAGE_REPLACED;

public class PackageReceiver extends BroadcastReceiver {
  private static final long ADD_THEN_REPLACE_DELTA=2000L;
  private static HashMap<String, Long> ADD_TIMESTAMPS=new HashMap<>();

  @Override
  public void onReceive(Context ctxt, Intent intent) {
    String pkg=intent.getData().getSchemeSpecificPart();

    if (ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
      ADD_TIMESTAMPS.put(pkg, SystemClock.uptimeMillis());
      SAWDetector.seeSAW(ctxt, pkg, getOperationMessage(ctxt, false));
    }
    else if (ACTION_PACKAGE_REPLACED.equals(intent.getAction())) {
      Long added=ADD_TIMESTAMPS.get(pkg);

      if (added==null ||
        (SystemClock.uptimeMillis()-added)>ADD_THEN_REPLACE_DELTA) {
        SAWDetector.seeSAW(ctxt, pkg, getOperationMessage(ctxt, true));

        if (added!=null) {
          ADD_TIMESTAMPS.remove(pkg);
        }
      }
    }
  }

  private String getOperationMessage(Context ctxt, boolean isReplace) {
    return(isReplace ?
      ctxt.getString(R.string.msg_upgraded) :
      ctxt.getString(R.string.msg_installed));
  }
}
