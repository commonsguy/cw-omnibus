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

package com.commonsware.android.sawmonitor;

import android.content.ComponentName;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static android.Manifest.permission.SYSTEM_ALERT_WINDOW;

public class SettingsFragment extends PreferenceFragment
  implements Preference.OnPreferenceChangeListener {
  private PackageManager pm;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.settings);
    pm=getActivity().getPackageManager();

    SwitchPreference enabled=(SwitchPreference)findPreference("enabled");

    enabled.setOnPreferenceChangeListener(this);

    populateWhitelist((MultiSelectListPreference)findPreference("whitelist"));
  }

  @Override
  public boolean onPreferenceChange(Preference preference,
                                    Object newValue) {
    Boolean state=(Boolean)newValue;
    ComponentName cn=
      new ComponentName(getActivity(), PackageReceiver.class);
    int componentState=state ?
      PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
      PackageManager.COMPONENT_ENABLED_STATE_DISABLED;

    pm.setComponentEnabledSetting(cn, componentState,
      PackageManager.DONT_KILL_APP);

    return(true);
  }

  void populateWhitelist(MultiSelectListPreference whitelist) {
    List<ApplicationInfo> apps=pm.getInstalledApplications(0);

    Collections.sort(apps,
      new ApplicationInfo.DisplayNameComparator(pm));

    ArrayList<CharSequence> displayNames=
      new ArrayList<CharSequence>();
    ArrayList<String> packageNames=new ArrayList<String>();

    for (ApplicationInfo app : apps) {
      try {
        PackageInfo pkgInfo=
          pm.getPackageInfo(app.packageName,
            PackageManager.GET_PERMISSIONS);

        if (pkgInfo.requestedPermissions!=null) {
          for (String perm : pkgInfo.requestedPermissions) {
            if (SYSTEM_ALERT_WINDOW.equals(perm)) {
              displayNames.add(app.loadLabel(pm));
              packageNames.add(app.packageName);
              break;
            }
          }
        }
      }
      catch (PackageManager.NameNotFoundException e) {
        // should not happen, quietly ignore
      }
    }

    whitelist
      .setEntries(displayNames
        .toArray(new CharSequence[displayNames.size()]));
    whitelist
      .setEntryValues(packageNames
        .toArray(new String[packageNames.size()]));
  }
}
