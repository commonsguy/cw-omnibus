/***
 Copyright (c) 2017 CommonsWare, LLC
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

package com.commonsware.android.location.background;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;
import android.widget.Toast;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AbstractPermissionActivity {
  @Override
  protected String[] getDesiredPermissions() {
    return(new String[] {ACCESS_FINE_LOCATION});
  }

  @Override
  protected void onPermissionDenied() {
    Toast.makeText(this, R.string.msg_denied, Toast.LENGTH_LONG).show();
  }

  @Override
  protected void onReady() {
    if (getFragmentManager().findFragmentById(android.R.id.content)==null) {
      getFragmentManager().beginTransaction()
        .add(android.R.id.content,
          new Prefs()).commit();
    }
  }

  public static class Prefs extends PreferenceFragment
    implements Preference.OnPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      getActivity().stopService(new Intent(getActivity(),
        LocationPollerService.class));

      addPreferencesFromResource(R.xml.prefs);

      Preference pref=findPreference("delay");

      updateSummary((ListPreference)pref,
        pref.getSharedPreferences().getString(pref.getKey(), null));
      pref.setOnPreferenceChangeListener(
        new Preference.OnPreferenceChangeListener() {
          @Override
          public boolean onPreferenceChange(Preference pref,
                                            Object newValue) {
            updateSummary((ListPreference)pref, newValue.toString());

            return(true);
          }
        });

      pref=findPreference("running");
      ((SwitchPreference)pref).setChecked(false);
      pref.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object newValue) {
      Boolean value=(Boolean)newValue;
      Intent i=new Intent(getActivity(), LocationPollerService.class);

      if (value) {
        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.N_MR1) {
          getActivity().startForegroundService(i);
        }
        else {
          getActivity().startService(i);
        }
      }
      else {
        getActivity().stopService(i);
      }

      getActivity().finish();

      return(true);
    }

    private void updateSummary(ListPreference pref, String value) {
      if (value==null || value.length()==0) {
        pref.setSummary(R.string.missing_text);
      }
      else {
        int index=pref.findIndexOfValue(value);

        if (index==-1) {
          pref.setSummary(R.string.missing_text);
        }
        else {
          pref.setSummary(pref.getEntries()[index]);
        }
      }
    }
  }
}
