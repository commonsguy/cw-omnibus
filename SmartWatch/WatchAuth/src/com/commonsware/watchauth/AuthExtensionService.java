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

import com.sonyericsson.extras.liveware.extension.util.ExtensionService;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.registration.DeviceInfo;
import com.sonyericsson.extras.liveware.extension.util.registration.DisplayInfo;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationAdapter;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;

public class AuthExtensionService extends ExtensionService {
  public static final String EXTENSION_KEY=
      "com.commonsware.watchauth.key";

  public AuthExtensionService() {
    super(EXTENSION_KEY);
  }

  @Override
  protected RegistrationInformation getRegistrationInformation() {
    return(new AuthRegistrationInformation(this));
  }

  @Override
  protected boolean keepRunningWhenConnected() {
    return(false);
  }

  @Override
  public ControlExtension createControlExtension(String hostAppPackageName) {
    final int w=AuthSmartWatch.getSupportedControlWidth(this);
    final int h=AuthSmartWatch.getSupportedControlHeight(this);

    for (DeviceInfo device : RegistrationAdapter.getHostApplication(this,
                                                                    hostAppPackageName)
                                                .getDevices()) {
      for (DisplayInfo display : device.getDisplays()) {
        if (display.sizeEquals(w, h)) {
          return(new AuthSmartWatch(hostAppPackageName, this));
        }
      }
    }
    
    throw new IllegalArgumentException("No properly-sized control for: "+ hostAppPackageName);
  }
}
