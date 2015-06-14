/***
  Copyright (c) 2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
*/
   
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
