/***
  Copyright (c) 2008-2013 CommonsWare, LLC
  
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

package com.commonsware.android.actionmode.longpress;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class ActionModeDemo extends ListActivity implements
    OnItemLongClickListener, AbsListView.MultiChoiceModeListener {
  private static final String STATE_CHOICE_MODE="choiceMode";
  private static final String STATE_MODEL="model";
  private static final String[] items= { "lorem", "ipsum", "dolor",
      "sit", "amet", "consectetuer", "adipiscing", "elit", "morbi",
      "vel", "ligula", "vitae", "arcu", "aliquet", "mollis", "etiam",
      "vel", "erat", "placerat", "ante", "porttitor", "sodales",
      "pellentesque", "augue", "purus" };
  private ArrayList<String> words=null;
  private ArrayAdapter<String> adapter=null;
  private ActionMode activeMode=null;

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);

    if (state == null) {
      initAdapter(null);
    }
    else {
      initAdapter(state.getStringArrayList(STATE_MODEL));
    }

    getListView().setOnItemLongClickListener(this);
    getListView().setMultiChoiceModeListener(this);

    int choiceMode=
        (state == null ? ListView.CHOICE_MODE_NONE
            : state.getInt(STATE_CHOICE_MODE));

    getListView().setChoiceMode(choiceMode);
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    l.setItemChecked(position, true);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.actions, menu);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
                                  ContextMenu.ContextMenuInfo menuInfo) {
    getMenuInflater().inflate(R.menu.context, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.add:
        addWord();

        return(true);

      case R.id.reset:
        initAdapter(null);

        return(true);

      case R.id.about:
        Toast.makeText(this, R.string.about_toast, Toast.LENGTH_LONG)
            .show();

        return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  @Override
  public boolean onItemLongClick(AdapterView<?> parent, View view,
                                 int position, long id) {
    getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
    getListView().setItemChecked(position, true);

    return(true);
  }

  @Override
  public boolean onCreateActionMode(ActionMode mode, Menu menu) {
    MenuInflater inflater=getMenuInflater();

    inflater.inflate(R.menu.context, menu);
    mode.setTitle(R.string.context_title);
    activeMode=mode;
    updateSubtitle(activeMode);

    return(true);
  }

  @Override
  public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
    return(false);
  }

  @Override
  public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
    boolean result=performActions(item);

    updateSubtitle(activeMode);

    return(result);
  }

  @Override
  public void onDestroyActionMode(ActionMode mode) {
    if (activeMode != null) {
      activeMode=null;
      getListView().setChoiceMode(ListView.CHOICE_MODE_NONE);
      getListView().setAdapter(getListView().getAdapter());
    }
  }

  @Override
  public void onItemCheckedStateChanged(ActionMode mode, int position,
                                        long id, boolean checked) {
    if (activeMode != null) {
      updateSubtitle(mode);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle state) {
    super.onSaveInstanceState(state);
    state.putInt(STATE_CHOICE_MODE, getListView().getChoiceMode());
    state.putStringArrayList(STATE_MODEL, words);
  }

  private void updateSubtitle(ActionMode mode) {
    mode.setSubtitle("(" + getListView().getCheckedItemCount() + ")");
  }

  public boolean performActions(MenuItem item) {
    @SuppressWarnings("unchecked")
    ArrayAdapter<String> adapter=(ArrayAdapter<String>)getListAdapter();
    SparseBooleanArray checked=getListView().getCheckedItemPositions();

    switch (item.getItemId()) {
      case R.id.cap:
        for (int i=0; i < checked.size(); i++) {
          if (checked.valueAt(i)) {
            int position=checked.keyAt(i);
            String word=words.get(position);

            word=word.toUpperCase(Locale.ENGLISH);

            adapter.remove(words.get(position));
            adapter.insert(word, position);
          }
        }

        return(true);

      case R.id.remove:
        ArrayList<Integer> positions=new ArrayList<Integer>();

        for (int i=0; i < checked.size(); i++) {
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

  private void initAdapter(ArrayList<String> startingPoint) {
    if (startingPoint == null) {
      words=new ArrayList<String>();

      for (int i=0;i<5;i++) {
        words.add(items[i]);
      }
    }
    else {
      words=startingPoint;
    }

    adapter=new ArrayAdapter<String>(this,
        android.R.layout.simple_list_item_activated_1,
        words);
    setListAdapter(adapter);
  }

  private void addWord() {
    if (adapter.getCount()<items.length) {
      adapter.add(items[adapter.getCount()]);
    }
  }
}