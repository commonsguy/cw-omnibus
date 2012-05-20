package com.commonsware.empublite;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.actionbarsherlock.app.SherlockFragment;
import org.json.JSONObject;

public class ModelFragment extends SherlockFragment {
  private BookContents contents=null;
  private ContentsLoadTask contentsTask=null;
  private SharedPreferences prefs=null;
  private PrefsLoadTask prefsTask=null;

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    setRetainInstance(true);
    deliverModel();
  }

  synchronized private void deliverModel() {
    if (prefs != null && contents != null) {
      ((EmPubLiteActivity)getActivity()).setupPager(prefs, contents);
    }
    else {
      if (prefs == null && prefsTask == null) {
        prefsTask=new PrefsLoadTask();
        executeAsyncTask(prefsTask,
                         getActivity().getApplicationContext());
      }
      else if (contents == null && contentsTask == null) {
        updateBook();
      }
    }
  }

  void updateBook() {
    contentsTask=new ContentsLoadTask();
    executeAsyncTask(contentsTask,
                     getActivity().getApplicationContext());
  }

  @TargetApi(11)
  static public <T> void executeAsyncTask(AsyncTask<T, ?, ?> task,
                                          T... params) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }
    else {
      task.execute(params);
    }
  }

  private static boolean deleteDir(File dir) {
    if (dir.exists() && dir.isDirectory()) {
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

  private class ContentsLoadTask extends AsyncTask<Context, Void, Void> {
    private BookContents localContents=null;
    private Exception e=null;

    @Override
    protected Void doInBackground(Context... ctxt) {
      String updateDir=
          prefs.getString(DownloadInstallService.PREF_UPDATE_DIR, null);

      try {
        StringBuilder buf=new StringBuilder();
        InputStream json=null;

        if (updateDir != null && new File(updateDir).exists()) {
          json=
              new FileInputStream(new File(new File(updateDir),
                                           "contents.json"));
        }
        else {
          json=ctxt[0].getAssets().open("book/contents.json");
        }

        BufferedReader in=
            new BufferedReader(new InputStreamReader(json));
        String str;

        while ((str=in.readLine()) != null) {
          buf.append(str);
        }

        in.close();

        if (updateDir != null && new File(updateDir).exists()) {
          localContents=
              new BookContents(new JSONObject(buf.toString()),
                               new File(updateDir));
        }
        else {
          localContents=
              new BookContents(new JSONObject(buf.toString()));
        }
      }
      catch (Exception e) {
        this.e=e;
      }

      String prevUpdateDir=
          prefs.getString(DownloadInstallService.PREF_PREV_UPDATE, null);

      if (prevUpdateDir != null) {
        File toBeDeleted=new File(prevUpdateDir);

        if (toBeDeleted.exists()) {
          deleteDir(toBeDeleted);
        }
      }

      return(null);
    }

    @Override
    public void onPostExecute(Void arg0) {
      if (e == null) {
        ModelFragment.this.contents=localContents;
        ModelFragment.this.contentsTask=null;
        deliverModel();
      }
      else {
        Log.e(getClass().getSimpleName(), "Exception loading contents",
              e);
      }
    }
  }

  private class PrefsLoadTask extends AsyncTask<Context, Void, Void> {
    SharedPreferences localPrefs=null;

    @Override
    protected Void doInBackground(Context... ctxt) {
      localPrefs=PreferenceManager.getDefaultSharedPreferences(ctxt[0]);
      localPrefs.getAll();

      return(null);
    }

    @Override
    public void onPostExecute(Void arg0) {
      ModelFragment.this.prefs=localPrefs;
      ModelFragment.this.prefsTask=null;
      deliverModel();
    }
  }
}
