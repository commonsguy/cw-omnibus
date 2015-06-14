/***
  Copyright (c) 2012 CommonsWare, LLC
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

package com.commonsware.watchauth;

import android.app.admin.DeviceAdminReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class AuthAdminReceiver extends DeviceAdminReceiver {
  @Override
  public void onEnabled(Context ctxt, Intent intent) {
    controlUnlockReceiver(ctxt, true);
  }

  @Override
  public void onDisabled(Context ctxt, Intent intent) {
    controlUnlockReceiver(ctxt, false);
  }

  private void controlUnlockReceiver(Context ctxt, boolean enabled) {
    PackageManager mgr=ctxt.getPackageManager();
    int state=
        enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;

    mgr.setComponentEnabledSetting(new ComponentName(
                                                     ctxt,
                                                     UnlockReceiver.class),
                                   state, PackageManager.DONT_KILL_APP);
  }
}
