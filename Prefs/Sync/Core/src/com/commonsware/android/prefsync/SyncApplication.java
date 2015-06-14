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
    https://commonsware.com/AndTuning
*/

package com.commonsware.android.prefsync;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.preference.PreferenceManager;
import java.util.HashSet;
import java.util.Set;

public class SyncApplication extends Application implements
    OnSharedPreferenceChangeListener {
  public static final String ACTION_SYNC_PREF=
      "com.commonsware.android.prefsync.action.SYNC_PREF";
  public static final String EXTRA_KEY="key";
  public static final String EXTRA_VALUE="value";
  public static final String EXTRA_SENDER="sender";
  public static final String EXTRA_TYPE="type";
  public static final int TYPE_BOOLEAN=1;
  public static final int TYPE_FLOAT=2;
  public static final int TYPE_INT=3;
  public static final int TYPE_LONG=4;
  public static final int TYPE_STRING=5;
  public static final int TYPE_STRINGSET=6;
  public static final String PERM_SYNC_PREF=
      "com.commonsware.android.prefsync.permission.SYNC_PREF";
  private SharedPreferences prefs=null;

  @Override
  public void onCreate() {
    super.onCreate();

    prefs=PreferenceManager.getDefaultSharedPreferences(this);
    prefs.registerOnSharedPreferenceChangeListener(this);
  }

  @TargetApi(11)
  @Override
  public void onSharedPreferenceChanged(SharedPreferences prefs,
                                        String key) {
    Intent i=new Intent(ACTION_SYNC_PREF);
    Object value=prefs.getAll().get(key);

    i.putExtra(EXTRA_SENDER, getPackageName());
    i.putExtra(EXTRA_KEY, key);

    if (value instanceof Boolean) {
      i.putExtra(EXTRA_VALUE, (Boolean)value);
      i.putExtra(EXTRA_TYPE, TYPE_BOOLEAN);
    }
    else if (value instanceof Float) {
      i.putExtra(EXTRA_VALUE, (Float)value);
      i.putExtra(EXTRA_TYPE, TYPE_FLOAT);
    }
    else if (value instanceof Integer) {
      i.putExtra(EXTRA_VALUE, (Integer)value);
      i.putExtra(EXTRA_TYPE, TYPE_INT);
    }
    else if (value instanceof Long) {
      i.putExtra(EXTRA_VALUE, (Long)value);
      i.putExtra(EXTRA_TYPE, TYPE_LONG);
    }
    else if (value instanceof String) {
      i.putExtra(EXTRA_VALUE, (String)value);
      i.putExtra(EXTRA_TYPE, TYPE_STRING);
    }
    else if (value instanceof Set<?>
        && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      i.putExtra(EXTRA_VALUE,
                 prefs.getStringSet(key, null).toArray(new String[0]));
      i.putExtra(EXTRA_TYPE, TYPE_STRINGSET);
    }

    sendBroadcast(i, PERM_SYNC_PREF);
  }

  @TargetApi(11)
  void applySync(Intent i) {
    if (!i.getStringExtra(EXTRA_SENDER).equals(getPackageName())) {
      SharedPreferences.Editor editor=prefs.edit();
      String key=i.getStringExtra(EXTRA_KEY);

      switch (i.getIntExtra(EXTRA_TYPE, -1)) {
        case TYPE_BOOLEAN:
          editor.putBoolean(key, i.getBooleanExtra(EXTRA_VALUE, false));
          break;
        case TYPE_FLOAT:
          editor.putFloat(key, i.getFloatExtra(EXTRA_VALUE, 0f));
          break;
        case TYPE_INT:
          editor.putInt(key, i.getIntExtra(EXTRA_VALUE, 0));
          break;
        case TYPE_LONG:
          editor.putLong(key, i.getLongExtra(EXTRA_VALUE, 0L));
          break;
        case TYPE_STRING:
          editor.putString(key, i.getStringExtra(EXTRA_VALUE));
          break;
        case TYPE_STRINGSET:
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Set<String> setValue=new HashSet<String>();

            for (String s : i.getStringArrayExtra(EXTRA_VALUE)) {
              setValue.add(s);
            }

            editor.putStringSet(key, setValue);
          }
          break;
      }

      editor.apply();
    }
  }
}
