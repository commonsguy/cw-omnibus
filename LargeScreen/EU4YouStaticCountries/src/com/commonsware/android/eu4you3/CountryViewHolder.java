package com.commonsware.android.eu4you3;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

class CountryViewHolder {
  private TextView name=null;
  private ImageView flag=null;
  
  CountryViewHolder(View row) {
    name=(TextView)row.findViewById(R.id.name);
    flag=(ImageView)row.findViewById(R.id.flag);
  }
  
  TextView getName() {
    return(name);
  }
  
  ImageView getFlag() {
    return(flag);
  }
  
  void populateFrom(Country nation) {
    getName().setText(nation.name);
    getFlag().setImageResource(nation.flag);
  }
}