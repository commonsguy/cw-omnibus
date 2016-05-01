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

package com.commonsware.android.recyclerview.headerlist;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public class MainActivity extends RecyclerViewActivity {
  private static final String[][] items= {
      { "lorem", "ipsum", "dolor", "sit", "amet" },
      { "consectetuer", "adipiscing", "elit", "morbi", "vel" },
      { "ligula", "vitae", "arcu", "aliquet", "mollis" },
      { "etiam", "vel", "erat", "placerat", "ante" },
      { "porttitor", "sodales", "pellentesque", "augue", "purus" } };
  
  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);

    setLayoutManager(new LinearLayoutManager(this));
    setAdapter(new IconicAdapter());
  }
  
  class IconicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      if (viewType==R.id.detail) {
        return(new RowController(getLayoutInflater()
                     .inflate(R.layout.row, parent, false)));
      }

      return(new HeaderController(getLayoutInflater()
                    .inflate(R.layout.header, parent, false)));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      if (holder instanceof RowController) {
        ((RowController)holder).bindModel((String)getItem(position));
      }
      else {
        ((HeaderController)holder).bindModel((Integer)getItem(position));
      }
    }

    @Override
    public int getItemCount() {
      int count=0;

      for (String[] batch : items) {
        count+=1 + batch.length;
      }

      return(count);
    }

    @Override
    public int getItemViewType(int position) {
      if (getItem(position) instanceof Integer) {
        return(R.id.header);
      }

      return(R.id.detail);
    }

    private Object getItem(int position) {
      int offset=position;
      int batchIndex=0;

      for (String[] batch : items) {
        if (offset == 0) {
          return(Integer.valueOf(batchIndex));
        }

        offset--;

        if (offset < batch.length) {
          return(batch[offset]);
        }

        offset-=batch.length;
        batchIndex++;
      }

      throw new IllegalArgumentException("Invalid position: "
          + String.valueOf(position));
    }
  }
}
