/***
  Copyright (c) 2012 CommonsWare, LLC
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

package com.commonsware.watchauth;

import android.annotation.TargetApi;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;

@TargetApi(11)
public class AuthPreferenceFragment extends PreferenceFragment
    implements OnPreferenceChangeListener {
  private static final String KEY_ENABLED="enabled";
  private DevicePolicyManager mgr=null;
  private ComponentName cn=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    cn=new ComponentName(getActivity(), AuthAdminReceiver.class);
    mgr=
        (DevicePolicyManager)getActivity().getSystemService(Context.DEVICE_POLICY_SERVICE);

    addPreferencesFromResource(R.xml.preferences);
  }

  @Override
  public void onResume() {
    super.onResume();

    CheckBoxPreference pref=(CheckBoxPreference)findPreference(KEY_ENABLED);
    
    pref.setChecked(mgr.isAdminActive(cn));
    pref.setOnPreferenceChangeListener(this);
  }

  @Override
  public boolean onPreferenceChange(Preference pref, Object newValue) {
    if (KEY_ENABLED.equals(pref.getKey())) {
      boolean value=((Boolean)newValue).booleanValue();

      if (value) {
        Intent intent=
            new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                        getString(R.string.device_admin_explanation));
        startActivity(intent);
      }
      else {
        mgr.removeActiveAdmin(cn);
      }
    }

    return(true);
  }
}
