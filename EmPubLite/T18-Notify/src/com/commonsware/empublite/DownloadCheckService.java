package com.commonsware.empublite;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import com.commonsware.cwac.wakeful.WakefulIntentService;
import org.json.JSONException;
import org.json.JSONObject;

public class DownloadCheckService extends WakefulIntentService {
  public static final String UPDATE_FILENAME="book.zip";
  private static final String UPDATE_BASEDIR="updates";
  private static final String UPDATE_URL=
      "http://misc.commonsware.com/empublite-update.json";

  public DownloadCheckService() {
    super("DownloadCheckService");
  }

  @Override
  protected void doWakefulWork(Intent intent) {
    BufferedReader reader=null;

    try {
      URL url=new URL(UPDATE_URL);
      HttpURLConnection c=(HttpURLConnection)url.openConnection();

      c.setRequestMethod("GET");
      c.setReadTimeout(15000);
      c.connect();

      reader=
          new BufferedReader(new InputStreamReader(c.getInputStream()));

      StringBuilder buf=new StringBuilder();
      String line=null;

      while ((line=reader.readLine()) != null) {
        buf.append(line + "\n");
      }

      checkDownloadInfo(buf.toString());
    }
    catch (Exception e) {
      Log.e(getClass().getSimpleName(),
            "Exception retrieving update info", e);
    }
    finally {
      if (reader != null) {
        try {
          reader.close();
        }
        catch (IOException e) {
          Log.e(getClass().getSimpleName(),
                "Exception closing HUC reader", e);
        }
      }
    }
  }

  static File getUpdateBaseDir(Context ctxt) {
    return(new File(ctxt.getFilesDir(), UPDATE_BASEDIR));
  }

  static File getUpdateDir(Context ctxt, boolean pruneOld) {
    File base=DownloadCheckService.getUpdateBaseDir(ctxt);

    if (!base.exists()) {
      return(null);
    }

    File[] updates=base.listFiles(new FileFilter() {
      public boolean accept(File f) {
        return f.isDirectory();
      }
    });

    Arrays.sort(updates, new Comparator<File>() {
      @Override
      public int compare(File lhs, File rhs) {
        return(lhs.getName().compareTo(rhs.getName()));
      }
    });

    if (pruneOld && updates.length > 1) {
      deleteDir(updates[0]);
    }

    return(updates[updates.length - 1]);
  }

  private static boolean deleteDir(File dir) {
    if (dir.isDirectory()) {
      File[] children=dir.listFiles();

      for (File child : children) {
        boolean ok=deleteDir(child);

        if (!ok) {
          return(false);
        }
      }
    }

    return(dir.delete());
  }

  private void checkDownloadInfo(String raw) throws JSONException {
    JSONObject json=new JSONObject(raw);
    String version=json.names().getString(0);
    File localCopy=new File(getUpdateBaseDir(this), version);

    if (!localCopy.exists()) {
      localCopy.mkdirs();

      String url=json.getString(version);
      DownloadManager mgr=
          (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
      DownloadManager.Request req=
          new DownloadManager.Request(Uri.parse(url));

      Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                 .mkdirs();

      req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                                     | DownloadManager.Request.NETWORK_MOBILE)
         .setAllowedOverRoaming(false)
         .setTitle(getString(R.string.update_title))
         .setDescription(getString(R.string.update_description))
         .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                            UPDATE_FILENAME);

      mgr.enqueue(req);
    }
  }
}
