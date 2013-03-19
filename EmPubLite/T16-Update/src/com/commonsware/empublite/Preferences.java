package com.commonsware.empublite;

import java.util.List;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class Preferences extends SherlockPreferenceActivity {
  @SuppressWarnings("deprecation")
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (Build.VERSION.SDK_INT<Build.VERSION_CODES.HONEYCOMB) {
      addPreferencesFromResource(R.xml.pref_display);
    }
  }
  
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  @Override
  public void onBuildHeaders(List<Header> target) {
    loadHeadersFromResource(R.xml.preference_headers, target);
  }
}
