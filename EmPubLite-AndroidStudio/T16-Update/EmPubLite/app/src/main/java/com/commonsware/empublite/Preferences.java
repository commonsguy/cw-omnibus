package com.commonsware.empublite;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class Preferences extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getFragmentManager().findFragmentById(android.R.id.content)==null) {
      getFragmentManager()
        .beginTransaction()
        .add(android.R.id.content, new Display())
        .commit();
    }
  }

  public static class Display extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.pref_display);
    }
  }
}