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

package com.commonsware.android.smartwatch;

import com.sonyericsson.extras.liveware.aef.registration.Registration;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;
import android.content.ContentValues;
import android.content.Context;

public class DemoRegistrationInformation extends
    RegistrationInformation {
  final Context ctxt;

  protected DemoRegistrationInformation(Context ctxt) {
    this.ctxt=ctxt;
  }

  @Override
  public int getRequiredControlApiVersion() {
    return(1);
  }

  @Override
  public int getRequiredSensorApiVersion() {
    return(0);
  }

  @Override
  public int getRequiredNotificationApiVersion() {
    return(0);
  }

  @Override
  public int getRequiredWidgetApiVersion() {
    return(0);
  }

  @Override
  public ContentValues getExtensionRegistrationConfiguration() {
    ContentValues values=new ContentValues();

    values.put(Registration.ExtensionColumns.NAME,
               ctxt.getString(R.string.extension_name));
    values.put(Registration.ExtensionColumns.EXTENSION_KEY,
               DemoExtensionService.EXTENSION_KEY);
    values.put(Registration.ExtensionColumns.HOST_APP_ICON_URI,
               ExtensionUtils.getUriString(ctxt, R.drawable.ic_launcher));
    values.put(Registration.ExtensionColumns.EXTENSION_ICON_URI,
               ExtensionUtils.getUriString(ctxt,
                                           R.drawable.ic_extension));
    values.put(Registration.ExtensionColumns.NOTIFICATION_API_VERSION,
               getRequiredNotificationApiVersion());
    values.put(Registration.ExtensionColumns.PACKAGE_NAME,
               ctxt.getPackageName());

    return(values);
  }

  @Override
  public boolean isDisplaySizeSupported(int width, int height) {
    return((width == DemoExtension.getSupportedControlWidth(ctxt)) && (height == DemoExtension.getSupportedControlHeight(ctxt)));
  }
}
