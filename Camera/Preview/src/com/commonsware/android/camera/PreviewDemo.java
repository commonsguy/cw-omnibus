/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
 */

package com.commonsware.android.camera;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import com.android.camera.PreviewFrameLayout;

public class PreviewDemo extends Activity {
  private PreviewFrameLayout frame=null;
  private SurfaceView preview=null;
  private SurfaceHolder previewHolder=null;
  private Camera camera=null;
  private boolean inPreview=false;
  private boolean cameraConfigured=false;

  @SuppressWarnings("deprecation")
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.main);

    frame=(PreviewFrameLayout)findViewById(R.id.frame);
    preview=(SurfaceView)findViewById(R.id.preview);
    previewHolder=preview.getHolder();
    previewHolder.addCallback(surfaceCallback);
    previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
  }

  @Override
  public void onResume() {
    super.onResume();

    camera=Camera.open();
    startPreview();
  }

  @Override
  public void onPause() {
    if (inPreview) {
      camera.stopPreview();
    }

    camera.release();
    camera=null;
    inPreview=false;

    super.onPause();
  }

  private Camera.Size getBestPreviewSize(int width, int height,
                                         Camera.Parameters parameters) {
    Camera.Size result=null;

    for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
      if (size.width <= width && size.height <= height) {
        if (result == null) {
          result=size;
        }
        else {
          int resultArea=result.width * result.height;
          int newArea=size.width * size.height;

          if (newArea > resultArea) {
            result=size;
          }
        }
      }
    }

    return(result);
  }

  private void initPreview(int width, int height) {
    if (camera != null && previewHolder.getSurface() != null) {
      try {
        camera.setPreviewDisplay(previewHolder);
      }
      catch (Throwable t) {
        Log.e("PreviewDemo-surfaceCallback",
              "Exception in setPreviewDisplay()", t);
        Toast.makeText(PreviewDemo.this, t.getMessage(),
                       Toast.LENGTH_LONG).show();
      }

      if (!cameraConfigured) {
        Camera.Parameters parameters=camera.getParameters();
        Camera.Size size=getBestPreviewSize(width, height, parameters);

        if (size != null) {
          parameters.setPreviewSize(size.width, size.height);
          frame.setAspectRatio((double)size.width / size.height);
          camera.setParameters(parameters);
          cameraConfigured=true;
        }
      }
    }
  }

  private void startPreview() {
    if (cameraConfigured && camera != null) {
      camera.startPreview();
      inPreview=true;
    }
  }

  SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() {
    public void surfaceCreated(SurfaceHolder holder) {
      // no-op -- wait until surfaceChanged()
    }

    public void surfaceChanged(SurfaceHolder holder, int format,
                               int width, int height) {
      initPreview(width, height);
      startPreview();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
      // no-op
    }
  };
}