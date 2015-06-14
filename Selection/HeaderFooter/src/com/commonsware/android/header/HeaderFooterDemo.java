/***
  Copyright (c) 2008-2012 CommonsWare, LLC
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

package com.commonsware.android.header;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class HeaderFooterDemo extends ListActivity {
  private static String[] items={"lorem", "ipsum", "dolor",
                                  "sit", "amet", "consectetuer",
                                  "adipiscing", "elit", "morbi",
                                  "vel", "ligula", "vitae",
                                  "arcu", "aliquet", "mollis",
                                  "etiam", "vel", "erat",
                                  "placerat", "ante",
                                  "porttitor", "sodales",
                                  "pellentesque", "augue",
                                  "purus"};
  private long startTime=SystemClock.uptimeMillis();
  private boolean areWeDeadYet=false;
  
  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.main);
    getListView().addHeaderView(buildHeader());
    getListView().addFooterView(buildFooter());
    setListAdapter(new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1,
                        items));
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    
    areWeDeadYet=true;
  }
  
  private View buildHeader() {
    Button btn=new Button(this);
    
    btn.setText("Randomize!");
    btn.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        List<String> list=Arrays.asList(items);
        
        Collections.shuffle(list);
        
        setListAdapter(new ArrayAdapter<String>(HeaderFooterDemo.this,
                            android.R.layout.simple_list_item_1,
                            list));
      }
    });
    
    return(btn);
  }
  
  private View buildFooter() {
    TextView txt=new TextView(this);
    
    updateFooter(txt);
    
    return(txt);
  }
  
  private void updateFooter(final TextView txt) {
    long runtime=(SystemClock.uptimeMillis()-startTime)/1000;
    
    txt.setText(String.valueOf(runtime)+" seconds since activity launched");
    
    if (!areWeDeadYet) {
      getListView().postDelayed(new Runnable() {
        public void run() {
          updateFooter(txt);  
        }
      }, 1000);
    }
  }
}