/***
  Copyright (c) 2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  Covered in detail in the book _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.wc.elv;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.json.JSONObject;

public class MainActivity extends Activity implements
    OnChildClickListener, OnGroupClickListener, OnGroupExpandListener,
    OnGroupCollapseListener {
  private ExpandableListAdapter adapter=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    InputStream raw=getResources().openRawResource(R.raw.sample);
    BufferedReader in=new BufferedReader(new InputStreamReader(raw));
    String str;
    StringBuffer buf=new StringBuffer();

    try {
      while ((str=in.readLine()) != null) {
        buf.append(str);
        buf.append('\n');
      }

      in.close();

      JSONObject model=new JSONObject(buf.toString());

      ExpandableListView elv=(ExpandableListView)findViewById(R.id.elv);

      adapter=new JSONExpandableListAdapter(getLayoutInflater(), model);
      elv.setAdapter(adapter);

      elv.setOnChildClickListener(this);
      elv.setOnGroupClickListener(this);
      elv.setOnGroupExpandListener(this);
      elv.setOnGroupCollapseListener(this);
    }
    catch (Exception e) {
      Log.e(getClass().getName(), "Exception reading JSON", e);
    }
  }

  @Override
  public boolean onChildClick(ExpandableListView parent, View v,
                              int groupPosition, int childPosition,
                              long id) {
    Toast.makeText(this,
                   adapter.getChild(groupPosition, childPosition)
                          .toString(), Toast.LENGTH_SHORT).show();

    return(false);
  }

  @Override
  public boolean onGroupClick(ExpandableListView parent, View v,
                              int groupPosition, long id) {
    Toast.makeText(this, adapter.getGroup(groupPosition).toString(),
                   Toast.LENGTH_SHORT).show();

    return(false);
  }

  @Override
  public void onGroupExpand(int groupPosition) {
    Toast.makeText(this,
                   "Expanding: "
                       + adapter.getGroup(groupPosition).toString(),
                   Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onGroupCollapse(int groupPosition) {
    Toast.makeText(this,
                   "Collapsing: "
                       + adapter.getGroup(groupPosition).toString(),
                   Toast.LENGTH_SHORT).show();
  }
}
