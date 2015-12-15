/***
 Copyright (c) 2015 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 From _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.percent.comparison;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StuffAdapter extends BaseAdapter {
  private final LayoutInflater inflater;
  private final int layoutId;

  StuffAdapter(LayoutInflater inflater, int layoutId) {
    this.inflater=inflater;
    this.layoutId=layoutId;
  }

  @Override
  public int getCount() {
    return(25);
  }

  @Override
  public Object getItem(int position) {
    return(Integer.valueOf(position));
  }

  @Override
  public long getItemId(int position) {
    return(position);
  }

  @Override
  public View getView(int position, View convertView,
                      ViewGroup parent) {
    if (convertView==null) {
      convertView=inflater.inflate(layoutId, parent, false);
    }

    String prefix=Integer.toString(position+1);
    TextView tv=(TextView)convertView.findViewById(R.id.start);

    tv.setText(prefix+"A");
    tv=(TextView)convertView.findViewById(R.id.center);
    tv.setText(prefix+"B");
    tv=(TextView)convertView.findViewById(R.id.end);
    tv.setText(prefix+"C");

    return(convertView);
  }
}
