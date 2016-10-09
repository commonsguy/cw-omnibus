package com.commonsware.empublite;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.commonsware.cwac.security.ZipUtils;
import org.greenrobot.eventbus.EventBus;
import java.io.File;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DownloadCheckService extends IntentService {
  private static final String OUR_BOOK_DATE="20120418";
  private static final String UPDATE_FILENAME="book.zip";
  static final String UPDATE_BASEDIR="updates";

  public DownloadCheckService() {
    super("DownloadCheckService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    try {
      String url=getUpdateUrl();

      if (url != null) {
        File book=download(url);
        File updateDir=new File(getFilesDir(), UPDATE_BASEDIR);

        updateDir.mkdirs();
        ZipUtils.unzip(book, updateDir);
        book.delete();
        EventBus.getDefault().post(new BookUpdatedEvent());
      }
    }
    catch (Exception e) {
      Log.e(getClass().getSimpleName(),
        "Exception downloading update", e);
    }
  }

  private String getUpdateUrl() throws IOException {
    Retrofit retrofit=
      new Retrofit.Builder()
        .baseUrl("https://commonsware.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build();
    BookUpdateInterface updateInterface=
      retrofit.create(BookUpdateInterface.class);
    BookUpdateInfo info=updateInterface.update().execute().body();

    if (info.updatedOn.compareTo(OUR_BOOK_DATE) > 0) {
      return(info.updateUrl);
    }

    return(null);
  }

  private File download(String url) throws IOException {
    File output=new File(getFilesDir(), UPDATE_FILENAME);

    if (output.exists()) {
      output.delete();
    }

    OkHttpClient client=new OkHttpClient();
    Request request=new Request.Builder().url(url).build();
    Response response=client.newCall(request).execute();
    BufferedSink sink=Okio.buffer(Okio.sink(output));

    sink.writeAll(response.body().source());
    sink.close();

    return(output);
  }
}