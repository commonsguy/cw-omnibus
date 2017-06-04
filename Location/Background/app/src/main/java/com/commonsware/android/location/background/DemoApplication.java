/***
 Copyright (c) 2017 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.location.background;

import android.app.Application;
import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;

public class DemoApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();

    LogConfiguration config=new LogConfiguration.Builder()
      .logLevel(BuildConfig.DEBUG ? LogLevel.ALL : LogLevel.NONE)
      .tag("LocationPoller")
      .build();
    Printer filePrinter=
      new FilePrinter.Builder(getExternalFilesDir(null).getAbsolutePath())
        .fileNameGenerator(new DateFileNameGenerator())
        .build();

    XLog.init(config, new AndroidPrinter(), filePrinter);
  }
}
