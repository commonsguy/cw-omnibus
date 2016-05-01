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

import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

abstract public class BaseTask extends AsyncTask<Void, Void, Void> {
  abstract String doTest();
  long startTime=SystemClock.uptimeMillis();
  View v=null;
  TextView msg=null;
  
  BaseTask(TextView msg, View v) {
    this.msg=msg;
    this.v=v;
    msg.setText(R.string.wait);
  }
  
  @Override
  protected Void doInBackground(Void... unused) {
    for (int i=0;i<100000;i++) {
      doTest();
    }
    
    return(null);
  }
  
  @Override
  protected void onPostExecute(Void unused) {
    long duration=SystemClock.uptimeMillis()-startTime;
    
    msg.setText("Done: "+String.valueOf(duration));
    v.setEnabled(true);
  }
}
