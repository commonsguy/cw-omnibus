/***
  Copyright (c) 2008-2012 CommonsWare, LLC
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

package com.commonsware.abj.interp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.test.AndroidTestCase;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;

abstract public class InterpreterTestCase extends AndroidTestCase {
  abstract protected String getInterpreterName();
  
  private static String ACTION="com.commonsware.abj.interp.InterpreterTestCase";
  private CountDownLatch latch=new CountDownLatch(1);
  private Bundle results=null;
  
  protected void setUp() throws Exception {
    super.setUp();
    
    getContext().registerReceiver(onBroadcast, new IntentFilter(ACTION));
  }
  
  protected void tearDown() {
    getContext().unregisterReceiver(onBroadcast);
  }
  
  protected Bundle execServiceTest(Bundle input) {
    Intent i=new Intent(getInterpreterName());
    
    i.putExtra(InterpreterService.BUNDLE, input);
    i.putExtra(InterpreterService.BROADCAST_ACTION, ACTION);
    
    getContext().startService(i);
    
    try {
      latch.await(5000, TimeUnit.MILLISECONDS);
    }
    catch (InterruptedException e) {
      // just keep rollin'
    }
    
    return(results);
  }
    
  private BroadcastReceiver onBroadcast=new BroadcastReceiver() {
    @Override
    public void onReceive(Context ctxt, Intent i) {
      results=i.getExtras();
      latch.countDown();
    }
  };
}