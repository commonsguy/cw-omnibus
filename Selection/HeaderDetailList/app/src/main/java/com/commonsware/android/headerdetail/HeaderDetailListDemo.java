/***
  Copyright (c) 2008-2012 CommonsWare, LLC
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

package com.commonsware.android.headerdetail;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HeaderDetailListDemo extends ListActivity {
  private static final String[][] items= {
      { "lorem", "ipsum", "dolor", "sit", "amet" },
      { "consectetuer", "adipiscing", "elit", "morbi", "vel" },
      { "ligula", "vitae", "arcu", "aliquet", "mollis" },
      { "etiam", "vel", "erat", "placerat", "ante" },
      { "porttitor", "sodales", "pellentesque", "augue", "purus" } };

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setListAdapter(new IconicAdapter());
  }

  class IconicAdapter extends BaseAdapter {
    @Override
    public int getCount() {
      int count=0;

      for (String[] batch : items) {
        count+=1 + batch.length;
      }

      return(count);
    }

    @Override
    public Object getItem(int position) {
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

    @Override
    public long getItemId(int position) {
      return(position);
    }

    @Override
    public int getViewTypeCount() {
      return(2);
    }

    @Override
    public int getItemViewType(int position) {
      if (getItem(position) instanceof Integer) {
        return(0);
      }

      return(1);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      if (getItemViewType(position) == 0) {
        return(getHeaderView(position, convertView, parent));
      }

      View row=convertView;

      if (row == null) {
        row=getLayoutInflater().inflate(R.layout.row, parent, false);
      }

      ViewHolder holder=(ViewHolder)row.getTag();

      if (holder == null) {
        holder=new ViewHolder(row);
        row.setTag(holder);
      }

      String word=(String)getItem(position);

      if (word.length() > 4) {
        holder.icon.setImageResource(R.drawable.delete);
      }
      else {
        holder.icon.setImageResource(R.drawable.ok);
      }

      holder.label.setText(word);
      holder.size.setText(String.format(getString(R.string.size_template),
                                        word.length()));

      return(row);
    }

    private View getHeaderView(int position, View convertView,
                               ViewGroup parent) {
      View row=convertView;

      if (row == null) {
        row=getLayoutInflater().inflate(R.layout.header, parent, false);
      }

      Integer batchIndex=(Integer)getItem(position);
      TextView label=(TextView)row.findViewById(R.id.label);

      label.setText(String.format(getString(R.string.batch),
                                  1 + batchIndex.intValue()));

      return(row);
    }
  }
}
