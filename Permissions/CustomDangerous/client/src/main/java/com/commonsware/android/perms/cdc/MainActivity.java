package com.commonsware.android.perms.cdc;

import android.content.ComponentName;
import android.content.Intent;
import android.widget.Toast;

public class MainActivity extends AbstractPermissionActivity {
  private static String[] PERMS=
    {"com.commonsware.android.perm.custdanger.SOMETHING"};

  @Override
  protected String[] getDesiredPermissions() {
    return(PERMS);
  }

  @Override
  protected void onPermissionDenied() {
    Toast.makeText(this, R.string.msg_toast, Toast.LENGTH_LONG).show();
    finish();
  }

  @Override
  protected void onReady() {
    ComponentName cn=
      new ComponentName("com.commonsware.android.perm.custdanger",
        "com.commonsware.android.perm.custdanger.MainActivity");
    Intent i=new Intent().setComponent(cn);

    startActivity(i);
    finish();
  }
}
