/***
 Copyright (c) 2008-2012 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.image;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Magnifier;

public class ImageViewDemo extends Activity {
  ImageView icon;

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);
    setContentView(R.layout.main);

    icon=findViewById(R.id.icon);

    icon.setOnClickListener(view -> {
      Magnifier mag=new Magnifier(icon);

      mag.show(icon.getWidth()/2, icon.getHeight()/2);
    });
  }
}
