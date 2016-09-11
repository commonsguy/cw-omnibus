package com.commonsware.android.sidecar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class TaskRootActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    startActivity((Intent)getIntent().getParcelableExtra(Intent.EXTRA_INTENT));
    finish();
  }
}
