package com.commonsware.android.animator.fadebc;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

public class MainActivity extends Activity implements Runnable {
  private static int PERIOD=2000;
  private TextView fadee=null;
  private boolean fadingOut=true;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    fadee=(TextView)findViewById(R.id.fadee);
  }

  @Override
  public void onResume() {
    super.onResume();

    run();
  }

  @Override
  public void onPause() {
    fadee.removeCallbacks(this);

    super.onPause();
  }

  @Override
  public void run() {
    if (fadingOut) {
      animate(fadee).alpha(0).setDuration(PERIOD);
      fadee.setText(R.string.fading_out);
    }
    else {
      animate(fadee).alpha(1).setDuration(PERIOD);
      fadee.setText(R.string.coming_back);
    }

    fadingOut=!fadingOut;

    fadee.postDelayed(this, PERIOD);
  }
}
