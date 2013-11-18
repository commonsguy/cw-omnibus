package com.commonsware.android.gradle.hello;

import android.util.Log;
import android.view.MenuItem;

public class MainActivityOptionsStrategy {
  public static boolean onOptionsItemSelected(MenuItem item) {
    Log.d("HelloProductFlavors", "chocolate!");

    return(false);
  }
}
