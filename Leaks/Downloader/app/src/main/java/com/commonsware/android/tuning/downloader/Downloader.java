/***
  Copyright (c) 2010-2011 CommonsWare, LLC
  
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

package com.commonsware.android.tuning.downloader;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Downloader extends IntentService {
  public static final String EXTRA_MESSENGER="com.commonsware.android.downloader.EXTRA_MESSENGER";
  private HttpClient client=null;

  public Downloader() {
    super("Downloader");
  }
  
  @Override
  public void onCreate() {
    super.onCreate();
    
    client=new DefaultHttpClient();
  }

  @Override	
  public void onHandleIntent(Intent i) {
    HttpGet getMethod=new HttpGet(i.getData().toString());
    int result=Activity.RESULT_CANCELED;
    
    try {
      ResponseHandler<byte[]> responseHandler=new ByteArrayResponseHandler();
      byte[] responseBody=client.execute(getMethod, responseHandler);
      File output=new File(Environment.getExternalStorageDirectory(),
                          i.getData().getLastPathSegment());
      
      if (output.exists()) {
        output.delete();
      }
      
      FileOutputStream fos=new FileOutputStream(output.getPath());
      
      fos.write(responseBody);
      fos.close();
      result=Activity.RESULT_OK;
    }
    catch (IOException e2) {
      Log.e(getClass().getName(), "Exception in download", e2);
    }
    
    Bundle extras=i.getExtras();
  
    if (extras!=null) {
      Messenger messenger=(Messenger)extras.get(EXTRA_MESSENGER);
      Message msg=Message.obtain();
      
      msg.arg1=result;
      
      try {
        messenger.send(msg);
      }
      catch (android.os.RemoteException e1) {
        Log.w(getClass().getName(), "Exception sending message", e1);
      }
    }
  }
}