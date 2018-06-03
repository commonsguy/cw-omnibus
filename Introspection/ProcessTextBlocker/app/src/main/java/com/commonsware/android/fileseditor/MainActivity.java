/***
  Copyright (c) 2012-2017 CommonsWare, LLC
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

package com.commonsware.android.fileseditor;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.Toast;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AbstractPermissionActivity  {
  @Override
  protected String[] getDesiredPermissions() {
    return(new String[]{WRITE_EXTERNAL_STORAGE});
  }

  @Override
  protected void onPermissionDenied() {
    Toast
      .makeText(this, R.string.msg_sorry, Toast.LENGTH_LONG)
      .show();
    finish();
  }

  @Override
  protected void onReady(Bundle savedInstanceState) {
    setContentView(R.layout.main);

    ViewPager pager=findViewById(R.id.pager);

    pager.setAdapter(new SampleAdapter(this, getSupportFragmentManager()));
  }
}