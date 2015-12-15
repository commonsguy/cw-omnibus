/***
 Copyright (c) 2012-2015 CommonsWare, LLC
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

package com.commonsware.android.backup;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.File;
import de.greenrobot.event.EventBus;
import okio.BufferedSink;
import okio.Okio;

public class RestoreService extends IntentService {
  public RestoreService() {
    super("RestoreService");
  }

  @Override
  protected void onHandleIntent(Intent i) {
    Request request=new Request.Builder()
      .url(i.getData().toString())
      .build();

    try {
      Response response=
        BackupService.OKHTTP_CLIENT.newCall(request).execute();
      File toRestore=new File(getCacheDir(), "backup.zip");

      if (toRestore.exists()) {
        toRestore.delete();
      }

      BufferedSink sink = Okio.buffer(Okio.sink(toRestore));

      sink.writeAll(response.body().source());
      sink.close();

      ZipUtils.unzip(toRestore, getFilesDir(),
        BackupService.ZIP_PREFIX_FILES);
      ZipUtils.unzip(toRestore,
        BackupService.getSharedPrefsDir(this),
        BackupService.ZIP_PREFIX_PREFS);
      ZipUtils.unzip(toRestore, getExternalFilesDir(null),
        BackupService.ZIP_PREFIX_EXTERNAL);

      EventBus.getDefault().post(new RestoreCompletedEvent());
    }
    catch (Exception e) {
      Log.e(getClass().getSimpleName(),
        "Exception restoring backup", e);
      EventBus.getDefault().post(new RestoreFailedEvent());
    }
  }

  static class RestoreCompletedEvent {

  }

  static class RestoreFailedEvent {

  }
}
