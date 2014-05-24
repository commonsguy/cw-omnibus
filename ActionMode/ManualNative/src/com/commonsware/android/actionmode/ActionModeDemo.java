/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  
  Licensed under the Apache License, Version 2.0 (the "License"); you may
  not use this file except in compliance with the License. You may obtain
  a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package com.commonsware.android.actionmode;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class ActionModeDemo extends ListActivity {
  private static final String[] items= { "lorem", "ipsum", "dolor",
      "sit", "amet", "consectetuer", "adipiscing", "elit", "morbi",
      "vel", "ligula", "vitae", "arcu", "aliquet", "mollis", "etiam",
      "vel", "erat", "placerat", "ante", "porttitor", "sodales",
      "pellentesque", "augue", "purus" };
  private ArrayList<String> words=null;

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);

    initAdapter();
    getListView().setLongClickable(true);
    getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    getListView().setOnItemLongClickListener(new ActionModeHelper(
                                                                  this,
                                                                  getListView()));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.option, menu);

    EditText add=null;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      View v=menu.findItem(R.id.add).getActionView();

      if (v != null) {
        add=(EditText)v.findViewById(R.id.title);
      }
    }

    if (add != null) {
      add.setOnEditorActionListener(onSearch);
    }

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.add:
        add();
        return(true);

      case R.id.reset:
        initAdapter();
        return(true);

      case R.id.about:
      case android.R.id.home:
        Toast.makeText(this, "Action Bar Sample App", Toast.LENGTH_LONG)
             .show();
        return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  @SuppressWarnings("unchecked")
  public boolean performAction(int itemId, int position) {
    ArrayAdapter<String> adapter=(ArrayAdapter<String>)getListAdapter();

    switch (itemId) {
      case R.id.cap:
        String word=words.get(position);

        word=word.toUpperCase();

        adapter.remove(words.get(position));
        adapter.insert(word, position);

        return(true);

      case R.id.remove:
        adapter.remove(words.get(position));

        return(true);
    }

    return(false);
  }

  private void initAdapter() {
    words=new ArrayList<String>();

    for (String s : items) {
      words.add(s);
    }

    setListAdapter(new ArrayAdapter<String>(
                                            this,
                                            R.layout.simple_list_item_1,
                                            words));
  }

  private void add() {
    final View addView=getLayoutInflater().inflate(R.layout.add, null);

    new AlertDialog.Builder(this).setTitle("Add a Word")
                                 .setView(addView)
                                 .setPositiveButton("OK",
                                                    new DialogInterface.OnClickListener() {
                                                      public void onClick(DialogInterface dialog,
                                                                          int whichButton) {
                                                        addWord((TextView)addView.findViewById(R.id.title));
                                                      }
                                                    })
                                 .setNegativeButton("Cancel", null)
                                 .show();
  }

  @SuppressWarnings("unchecked")
  private void addWord(TextView title) {
    ArrayAdapter<String> adapter=(ArrayAdapter<String>)getListAdapter();

    adapter.add(title.getText().toString());
  }

  private TextView.OnEditorActionListener onSearch=
      new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView v, int actionId,
                                      KeyEvent event) {
          if (event == null || event.getAction() == KeyEvent.ACTION_UP) {
            addWord(v);

            InputMethodManager imm=
                (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
          }

          return(true);
        }
      };
}