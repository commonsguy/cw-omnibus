/***
  Copyright (c) 2008-2015 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
*/

package com.commonsware.android.recyclerview.actionmodelist2;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class MainActivity extends RecyclerViewActivity {
  private static final String STATE_ITEMS="items";
  private static final String[] ORIGINAL_ITEMS={"lorem", "ipsum", "dolor",
          "sit", "amet",
          "consectetuer", "adipiscing", "elit", "morbi", "vel",
          "ligula", "vitae", "arcu", "aliquet", "mollis",
          "etiam", "vel", "erat", "placerat", "ante",
          "porttitor", "sodales", "pellentesque", "augue", "purus"};
  private ArrayList<String> items;
  private ChoiceCapableAdapter<?> adapter=null;

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);

    setLayoutManager(new LinearLayoutManager(this));
    adapter=new IconicAdapter();
    setAdapter(adapter);
  }

  @Override
  public void onSaveInstanceState(Bundle state) {
    adapter.onSaveInstanceState(state);
    state.putStringArrayList(STATE_ITEMS, items);
  }

  @Override
  public void onRestoreInstanceState(Bundle state) {
    adapter.onRestoreInstanceState(state);
    items=state.getStringArrayList(STATE_ITEMS);
  }

  private ArrayList<String> getItems() {
    if (items==null) {
      items=new ArrayList<String>();

      for (String s : ORIGINAL_ITEMS) {
        items.add(s);
      }
    }

    return(items);
  }
  
  class IconicAdapter extends ChoiceCapableAdapter<RowController>
      implements ActionMode.Callback {
    private ActionMode activeMode=null;

    IconicAdapter() {
      super(new MultiChoiceMode());
    }

    @Override
    public RowController onCreateViewHolder(ViewGroup parent, int viewType) {
      return(new RowController(this, getLayoutInflater()
                                      .inflate(R.layout.row, parent, false)));
    }

    @Override
    public void onBindViewHolder(RowController holder, int position) {
      holder.bindModel(getItems().get(position));
    }

    @Override
    public int getItemCount() {
      return(getItems().size());
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
      switch(item.getItemId()) {
        case R.id.cap:
          visitChecks(new ChoiceMode.Visitor() {
            @Override
            public void onCheckedPosition(int position) {
              String word=getItems().get(position);

              word=word.toUpperCase(Locale.ENGLISH);
              getItems().set(position, word);
              notifyItemChanged(position);
            }
          });
          break;

        case R.id.remove:
          final ArrayList<Integer> positions=new ArrayList<Integer>();

          visitChecks(new ChoiceMode.Visitor() {
            @Override
            public void onCheckedPosition(int position) {
              positions.add(position);
            }
          });

          Collections.sort(positions, Collections.reverseOrder());

          for (int position : positions) {
            getItems().remove(position);
            notifyItemRemoved(position);
          }

          clearChecks();
          activeMode.finish();
          break;

        default:
          return(false);
      }

      if (activeMode!=null) {
        updateSubtitle(activeMode);
      }

      return(true);
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
      if (activeMode != null) {
        activeMode=null;
        visitChecks(new ChoiceMode.Visitor() {
          @Override
          public void onCheckedPosition(int position) {
            onChecked(position, false);
            notifyItemChanged(position);
          }
        });
      }
    }

    @Override
    void onChecked(int position, boolean isChecked) {
      super.onChecked(position, isChecked);

      if (isChecked) {
        if (activeMode==null) {
          activeMode=startActionMode(this);
        }
        else {
          updateSubtitle(activeMode);
        }
      }
      else if (getCheckedCount()==0 && activeMode!=null) {
        activeMode.finish();
      }
    }

    private void updateSubtitle(ActionMode mode) {
      mode.setSubtitle("(" + getCheckedCount() + ")");
    }
  }
}
