/***
  Copyright (c) 2011 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _Tuning Android Applications_
    https://commonsware.com/AndTuning
*/

   
package com.commonsware.android.traceview;

import android.view.View;
import android.widget.TextView;

public class StringFormatActivity extends BaseActivity {
  StringFormatTask createTask(TextView msg, View v) {
    return(new StringFormatTask(msg, v));
  }
  
  class StringFormatTask extends BaseTask {
    StringFormatTask(TextView msg, View v) {
      super(msg, v);
    }
    
    protected String doTest() {
      return(String.format("This is a string%1$s and also has %2$d hyphens in it",
                            " -- that varies --", 4));
    }
  }
}