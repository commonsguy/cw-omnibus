package com.commonsware.empublite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import com.commonsware.cwac.wakeful.WakefulIntentService;

public class DownloadInstallService extends WakefulIntentService {
  public static final String PREF_UPDATE_DIR="updateDir";
  public static final String PREF_PREV_UPDATE="previousUpdateDir";
  public static final String ACTION_UPDATE_READY=
      "com.commonsware.empublite.action.UPDATE_READY";

  public DownloadInstallService() {
    super("DownloadInstallService");
  }

  @Override
  protected void doWakefulWork(Intent intent) {
    SharedPreferences prefs=
        PreferenceManager.getDefaultSharedPreferences(this);
    String prevUpdateDir=prefs.getString(PREF_UPDATE_DIR, null);
    String pendingUpdateDir=
        prefs.getString(DownloadCheckService.PREF_PENDING_UPDATE, null);

    if (pendingUpdateDir != null) {
      File root=
          Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
      File update=new File(root, DownloadCheckService.UPDATE_FILENAME);

      try {
        unzip(update, new File(pendingUpdateDir));
        prefs.edit().putString(PREF_PREV_UPDATE, prevUpdateDir)
             .putString(PREF_UPDATE_DIR, pendingUpdateDir).commit();
      }
      catch (IOException e) {
        Log.e(getClass().getSimpleName(), "Exception unzipping update",
              e);
      }

      update.delete();

      Intent i=new Intent(ACTION_UPDATE_READY);

      i.setPackage(getPackageName());
      sendOrderedBroadcast(i, null);
    }
    else {
      Log.e(getClass().getSimpleName(), "null pendingUpdateDir");
    }
  }

  private static void unzip(File src, File dest) throws IOException {
    InputStream is=new FileInputStream(src);
    ZipInputStream zis=new ZipInputStream(new BufferedInputStream(is));
    ZipEntry ze;

    dest.mkdirs();

    while ((ze=zis.getNextEntry()) != null) {
      byte[] buffer=new byte[8192];
      int count;
      FileOutputStream fos=
          new FileOutputStream(new File(dest, ze.getName()));
      BufferedOutputStream out=new BufferedOutputStream(fos);

      try {
        while ((count=zis.read(buffer)) != -1) {
          out.write(buffer, 0, count);
        }

        out.flush();
      }
      finally {
        fos.getFD().sync();
        out.close();
      }

      zis.closeEntry();
    }

    zis.close();
  }
}
