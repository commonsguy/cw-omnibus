/***
 * Copyright (c) 2008-2015 CommonsWare, LLC
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain	a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 * by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 * <p/>
 * From _The Busy Coder's Guide to Android Development_
 * https://commonsware.com/Android
 */

package com.commonsware.android.design.til;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

public class LaunchDemo extends AppCompatActivity {
  private TextInputLayout til;

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.main);

    til=(TextInputLayout)findViewById(R.id.til);
    til.setErrorEnabled(true);
  }

  public void showMe(View v) {
    EditText urlField=(EditText)findViewById(R.id.url);
    String url=urlField.getText().toString();

    if (Patterns.WEB_URL.matcher(url).matches()) {
      startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
    else {
      til.setError(getString(R.string.til_error));
    }
  }
}
