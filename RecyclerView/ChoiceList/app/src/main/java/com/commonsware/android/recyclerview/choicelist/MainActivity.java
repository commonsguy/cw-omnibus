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

package com.commonsware.android.recyclerview.choicelist;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ViewGroup;

public class MainActivity extends RecyclerViewActivity {
  private static final String[] items={"lorem", "ipsum", "dolor",
          "sit", "amet",
          "consectetuer", "adipiscing", "elit", "morbi", "vel",
          "ligula", "vitae", "arcu", "aliquet", "mollis",
          "etiam", "vel", "erat", "placerat", "ante",
          "porttitor", "sodales", "pellentesque", "augue", "purus"};
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
  }

  @Override
  public void onRestoreInstanceState(Bundle state) {
    adapter.onRestoreInstanceState(state);
  }
  
  class IconicAdapter extends ChoiceCapableAdapter<RowController> {
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
      holder.bindModel(items[position]);
    }

    @Override
    public int getItemCount() {
      return(items.length);
    }
  }
}
