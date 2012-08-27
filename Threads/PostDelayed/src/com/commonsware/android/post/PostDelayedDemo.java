package com.commonsware.android.post;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class PostDelayedDemo extends Activity {
  private static final int PERIOD=5000;
  private View root=null;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    root=findViewById(android.R.id.content);
  }
  
  @Override
  public void onResume() {
    super.onResume();
    
    everyFiveSeconds.run();
  }
  
  @Override
  public void onPause() {
    root.removeCallbacks(everyFiveSeconds);
    
    super.onPause();
  }

  Runnable everyFiveSeconds=new Runnable() {
    public void run() {
      Toast.makeText(PostDelayedDemo.this, "Who-hoo!",
                     Toast.LENGTH_SHORT).show();
      root.postDelayed(this, PERIOD);
    }
  };
}
