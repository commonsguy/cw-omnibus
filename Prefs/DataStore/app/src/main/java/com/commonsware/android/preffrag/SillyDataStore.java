/***
 Copyright (c) 2008-2015 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.preffrag;

import android.preference.PreferenceDataStore;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class SillyDataStore implements PreferenceDataStore {
  static private final SillyDataStore INSTANCE=new SillyDataStore();
  private Map<String, Object> cache=new HashMap<>();

  static SillyDataStore get() {
    return(INSTANCE);
  }

  private SillyDataStore() {
    // just here to prevent accidental creation from outside
  }

  @Override
  public void putString(String key, String value) {
    cache.put(key, value);
  }

  @Override
  public void putStringSet(String key, Set<String> values) {
    cache.put(key, values);
  }

  @Override
  public void putInt(String key, int value) {
    cache.put(key, value);
  }

  @Override
  public void putLong(String key, long value) {
    cache.put(key, value);
  }

  @Override
  public void putFloat(String key, float value) {
    cache.put(key, value);
  }

  @Override
  public void putBoolean(String key, boolean value) {
    cache.put(key, value);
  }

  @SuppressWarnings("Since15")
  @Override
  public String getString(String key, String defValue) {
    return((String)cache.getOrDefault(key, defValue));
  }

  @SuppressWarnings("Since15")
  @Override
  public Set<String> getStringSet(String key, Set<String> defValues) {
    return((Set<String>)cache.getOrDefault(key, defValues));
  }

  @SuppressWarnings("Since15")
  @Override
  public int getInt(String key, int defValue) {
    return((Integer)cache.getOrDefault(key, defValue));
  }

  @SuppressWarnings("Since15")
  @Override
  public long getLong(String key, long defValue) {
    return((Long)cache.getOrDefault(key, defValue));
  }

  @SuppressWarnings("Since15")
  @Override
  public float getFloat(String key, float defValue) {
    return((Float)cache.getOrDefault(key, defValue));
  }

  @SuppressWarnings("Since15")
  @Override
  public boolean getBoolean(String key, boolean defValue) {
    return((Boolean)cache.getOrDefault(key, defValue));
  }
}
