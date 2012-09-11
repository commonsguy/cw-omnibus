package com.commonsware.android.anim.ftrans;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class MainActivity extends Activity implements Runnable {
  int count=1;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getFragmentManager().findFragmentById(android.R.id.content) == null) {
      findViewById(android.R.id.content).postDelayed(this, 1000);
    }
  }

  @Override
  public void run() {
    getFragmentManager().beginTransaction()
                        .add(android.R.id.content,
                             ButtonFragment.newInstance(count++))
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
  }

  void onClick() {
    if (count == 4) {
      remove();
    }
    else {
      getFragmentManager().beginTransaction()
                          .replace(android.R.id.content,
                                   ButtonFragment.newInstance(count++))
                          .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                          .commit();
    }
  }

  void remove() {
    getFragmentManager().beginTransaction()
                        .remove(getFragmentManager().findFragmentById(android.R.id.content))
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        .commit();
  }
}
