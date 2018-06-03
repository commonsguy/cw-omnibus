package com.commonsware.android.slice.weather;

import android.app.Application;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class ThreeTenApp extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    AndroidThreeTen.init(this);
  }
}
