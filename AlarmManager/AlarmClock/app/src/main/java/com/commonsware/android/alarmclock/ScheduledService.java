/***
  Copyright (c) 2013-2015 CommonsWare, LLC
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

package com.commonsware.android.alarmclock;

import android.content.Intent;
import android.util.Log;
import com.commonsware.cwac.wakeful.WakefulIntentService;
import org.greenrobot.eventbus.EventBus;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Random;

public class ScheduledService extends WakefulIntentService {
  private Random rng=new Random();

  public ScheduledService() {
    super("ScheduledService");
  }

  @Override
  protected void doWakefulWork(Intent intent) {
    RandomEvent event=new RandomEvent(rng.nextInt());
    File log=new File(getExternalFilesDir(null), "alarmclock-log.txt");

    log.getParentFile().mkdirs();

    EventBus.getDefault().post(event);
    append(log, event);
  }

  private void append(File f, RandomEvent event) {
    try {
      FileOutputStream fos=new FileOutputStream(f, true);
      Writer osw=new OutputStreamWriter(fos);

      osw.write(event.when.toString());
      osw.write(" : ");
      osw.write(Integer.toHexString(event.value));
      osw.write('\n');
      osw.flush();
      fos.flush();
      fos.getFD().sync();
      fos.close();

      Log.d(getClass().getSimpleName(),
        "logged to "+f.getAbsolutePath());
    }
    catch (IOException e) {
      Log.e(getClass().getSimpleName(),
        "Exception writing to file", e);
    }
  }
}
