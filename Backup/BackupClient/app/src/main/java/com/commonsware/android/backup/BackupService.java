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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.util.Log;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import de.greenrobot.event.EventBus;

public class BackupService extends IntentService {
  static final OkHttpClient OKHTTP_CLIENT=new OkHttpClient();
  static final String ZIP_PREFIX_FILES="files/";
  static final String ZIP_PREFIX_PREFS="shared_prefs/";
  static final String ZIP_PREFIX_EXTERNAL="external/";
  private static final String BACKUP_FILENAME="backup.zip";
  private static final String BACKUP_PREFS_FILENAME=
    "com.commonsware.android.backup.BackupService.xml";
  private static final String PREF_LAST_BACKUP_DATASET=
    "lastBackupDataset";
  private static final String URL_CREATE_BACKUP=
    BuildConfig.URL_SERVER+"/api/backups";
  private static final String RESOURCE_DATASET="/dataset";
  private static final MediaType JSON=
    MediaType.parse("application/json; charset=utf-8");
  private static final MediaType ZIP=
    MediaType.parse("application/zip");

  static File getSharedPrefsDir(Context ctxt) {
    return(new File(new File(ctxt.getApplicationInfo().dataDir),
      "shared_prefs"));
  }

  public BackupService() {
    super("BackupService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    try {
      File backup=buildBackup();

      uploadBackup(backup);
      backup.delete();

      EventBus.getDefault().post(new BackupCompletedEvent());
    }
    catch (IOException e) {
      Log.e(getClass().getSimpleName(),
        "Exception creating ZIP file", e);
      EventBus.getDefault().post(new BackupFailedEvent());
    }
  }

  private File buildBackup() throws IOException {
    File zipFile=new File(getCacheDir(), BACKUP_FILENAME);

    if (zipFile.exists()) {
      zipFile.delete();
    }

    FileOutputStream fos=new FileOutputStream(zipFile);
    ZipOutputStream zos=new ZipOutputStream(fos);

    zipDir(ZIP_PREFIX_FILES, getFilesDir(), zos);
    zipDir(ZIP_PREFIX_PREFS, getSharedPrefsDir(this), zos);
    zipDir(ZIP_PREFIX_EXTERNAL, getExternalFilesDir(null), zos);
    zos.flush();
    fos.getFD().sync();
    zos.close();

    return(zipFile);
  }

  private void zipDir(String basePath, File dir,
                      ZipOutputStream zos) throws IOException {
    byte[] buf=new byte[16384];

    if (dir.listFiles()!=null) {
      for (File file : dir.listFiles()) {
        if (file.isDirectory()) {
          String path=basePath+file.getName()+"/";

          zos.putNextEntry(new ZipEntry(path));
          zipDir(path, file, zos);
          zos.closeEntry();
        }
        else if (!file.getName().equals(BACKUP_PREFS_FILENAME)) {
          FileInputStream fin=new FileInputStream(file);
          int length;

          zos.putNextEntry(
            new ZipEntry(basePath+file.getName()));

          while ((length=fin.read(buf))>0) {
            zos.write(buf, 0, length);
          }

          zos.closeEntry();
          fin.close();
        }
      }
    }
  }

  private void uploadBackup(File backup) throws IOException {
    Request request=new Request.Builder()
      .url(URL_CREATE_BACKUP)
      .post(RequestBody.create(JSON, "{}"))
      .build();
    Response response=OKHTTP_CLIENT.newCall(request).execute();

    if (response.code()==201) {
      String backupURL=response.header("Location");

      request=new Request.Builder()
        .url(backupURL+RESOURCE_DATASET)
        .put(RequestBody.create(ZIP, backup))
        .build();
      response=OKHTTP_CLIENT.newCall(request).execute();

      if (response.code()==201) {
        String datasetURL=response.header("Location");
        SharedPreferences prefs=
          getSharedPreferences(getClass().getName(),
            Context.MODE_PRIVATE);

        prefs
          .edit()
          .putString(PREF_LAST_BACKUP_DATASET, datasetURL)
          .commit();
      }
      else {
        Log.e(getClass().getSimpleName(),
          "Unsuccessful request to upload backup");
      }
    }
    else {
      Log.e(getClass().getSimpleName(),
        "Unsuccessful request to create backup");
    }
  }

  static class BackupCompletedEvent {

  }

  static class BackupFailedEvent {

  }
}
