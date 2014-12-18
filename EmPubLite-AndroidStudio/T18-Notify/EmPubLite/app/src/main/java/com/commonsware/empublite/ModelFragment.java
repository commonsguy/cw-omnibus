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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import de.greenrobot.event.EventBus;

public class ModelFragment extends Fragment {
  private BookContents contents=null;
  private SharedPreferences prefs=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
  }

  @Override
  public void onAttach(Activity host) {
    super.onAttach(host);

    EventBus.getDefault().register(this);

    if (contents == null) {
      new LoadThread(host).start();
    }
  }

  @Override
  public void onDetach() {
    EventBus.getDefault().unregister(this);

    super.onDetach();
  }

  public BookContents getBook() {
    return(contents);
  }

  public SharedPreferences getPrefs() {
    return(prefs);
  }

  public void onEvent(BookUpdatedEvent event) {
    if (getActivity() != null) {
      new LoadThread(getActivity()).start();
    }
  }

  private class LoadThread extends Thread {
    private Context ctxt=null;

    LoadThread(Context ctxt) {
      super();

      this.ctxt=ctxt.getApplicationContext();
      Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
    }

    @Override
    public void run() {
      prefs=PreferenceManager.getDefaultSharedPreferences(ctxt);

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

        contents=gson.fromJson(reader, BookContents.class);
        is.close();

        if (baseDir.exists()) {
          contents.setBaseDir(baseDir);
        }

        EventBus.getDefault().post(new BookLoadedEvent(contents));
      }
      catch (IOException e) {
        Log.e(getClass().getSimpleName(), "Exception parsing JSON", e);
      }
    }
  }
}
