/***
 Copyright (c) 2016 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.freecar;

import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.preference.PreferenceManager;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.widget.Toast;
import java.lang.reflect.Field;

public class FreecarTileService extends TileService {
  static final String PREF_TO_LAUNCH="toLaunch";

  @Override
  public void onClick() {
    super.onClick();

    SharedPreferences prefs=
      PreferenceManager.getDefaultSharedPreferences(this);
    String cnFlat=prefs.getString(PREF_TO_LAUNCH, null);

    if (cnFlat!=null) {
      ComponentName cn=ComponentName.unflattenFromString(cnFlat);

      try {
        ActivityInfo info=getPackageManager().getActivityInfo(cn, 0);
        ActivityInfo.WindowLayout layout=info.windowLayout;
        Intent i=
          new Intent()
            .setComponent(cn)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Point size=new Point();

        getSystemService(DisplayManager.class)
          .getDisplay(Display.DEFAULT_DISPLAY)
          .getSize(size);

        if (layout==null) {
          size.x=size.x/2;
          size.y=size.y/2;
        }
        else {
          if (layout.widthFraction>0.0f) {
            size.x=
              Math.max(layout.minWidth,
                (int)(size.x*layout.widthFraction));
          }
          else {
            size.x=layout.width;
          }

          if (layout.heightFraction>0.0f) {
            size.y=
              Math.max(layout.minHeight,
                (int)(size.y*layout.heightFraction));
          }
          else {
            size.y=layout.height;
          }
        }

        ActivityOptions opts=
          ActivityOptions
            .makeBasic()
            .setLaunchBounds(new Rect(0, 0, size.x, size.y));

        startActivity(i, opts.toBundle());
      }
      catch (Exception e) {
        Log.e(getClass().getSimpleName(),
          "Exception trying to launch activity", e);

        toast(R.string.msg_sorry);
      }
    }
    else {
      toast(R.string.msg_choose);
    }
  }

  private void toast(int msg) {
    Toast t=Toast.makeText(this, msg, Toast.LENGTH_LONG);

    t.setGravity(Gravity.END | Gravity.BOTTOM, 32, 32);
    t.show();
  }
}
