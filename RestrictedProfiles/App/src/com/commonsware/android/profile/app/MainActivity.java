package com.commonsware.android.profile.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.UserManager;
import android.widget.Toast;

public class MainActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    UserManager mgr=(UserManager)getSystemService(USER_SERVICE);
    Bundle restrictions=
        mgr.getApplicationRestrictions(getPackageName());

    if (restrictions.keySet().size() > 0) {
      setContentView(R.layout.activity_main);

      RestrictionsFragment f=
          (RestrictionsFragment)getFragmentManager().findFragmentById(R.id.contents);

      f.showRestrictions(restrictions);
    }
    else {
      Toast.makeText(this, R.string.no_restrictions, Toast.LENGTH_LONG)
           .show();
      finish();
    }
  }
}
