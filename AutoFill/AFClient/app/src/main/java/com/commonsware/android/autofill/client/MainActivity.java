/***
 Copyright (c) 2017 CommonsWare, LLC
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

package com.commonsware.android.autofill.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final EditText username=(EditText)findViewById(R.id.username);
    final EditText passphrase=(EditText)findViewById(R.id.passphrase);
    final EditText cc=(EditText)findViewById(R.id.cc);

    findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
         if (ResultActivity.login(MainActivity.this,
           username.getText().toString(), passphrase.getText().toString(),
           cc.getText().toString())) {
           finish();
         }
         else {
           Toast.makeText(MainActivity.this, R.string.msg_invalid,
             Toast.LENGTH_LONG).show();
         }
      }
    });
  }
}
