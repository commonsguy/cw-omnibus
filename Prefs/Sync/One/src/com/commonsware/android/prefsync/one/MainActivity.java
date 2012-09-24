/***
  Copyright (c) 2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _Tuning Android Applications_
    http://commonsware.com/AndTuning
*/

package com.commonsware.android.prefsync.one;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements
    OnSharedPreferenceChangeListener, OnClickListener {
  private static final String KEY="test";
  private Button btn=null;
  private SharedPreferences prefs=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    btn=new Button(this);
    setContentView(btn);
    prefs=PreferenceManager.getDefaultSharedPreferences(this);

    prefs.registerOnSharedPreferenceChangeListener(this);
    btn.setText(String.valueOf(prefs.getInt(KEY, 0)));
    btn.setOnClickListener(this);
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences prefs,
                                        String key) {
    btn.setText(String.valueOf(prefs.getInt(KEY, 0)));
  }

  @Override
  public void onClick(View v) {
    prefs.edit().putInt(KEY, prefs.getInt("test", 0) + 1).apply();
  }
}