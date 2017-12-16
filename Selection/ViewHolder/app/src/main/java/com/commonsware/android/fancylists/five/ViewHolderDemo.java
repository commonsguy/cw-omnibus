/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  Covered in detail in the book _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
*/

package com.commonsware.android.fancylists.five;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class ViewHolderDemo extends ListActivity {
  private static final String[] items={"lorem", "ipsum", "dolor",
          "sit", "amet",
          "consectetuer", "adipiscing", "elit", "morbi", "vel",
          "ligula", "vitae", "arcu", "aliquet", "mollis",
          "etiam", "vel", "erat", "placerat", "ante",
          "porttitor", "sodales", "pellentesque", "augue", "purus"};
  
  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);
    setListAdapter(new IconicAdapter());
  }
  
  private String getModel(int position) {
    return(((IconicAdapter)getListAdapter()).getItem(position));
  }
  
  class IconicAdapter extends ArrayAdapter<String> {
    IconicAdapter() {
      super(ViewHolderDemo.this, R.layout.row, R.id.label,
            items);
    }
    
    @Override
    public View getView(int position, View convertView,
                        ViewGroup parent) {
      View row=super.getView(position, convertView, parent);
      ViewHolder holder=(ViewHolder)row.getTag();
      
      if (holder==null) {                         
        holder=new ViewHolder(row);
        row.setTag(holder);
      }
      
      if (getModel(position).length()>4) {
        holder.icon.setImageResource(R.drawable.delete);
      } 
      else {
        holder.icon.setImageResource(R.drawable.ok);
      }

      holder.size.setText(String.format(getString(R.string.size_template), items[position].length()));

      return(row);
    }
  }
}
