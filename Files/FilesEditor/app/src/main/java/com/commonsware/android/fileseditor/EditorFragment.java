/***
  Copyright (c) 2012-2015 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  Covered in detail in the book _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.fileseditor;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class EditorFragment extends Fragment {
  private static final String KEY_FILE="file";
  private EditText editor;
  private LoadTextTask loadTask=null;
  private boolean loaded=false;

  static EditorFragment newInstance(File fileToEdit) {
    EditorFragment frag=new EditorFragment();
    Bundle args=new Bundle();

    args.putSerializable(KEY_FILE, fileToEdit);
    frag.setArguments(args);

    return(frag);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setRetainInstance(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    View result=inflater.inflate(R.layout.editor, container, false);

    editor=result.findViewById(R.id.editor);

    return(result);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    if (!loaded) {
      loadTask=new LoadTextTask();
      loadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
          (File)getArguments().getSerializable(KEY_FILE));
    }
  }

  @Override
  public void onPause() {
    if (loaded) {
      new SaveThread(editor.getText().toString(),
          (File)getArguments().getSerializable(KEY_FILE)).start();
    }

    super.onPause();
  }

  @Override
  public void onDestroy() {
    if (loadTask!=null) {
      loadTask.cancel(false);
    }

    super.onDestroy();
  }

  private class LoadTextTask extends AsyncTask<File, Void, String> {
    @Override
    protected String doInBackground(File... files) {
      String result=null;

      if (files[0].exists()) {
        BufferedReader br;

        try {
          br=new BufferedReader(new FileReader(files[0]));

          try {
            StringBuilder sb=new StringBuilder();
            String line=br.readLine();

            while (line!=null) {
              sb.append(line);
              sb.append("\n");
              line=br.readLine();
            }

            result=sb.toString();
          }
          finally {
            br.close();
          }
        }
        catch (IOException e) {
          Log.e(getClass().getSimpleName(), "Exception reading file", e);
        }
      }

      return(result);
    }

    @Override
    protected void onPostExecute(String s) {
      editor.setText(s);
      loadTask=null;
      loaded=true;
    }
  }

  private static class SaveThread extends Thread {
    private final String text;
    private final File fileToEdit;

    SaveThread(String text, File fileToEdit) {
      this.text=text;
      this.fileToEdit=fileToEdit;
    }

    @Override
    public void run() {
      try {
        fileToEdit.getParentFile().mkdirs();

        FileOutputStream fos=new FileOutputStream(fileToEdit);

        Writer w=new BufferedWriter(new OutputStreamWriter(fos));

        try {
          w.write(text);
          w.flush();
          fos.getFD().sync();
        }
        finally {
          w.close();
        }
      }
      catch (IOException e) {
        Log.e(getClass().getSimpleName(), "Exception writing file", e);
      }
    }
  }
}