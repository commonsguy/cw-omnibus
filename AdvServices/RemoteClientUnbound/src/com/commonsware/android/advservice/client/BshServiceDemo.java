/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Advanced Android Development_
    http://commonsware.com/AdvAndroid
*/

package com.commonsware.android.advservice.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class BshServiceDemo extends Activity {
  private static final String SCRIPT="com.commonsware.SCRIPT";
  private static final String BROADCAST_ACTION="com.commonsware.BROADCAST_ACTION";
  private static final String BROADCAST_PACKAGE="com.commonsware.BROADCAST_PACKAGE";
  private static final String PRIVATE_ACTION="com.commonsware.PRIVATE_BROADCAST_ACTION";
  private static final String PENDING_RESULT="com.commonsware.PENDING_RESULT";
  private static final String PAYLOAD="com.commonsware.PAYLOAD";
  private static final String RESULT_CODE="com.commonsware.RESULT_CODE";
  private static final int SUCCESS=1337;
  private static final int REQUEST_CODE=24601;
  private EditText script=null;
  
  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.main);
  
    script=(EditText)findViewById(R.id.script);
    registerReceiver(onBroadcast, new IntentFilter(PRIVATE_ACTION));
  }
    
  @Override
  public void onDestroy() {
    super.onDestroy();
    
    unregisterReceiver(onBroadcast);
  }
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode,
                                  Intent data) {
    if (requestCode==REQUEST_CODE && resultCode==RESULT_OK) {
      handleResult(data);
    }
  }

  public void evalPrivateBroadcast(View v) {
    Intent i=new Intent("com.commonsware.android.advservice.IScript");
    
    i.putExtra(SCRIPT, script.getText().toString());
    i.putExtra(BROADCAST_ACTION, PRIVATE_ACTION);
    i.putExtra(BROADCAST_PACKAGE,
                "com.commonsware.android.advservice.client");
    
    startService(i);
  }
  
  public void evalPendingResult(View v) {
    Intent i=new Intent("com.commonsware.android.advservice.IScript");
    
    i.putExtra(SCRIPT, script.getText().toString());
    i.putExtra(PENDING_RESULT, createPendingResult(REQUEST_CODE,
                                                   null,
                                                   PendingIntent.FLAG_ONE_SHOT));
    
    startService(i);
  }
  
  private void success(String result) {
    Toast
      .makeText(BshServiceDemo.this, result, Toast.LENGTH_LONG)
      .show();
  }
  
  private void failure(String error) {
    AlertDialog.Builder builder=
              new AlertDialog.Builder(BshServiceDemo.this);
    
    builder
      .setTitle("Exception!")
      .setMessage(error)
      .setPositiveButton("OK", null)
      .show();
  }
  
  private void handleResult(Intent i) {
    String result=i.getStringExtra(PAYLOAD);
    int resultCode=i.getIntExtra(RESULT_CODE, -1);
    
    if (resultCode==SUCCESS) {
      success(result);
    }
    else {
      failure(result);
    }
  }
  
  private BroadcastReceiver onBroadcast=new BroadcastReceiver() {
    @Override
    public void onReceive(Context ctxt, Intent i) {
      handleResult(i);
    }
  };
}