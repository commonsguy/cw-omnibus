package com.commonsware.empublite;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.commonsware.cwac.wakeful.WakefulIntentService;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.NoSubscriberEvent;
import retrofit.RestAdapter;

public class DownloadCheckService extends WakefulIntentService {
  private static final String OUR_BOOK_DATE="20120418";
  private static final String UPDATE_FILENAME="book.zip";
  public static final String UPDATE_BASEDIR="updates";
  private static final int NOTIFY_ID=1337;

  public DownloadCheckService() {
    super("DownloadCheckService");
  }

  @Override
  protected void doWakefulWork(Intent intent) {
    try {
      String url=getUpdateUrl();

      if (url != null) {
        File book=download(url);
        File updateDir=new File(getFilesDir(), UPDATE_BASEDIR);

        updateDir.mkdirs();
        unzip(book, updateDir);
        book.delete();

        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new BookUpdatedEvent());
        EventBus.getDefault().unregister(this);
      }
    }
    catch (Exception e) {
      Log.e(getClass().getSimpleName(),
          "Exception downloading update", e);
    }
  }

  public void onEvent(NoSubscriberEvent event) {
    NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
    Intent toLaunch=new Intent(this, EmPubLiteActivity.class);
    PendingIntent pi=PendingIntent.getActivity(this, 0, toLaunch, 0);

    builder.setAutoCancel(true).setContentIntent(pi)
        .setContentTitle(getString(R.string.update_complete))
        .setContentText(getString(R.string.update_desc))
        .setSmallIcon(android.R.drawable.stat_sys_download_done)
        .setTicker(getString(R.string.update_complete));

    NotificationManager mgr=
        ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE));

    mgr.notify(NOTIFY_ID, builder.build());
  }

  private String getUpdateUrl() {
    RestAdapter restAdapter=
        new RestAdapter.Builder().setEndpoint("http://commonsware.com")
            .build();
    BookUpdateInterface updateInterface=
        restAdapter.create(BookUpdateInterface.class);
    BookUpdateInfo info=updateInterface.update();

    if (info.updatedOn.compareTo(OUR_BOOK_DATE) > 0) {
      return(info.updateUrl);
    }

    return(null);
  }

  private File download(String url) throws MalformedURLException,
      IOException {
    File output=new File(getFilesDir(), UPDATE_FILENAME);

    if (output.exists()) {
      output.delete();
    }

    HttpURLConnection c=
        (HttpURLConnection)new URL(url).openConnection();
    FileOutputStream fos=new FileOutputStream(output.getPath());
    BufferedOutputStream out=new BufferedOutputStream(fos);

    try {
      InputStream in=c.getInputStream();
      byte[] buffer=new byte[16384];
      int len=0;

      while ((len=in.read(buffer)) > 0) {
        out.write(buffer, 0, len);
      }

      out.flush();
    }
    finally {
      fos.getFD().sync();
      out.close();
      c.disconnect();
    }

    return(output);
  }

  private static void unzip(File src, File dest) throws IOException {
    InputStream is=new FileInputStream(src);
    ZipInputStream zis=new ZipInputStream(new BufferedInputStream(is));
    ZipEntry ze;

    dest.mkdirs();

    while ((ze=zis.getNextEntry()) != null) {
      byte[] buffer=new byte[16384];
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