/***
 Copyright (c) 2015 CommonsWare, LLC
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

package com.commonsware.android.andcorder;

import android.content.Context;
import android.content.res.Configuration;
import android.media.CamcorderProfile;
import android.util.DisplayMetrics;
import android.view.WindowManager;

class RecordingConfig {
  private static final int[] CAMCORDER_PROFILES={
    CamcorderProfile.QUALITY_2160P,
    CamcorderProfile.QUALITY_1080P,
    CamcorderProfile.QUALITY_720P,
    CamcorderProfile.QUALITY_480P,
    CamcorderProfile.QUALITY_CIF,
    CamcorderProfile.QUALITY_QVGA,
    CamcorderProfile.QUALITY_QCIF
  };

  final int width;
  final int height;
  final int frameRate;
  final int bitRate;
  final int density;

  RecordingConfig(Context ctxt) {
    DisplayMetrics metrics=new DisplayMetrics();
    WindowManager wm=(WindowManager)ctxt.getSystemService(Context.WINDOW_SERVICE);

    wm.getDefaultDisplay().getRealMetrics(metrics);

    density=metrics.densityDpi;

    Configuration cfg=ctxt.getResources().getConfiguration();

    boolean isLandscape=
      (cfg.orientation==Configuration.ORIENTATION_LANDSCAPE);

    CamcorderProfile selectedProfile=null;

    for (int profileId : CAMCORDER_PROFILES) {
      CamcorderProfile profile=null;

      try {
        profile=CamcorderProfile.get(profileId);
      }
      catch (Exception e) {
        // not documented to throw anything, but does
      }

      if (profile!=null) {
        if (selectedProfile==null) {
          selectedProfile=profile;
        }
        else if (profile.videoFrameWidth>=metrics.widthPixels &&
          profile.videoFrameHeight>=metrics.heightPixels) {
          selectedProfile=profile;
        }
      }
    }

    if (selectedProfile==null) {
      throw new IllegalStateException("No CamcorderProfile available!");
    }
    else {
      frameRate=selectedProfile.videoFrameRate;
      bitRate=selectedProfile.videoBitRate;

      int targetWidth, targetHeight;

      if (isLandscape) {
        targetWidth=selectedProfile.videoFrameWidth;
        targetHeight=selectedProfile.videoFrameHeight;
      }
      else {
        targetWidth=selectedProfile.videoFrameHeight;
        targetHeight=selectedProfile.videoFrameWidth;
      }

      if (targetWidth>=metrics.widthPixels &&
        targetHeight>=metrics.heightPixels) {
        width=metrics.widthPixels;
        height=metrics.heightPixels;
      }
      else {
        if (isLandscape) {
          width=targetHeight*metrics.widthPixels/metrics.heightPixels;
          height=targetHeight;
        }
        else {
          width=targetWidth;
          height=targetWidth*metrics.heightPixels/metrics.widthPixels;
        }
      }
    }
  }
}
