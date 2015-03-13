/***
 Copyright (c) 2015 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 From _The Busy Coder's Guide to Android Development_
 http://commonsware.com/Android
 */

package com.commonsware.android.recyclerview.singleactivatedlist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

class RowController extends RecyclerView.ViewHolder
    implements View.OnClickListener {
  private ChoiceCapableAdapter adapter;
  private TextView label=null;
  private TextView size=null;
  private ImageView icon=null;
  private String template=null;
  private View row=null;

  RowController(ChoiceCapableAdapter adapter, View row) {
    super(row);

    this.row=row;
    this.adapter=adapter;
    label=(TextView)row.findViewById(R.id.label);
    size=(TextView)row.findViewById(R.id.size);
    icon=(ImageView)row.findViewById(R.id.icon);

    template=size.getContext().getString(R.string.size_template);

    row.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    boolean isCheckedNow=adapter.isChecked(getPosition());

    adapter.onChecked(getPosition(), !isCheckedNow);
    row.setActivated(!isCheckedNow);
  }

  void bindModel(String item) {
    label.setText(item);
    size.setText(String.format(template, item.length()));

    if (item.length()>4) {
      icon.setImageResource(R.drawable.delete);
    }
    else {
      icon.setImageResource(R.drawable.ok);
    }

    setChecked(adapter.isChecked(getPosition()));
  }

  void setChecked(boolean isChecked) {
    row.setActivated(isChecked);
  }
}
