package com.commonsware.empublite;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.preference.PreferenceFragment;

@TargetApi(11)
public class StockPreferenceFragment extends PreferenceFragment {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    int res=getActivity()
              .getResources()
              .getIdentifier(getArguments().getString("resource"),
                              "xml",
                              getActivity().getPackageName());
    
    addPreferencesFromResource(res);
  }
}
