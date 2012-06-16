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

package com.commonsware.android.contacts.pick;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Contacts;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PickDemo extends Activity {
  private static final int PICK_REQUEST=1337;
  private static Uri CONTENT_URI=null;
  
  static {
    int sdk=new Integer(Build.VERSION.SDK).intValue();
    
    if (sdk>=5) {
      try {
        Class<?> clazz=Class.forName("android.provider.ContactsContract$Contacts");
      
        CONTENT_URI=(Uri)clazz.getField("CONTENT_URI").get(clazz);
      }
      catch (Throwable t) {
        Log.e("PickDemo", "Exception when determining CONTENT_URI", t);
      }
    }
    else {
      CONTENT_URI=Contacts.People.CONTENT_URI;
    }
  }

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    
    if (CONTENT_URI==null) {
      Toast
        .makeText(this, "We are experiencing technical difficulties...",
                  Toast.LENGTH_LONG)
        .show();
      finish();
      
      return;
    }
    
    setContentView(R.layout.main);
    
    Button btn=(Button)findViewById(R.id.pick);
    
    btn.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        Intent i=new Intent(Intent.ACTION_PICK, CONTENT_URI);

        startActivityForResult(i, PICK_REQUEST);
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
    if (requestCode==PICK_REQUEST) {
      if (resultCode==RESULT_OK) {
          startActivity(new Intent(Intent.ACTION_VIEW,
                                    data.getData()));
      }
    }
  }
}