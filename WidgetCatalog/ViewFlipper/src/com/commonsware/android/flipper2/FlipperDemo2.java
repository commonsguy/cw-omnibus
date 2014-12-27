/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
	
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
*/

package com.commonsware.android.flipper2;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class FlipperDemo2 extends Activity {
  static String[] items={"lorem", "ipsum", "dolor", "sit", "amet",
                          "consectetuer", "adipiscing", "elit",
                          "morbi", "vel", "ligula", "vitae",
                          "arcu", "aliquet", "mollis", "etiam",
                          "vel", "erat", "placerat", "ante",
                          "porttitor", "sodales", "pellentesque",
                          "augue", "purus"};
  
  
  ViewFlipper flipper;
  
  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.main);
    
    LayoutParams p = new LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT );
    
    flipper=(ViewFlipper)findViewById(R.id.details);
    
    Integer[] colors = { Color.BLACK, Color.BLUE, Color.DKGRAY, Color.GRAY, Color.GREEN, Color.LTGRAY, Color.RED, Color.YELLOW, Color.LTGRAY };
    
    for (String item : items) {
    	
      TextView t = new TextView(this);
      t.setLayoutParams(p);
      t.setText( item );
      
      t.setGravity( Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL );
      t.setBackgroundColor( colors[(int)(Math.random() * (colors.length-1))] );
      t.setTextColor(colors[(int)(Math.random() * (colors.length-1))]);
   
      flipper.addView(t,
                      new ViewGroup.LayoutParams(
                              ViewGroup.LayoutParams.FILL_PARENT,
                              ViewGroup.LayoutParams.FILL_PARENT));
    }
    
    flipper.setFlipInterval(2000);
    flipper.startFlipping();
  }
}
