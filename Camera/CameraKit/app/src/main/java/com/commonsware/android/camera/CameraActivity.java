/***
 Copyright (c) 2017 CommonsWare, LLC
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

package com.commonsware.android.camera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.github.clans.fab.FloatingActionButton;
import com.wonderkiln.camerakit.CameraKitEventListenerAdapter;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

public class CameraActivity extends Activity {
  private static final String EXTRA_IS_PHOTO="isPhoto";
  private CameraView camera;
  private FloatingActionButton fab;
  private boolean isPhoto=true;
  private boolean isRecording=false;

  public static void takePhoto(Activity requester, int requestCode) {
    Intent i=new Intent(requester, CameraActivity.class)
      .putExtra(EXTRA_IS_PHOTO, true);

    requester.startActivityForResult(i, requestCode);
  }

  public static void recordVideo(Activity requester, int requestCode) {
    Intent i=new Intent(requester, CameraActivity.class)
      .putExtra(EXTRA_IS_PHOTO, false);

    requester.startActivityForResult(i, requestCode);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.camera);

    isPhoto=getIntent().getBooleanExtra(EXTRA_IS_PHOTO, true);
    camera=findViewById(R.id.camera);
    fab=findViewById(R.id.fab);

    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (isPhoto) {
          takePhoto();
        }
        else {
          recordVideo();
        }
      }
    });

    if (!isPhoto) {
      fab.setImageResource(R.drawable.ic_videocam_black_24dp);
    }

    camera.addCameraKitListener(new CameraKitEventListenerAdapter() {
      @Override
      public void onImage(CameraKitImage image) {
        // TODO: do something with picture

        setResult(RESULT_OK);
        finish();
      }

      @Override
      public void onVideo(CameraKitVideo video) {
        // TODO: do something with video file

        setResult(RESULT_OK);
        finish();
      }
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    camera.start();
  }

  @Override
  protected void onPause() {
    camera.stop();
    super.onPause();
  }

  private void takePhoto() {
    camera.captureImage();
    fab.setEnabled(false);
  }

  private void recordVideo() {
    if (isRecording) {
      camera.stopVideo();
      finish();
    }
    else {
      fab.setColorNormalResId(R.color.recording);
      fab.setImageResource(R.drawable.ic_stop_black_24dp);
      isRecording=true;
      camera.start();
    }
  }
}
