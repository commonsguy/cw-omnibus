/***
 Copyright (c) 2016 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 From _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.documenttree;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SettingsFragment extends PreferenceFragment
  implements TreeUriPreferenceHelper.Host,
  SharedPreferences.OnSharedPreferenceChangeListener {
  static final String STORAGE_FAKE_UUID="fake";
  private static final String PREF_DOC_TREE="documentTree";
  private static final String PREF_VOLUMES="storageVolume";
  private static final String PREF_STORAGE_URI="storageUri";
  private static final int REQUEST_DOC_TREE=123;
  private static final int REQUEST_STORAGE_VOLUME=
    REQUEST_DOC_TREE+1;
  private DocumentHelper docTreeHelper;
  private VolumeHelper volumeHelper;
  private Preference prefDocTree;
  private ListPreference prefVolumes;
  private SharedPreferences prefs;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.settings);

    prefDocTree=findPreference(PREF_DOC_TREE);
    prefs=prefDocTree
      .getSharedPreferences();
    prefs.registerOnSharedPreferenceChangeListener(this);
    onSharedPreferenceChanged(prefs, PREF_DOC_TREE);
    docTreeHelper=new DocumentHelper(this,
      prefDocTree);

    prefVolumes=(ListPreference)findPreference(PREF_VOLUMES);
    populateVolumes();
    onSharedPreferenceChanged(prefs, PREF_STORAGE_URI);
    volumeHelper=
      new VolumeHelper(this, prefVolumes,
        PREF_STORAGE_URI, Environment.DIRECTORY_DOCUMENTS);
  }

  @Override
  public void onDestroy() {
    prefDocTree
      .getSharedPreferences()
      .unregisterOnSharedPreferenceChangeListener(this);

    super.onDestroy();
  }

  @Override
  public void startActivityForHelper(Intent intent,
                                     TreeUriPreferenceHelper helper) {
    if (helper==docTreeHelper) {
      startActivityForResult(intent, REQUEST_DOC_TREE);
    }
    else if (helper==volumeHelper) {
      startActivityForResult(intent, REQUEST_STORAGE_VOLUME);
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode,
                               Intent data) {
    if (resultCode==Activity.RESULT_OK) {
      if (requestCode==REQUEST_DOC_TREE) {
        docTreeHelper.onActivityResult(data);
      }
      else if (requestCode==REQUEST_STORAGE_VOLUME) {
        volumeHelper.onActivityResult(data);
      }
    }
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences prefs,
                                        String key) {
    if (PREF_DOC_TREE.equals(key)) {
      prefDocTree.setSummary(prefs.getString(key, "<no value>"));
    }
    else if (PREF_STORAGE_URI.equals(key)) {
      prefVolumes
        .setSummary(prefs
          .getString(key, "<no value>").replaceAll("%", "%%"));
    }
  }

  private void populateVolumes() {
    StorageManager storage=
      getActivity().getSystemService(StorageManager.class);
    List<StorageVolume> volumes=storage.getStorageVolumes();

    Collections.sort(volumes, new Comparator<StorageVolume>() {
      @Override
      public int compare(StorageVolume lhs,
                         StorageVolume rhs) {
        return(lhs.getDescription(getActivity())
          .compareTo(rhs.getDescription(getActivity())));
      }
    });

    String[] displayNames=new String[volumes.size()];
    String[] uuids=new String[volumes.size()];

    for (int i=0;i<volumes.size();i++) {
      displayNames[i]=volumes.get(i).getDescription(getActivity());
      uuids[i]=volumes.get(i).getUuid();

      if (uuids[i]==null) {
        uuids[i]=STORAGE_FAKE_UUID;
      }
    }

    prefVolumes.setEntries(displayNames);
    prefVolumes.setEntryValues(uuids);
  }
}
