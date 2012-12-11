package com.commonsware.android.mapsv2.basic;

import android.os.Bundle;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class LegalNoticesActivity extends SherlockActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.legal);

    TextView legal=(TextView)findViewById(R.id.legal);

    legal.setText(GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(this));
  }
}
