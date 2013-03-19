package com.commonsware.android.fs;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainActivity extends Activity implements
    OnCheckedChangeListener {
  private RadioGroup screenStyleGroup=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    screenStyleGroup=(RadioGroup)findViewById(R.id.screenStyle);
    screenStyleGroup.setOnCheckedChangeListener(this);
  }

  @Override
  public void onCheckedChanged(RadioGroup group, int checkedId) {
    updateUI(group, screenStyleGroup.getCheckedRadioButtonId());
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  private void updateUI(View v, int screenStyle) {
    int flags=0;

    switch (screenStyle) {
      case R.id.normal:
        flags=View.SYSTEM_UI_FLAG_VISIBLE;
        break;

      case R.id.lowProfile:
        flags=View.SYSTEM_UI_FLAG_LOW_PROFILE;
        break;

      case R.id.hideNav:
        flags=View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        break;

      case R.id.hideStatusBar:
        flags=View.SYSTEM_UI_FLAG_FULLSCREEN;
        break;

      case R.id.fullScreen:
        flags=
            View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        break;
    }

    v.setSystemUiVisibility(flags);
  }
}
