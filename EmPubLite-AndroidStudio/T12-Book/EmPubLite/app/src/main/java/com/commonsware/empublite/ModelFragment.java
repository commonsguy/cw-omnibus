package com.commonsware.empublite;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import com.google.gson.Gson;
import org.greenrobot.eventbus.EventBus;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicReference;

public class ModelFragment extends Fragment {
  final private AtomicReference<BookContents> contents=
    new AtomicReference<>();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
  }

  @Override
  public void onAttach(Activity host) {
    super.onAttach(host);

    if (contents.get()==null) {
      new LoadThread(host.getAssets()).start();
    }
  }

  public BookContents getBook() {
    return(contents.get());
  }

  private class LoadThread extends Thread {
    private AssetManager assets=null;

    LoadThread(AssetManager assets) {
      super();

      this.assets=assets;
    }

    @Override
    public void run() {
      Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
      Gson gson=new Gson();

      try {
        InputStream is=assets.open("book/contents.json");
        BufferedReader reader=
          new BufferedReader(new InputStreamReader(is));

        contents.set(gson.fromJson(reader, BookContents.class));

        EventBus.getDefault().post(new BookLoadedEvent(getBook()));
      }
      catch (IOException e) {
        Log.e(getClass().getSimpleName(), "Exception parsing JSON", e);
      }
    }
  }
}
