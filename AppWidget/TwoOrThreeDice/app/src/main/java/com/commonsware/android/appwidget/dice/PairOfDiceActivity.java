package com.commonsware.android.appwidget.dice;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class PairOfDiceActivity extends Activity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    Toast.makeText(this, "App widget ready to be added!",
                   Toast.LENGTH_LONG).show();
    
    finish();
  }
}
