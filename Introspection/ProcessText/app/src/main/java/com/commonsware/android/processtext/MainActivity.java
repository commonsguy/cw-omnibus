/***
  Copyright (c) 2013-2014 CommonsWare, LLC
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

package com.commonsware.android.processtext;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends FragmentActivity
  implements QuestionsFragment.Contract {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getSupportFragmentManager().findFragmentById(android.R.id.content)==null) {
      String search=null;

      if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
        if (Intent.ACTION_PROCESS_TEXT.equals(getIntent().getAction())) {
          search=getIntent().getStringExtra(Intent.EXTRA_PROCESS_TEXT);

          if (search==null) {
            search=getIntent()
              .getStringExtra(Intent.EXTRA_PROCESS_TEXT_READONLY);
          }
        }
      }

      getSupportFragmentManager()
        .beginTransaction()
        .add(android.R.id.content,
          QuestionsFragment.newInstance(search))
        .commit();
    }
  }

  @Override
  public void onQuestion(Item question) {
    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M &&
      Intent.ACTION_PROCESS_TEXT.equals(getIntent().getAction()) &&
      getIntent().getStringExtra(Intent.EXTRA_PROCESS_TEXT)!=null) {
      setResult(Activity.RESULT_OK,
        new Intent().putExtra(Intent.EXTRA_PROCESS_TEXT, question.link));
      finish();
    }
    else {
      startActivity(new Intent(Intent.ACTION_VIEW,
        Uri.parse(question.link)));
    }
  }
}
