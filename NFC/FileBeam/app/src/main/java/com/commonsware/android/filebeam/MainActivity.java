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

package com.commonsware.android.filebeam;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
  private NfcAdapter adapter=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    adapter=NfcAdapter.getDefaultAdapter(this);

    if (!adapter.isNdefPushEnabled()) {
      Toast.makeText(this, R.string.sorry, Toast.LENGTH_LONG).show();
      finish();
    }
    else {
      Intent i=new Intent(Intent.ACTION_GET_CONTENT);
      
      i.setType("*/*").addCategory(Intent.CATEGORY_OPENABLE);
      startActivityForResult(i, 0);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
                                  Intent data) {
    if (requestCode==0 && resultCode==RESULT_OK) {
      adapter.setBeamPushUris(new Uri[] {data.getData()}, this);
      
      Button btn=new Button(this);
      
      btn.setText(R.string.over);
      btn.setOnClickListener(this);
      setContentView(btn);
    }
  }

  @Override
  public void onClick(View v) {
    finish();
  }
}
