/***
  Copyright (c) 2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
*/

package com.commonsware.android.progloc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainActivity extends Activity implements OnCheckedChangeListener {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    CompoundButton cb=(CompoundButton)findViewById(R.id.enabled);
    
    cb.setOnCheckedChangeListener(this);
  }

  @Override
  public void onCheckedChanged(CompoundButton cb, boolean isChecked) {
    Intent i=new Intent(this, LocationService.class);
    
    if (isChecked) {
      startService(i);
    }
    else {
      stopService(i);
    }
  }
}
