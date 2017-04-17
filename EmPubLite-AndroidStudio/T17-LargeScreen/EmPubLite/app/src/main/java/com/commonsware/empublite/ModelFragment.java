package com.commonsware.empublite;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.gson.Gson;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicReference;

public class ModelFragment extends Fragment {
  final private AtomicReference<BookContents> contents=
    new AtomicReference<>();
  final private AtomicReference<SharedPreferences> prefs=
    new AtomicReference<>();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
  }

  @Override
  public void onAttach(Activity host) {
    super.onAttach(host);

    EventBus.getDefault().register(this);

    if (contents.get()==null) {
      new LoadThread(host).start();
    }
  }

  @Override
  public void onDetach() {
    EventBus.getDefault().unregister(this);

    super.onDetach();
  }

  @SuppressWarnings("unused")
  @Subscribe(threadMode =ThreadMode.BACKGROUND)
  public void onBookUpdated(BookUpdatedEvent event) {
    if (getActivity()!=null) {
      new LoadThread(getActivity()).start();
    }
  }

  public BookContents getBook() {
    return(contents.get());
  }

  public SharedPreferences getPrefs() {
    return(prefs.get());
  }

  private class LoadThread extends Thread {
    final private Context ctxt;

    LoadThread(Context ctxt) {
      super();

      this.ctxt=ctxt.getApplicationContext();
    }

    @Override
    public void run() {
      prefs.set(PreferenceManager.getDefaultSharedPreferences(ctxt));

      Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
      Gson gson=new Gson();
      File baseDir=
        new File(ctxt.getFilesDir(),
          DownloadCheckService.UPDATE_BASEDIR);

      try {
        InputStream is;

        if (baseDir.exists()) {
          is=new FileInputStream(new File(baseDir, "contents.json"));
        }
        else {
          is=ctxt.getAssets().open("book/contents.json");
        }

        BufferedReader reader=
          new BufferedReader(new InputStreamReader(is));

        contents.set(gson.fromJson(reader, BookContents.class));

        if (baseDir.exists()) {
          contents.get().setBaseDir(baseDir);
        }

        EventBus.getDefault().post(new BookLoadedEvent(getBook()));
      }
      catch (IOException e) {
        Log.e(getClass().getSimpleName(), "Exception parsing JSON", e);
      }
    }
  }
}
