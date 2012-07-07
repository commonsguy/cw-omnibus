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

package com.commonsware.android.actionmodemc;

import java.util.ArrayList;
import java.util.Collections;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ActionModeDemo extends ListActivity {
  private static final String[] items=
      { "lorem", "ipsum", "dolor", "sit", "amet", "consectetuer",
          "adipiscing", "elit", "morbi", "vel", "ligula", "vitae",
          "arcu", "aliquet", "mollis", "etiam", "vel", "erat",
          "placerat", "ante", "porttitor", "sodales", "pellentesque",
          "augue", "purus" };
  private ArrayList<String> words=null;

  @TargetApi(11)
  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);

    initAdapter();
    
    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
      getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
      getListView()
        .setMultiChoiceModeListener(new HCMultiChoiceModeListener(this,
                                                                  getListView()));
    }
    else {
      getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
      registerForContextMenu(getListView());
    }
  }
  
  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    l.setItemChecked(position, true);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    new MenuInflater(this).inflate(R.menu.option, menu);

    EditText add=null;

    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
      View v=menu.findItem(R.id.add).getActionView();

      if (v!=null) {
        add=(EditText)v.findViewById(R.id.title);
      }
    }

    if (add!=null) {
      add.setOnEditorActionListener(onSearch);
    }

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
                                  ContextMenu.ContextMenuInfo menuInfo) {
    new MenuInflater(this).inflate(R.menu.context, menu);
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
        Toast
             .makeText(this, "Action Bar Sample App", Toast.LENGTH_LONG)
             .show();
        return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    boolean result=performActions(item);

    if (!result) {
      result=super.onContextItemSelected(item);
    }
    
    return(result);
  }

  @SuppressWarnings("unchecked")
  public boolean performActions(MenuItem item) {
    ArrayAdapter<String> adapter=(ArrayAdapter<String>)getListAdapter();
    SparseBooleanArray checked=getListView().getCheckedItemPositions();
    
    switch (item.getItemId()) {
      case R.id.cap:
        for (int i=0;i<checked.size();i++) {
          if (checked.valueAt(i)) {
            int position=checked.keyAt(i);
            String word=words.get(position);

            word=word.toUpperCase();

            adapter.remove(words.get(position));
            adapter.insert(word, position);
          }
        }

        return(true);

      case R.id.remove:
        ArrayList<Integer> positions=new ArrayList<Integer>();
        
        for (int i=0;i<checked.size();i++) {
          if (checked.valueAt(i)) {
            positions.add(checked.keyAt(i));
          }
        }
        
        Collections.sort(positions, Collections.reverseOrder());
        
        for (int position : positions) {
          adapter.remove(words.get(position));
        }
        
        getListView().clearChoices();

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
                                            android.R.layout.simple_list_item_checked,
                                            words));
  }

  private void add() {
    final View addView=getLayoutInflater().inflate(R.layout.add, null);

    new AlertDialog.Builder(this)
                                 .setTitle("Add a Word")
                                 .setView(addView)
                                 .setPositiveButton(
                                                    "OK",
                                                    new DialogInterface.OnClickListener() {
                                                      public void onClick(
                                                                          DialogInterface dialog,
                                                                          int whichButton) {
                                                        addWord((TextView)addView
                                                                                 .findViewById(R.id.title));
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
          if (event==null||event.getAction()==KeyEvent.ACTION_UP) {
            addWord(v);

            InputMethodManager imm=
                (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
          }

          return(true);
        }
      };
}