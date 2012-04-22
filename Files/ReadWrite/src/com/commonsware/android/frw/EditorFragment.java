/***
  Copyright (c) 2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
 */

package com.commonsware.android.frw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class EditorFragment extends SherlockFragment {
  private static final String FILENAME="notes.txt";
  private CheckBox external=null;
  private EditText editor=null;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                           Bundle savedInstanceState) {
    setHasOptionsMenu(true);

    View result=inflater.inflate(R.layout.editor, parent, false);

    editor=(EditText)result.findViewById(R.id.editor);

    return(result);
  }

  @Override
  public void onResume() {
    super.onResume();

    new LoadTask().execute(getTarget());
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.actions, menu);
    external=(CheckBox)menu.findItem(R.id.location).getActionView();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.save) {
      try {
        save(editor.getText().toString(), getTarget());
      }
      catch (Exception e) {
        boom(e);
      }
    }
    else if (item.getItemId() == R.id.saveBackground) {
      new SaveTask(editor.getText().toString(), getTarget()).execute();
    }

    return(super.onOptionsItemSelected(item));
  }

  private File getTarget() {
    File root=null;

    if (external.isChecked()) {
      root=getActivity().getExternalFilesDir(null);
    }
    else {
      root=getActivity().getFilesDir();
    }

    return(new File(root, FILENAME));
  }

  private void boom(Exception e) {
    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG)
         .show();
    Log.e(getClass().getSimpleName(), "Exception saving file", e);
  }

  private void save(String text, File target) throws IOException {
    FileOutputStream fos=new FileOutputStream(target);
    OutputStreamWriter out=new OutputStreamWriter(fos);

    out.write(text);
    out.flush();
    fos.getFD().sync();
    out.close();
  }

  private String load(File target) throws IOException {
    String result="";

    try {
      InputStream in=new FileInputStream(target);

      if (in != null) {
        InputStreamReader tmp=new InputStreamReader(in);
        BufferedReader reader=new BufferedReader(tmp);
        String str;
        StringBuilder buf=new StringBuilder();

        while ((str=reader.readLine()) != null) {
          buf.append(str + "\n");
        }

        in.close();
        result=buf.toString();
      }
    }
    catch (java.io.FileNotFoundException e) {
      // that's OK, we probably haven't created it yet
    }

    return(result);
  }

  private class LoadTask extends AsyncTask<File, Void, String> {
    private Exception e=null;

    @Override
    protected String doInBackground(File... args) {
      String result="";

      try {
        result=load(args[0]);
      }
      catch (Exception e) {
        this.e=e;
      }

      return(result);
    }

    @Override
    protected void onPostExecute(String text) {
      if (e == null) {
        editor.setText(text);
      }
      else {
        boom(e);
      }
    }
  }

  private class SaveTask extends AsyncTask<Void, Void, Void> {
    private Exception e=null;
    private String text;
    private File target;

    SaveTask(String text, File target) {
      this.text=text;
      this.target=target;
    }

    @Override
    protected Void doInBackground(Void... args) {
      try {
        save(text, target);
      }
      catch (Exception e) {
        this.e=e;
      }

      return(null);
    }

    @Override
    protected void onPostExecute(Void arg0) {
      if (e != null) {
        boom(e);
      }
    }
  }
}
