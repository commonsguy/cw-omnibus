/***
 Copyright (c) 2018 CommonsWare, LLC

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

package com.commonsware.android.activitymill;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
  private final String revisedTitle;

  static MainActivity sockItToMe(String revisedTitle) {
    return new MainActivity(revisedTitle);
  }

  private MainActivity(String revisedTitle) {
    this.revisedTitle=revisedTitle;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setTitle(revisedTitle);
  }
}
