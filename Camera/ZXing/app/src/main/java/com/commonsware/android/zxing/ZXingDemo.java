/***
  Copyright (c) 2008-2010 CommonsWare, LLC
  
  Licensed under the Apache License, Version 2.0 (the "License"); you may
  not use this file except in compliance with the License. You may obtain
  a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package com.commonsware.android.zxing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ZXingDemo extends Activity {
  TextView format=null;
  TextView contents=null;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    
    format=(TextView)findViewById(R.id.format);
    contents=(TextView)findViewById(R.id.contents);
  }
  
  public void doScan(View v) {
    (new IntentIntegrator(this)).initiateScan();
  }
  
  public void onActivityResult(int request, int result, Intent i) {
    IntentResult scan=IntentIntegrator.parseActivityResult(request,
                                                           result,
                                                           i);
    
    if (scan!=null) {
      format.setText(scan.getFormatName());
      contents.setText(scan.getContents());
    }
  }
  
  @Override
  public void onSaveInstanceState(Bundle state) {
    state.putString("format", format.getText().toString());
    state.putString("contents", contents.getText().toString());
  }
  
  @Override
  public void onRestoreInstanceState(Bundle state) {
    format.setText(state.getString("format"));
    contents.setText(state.getString("contents"));
  }
}
