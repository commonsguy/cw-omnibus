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

package com.commonsware.android.picture;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import com.android.camera.PreviewFrameLayout;

public class PictureDemo extends Activity {
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

  @TargetApi(Build.VERSION_CODES.GINGERBREAD)
  @Override
  public void onResume() {
    super.onResume();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
      Camera.CameraInfo info=new Camera.CameraInfo();

      for (int i=0; i < Camera.getNumberOfCameras(); i++) {
        Camera.getCameraInfo(i, info);

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
          camera=Camera.open(i);
        }
      }
    }

    if (camera == null) {
      camera=Camera.open();
    }

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

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    new MenuInflater(this).inflate(R.menu.options, menu);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.camera) {
      if (inPreview) {
        camera.takePicture(null, null, photoCallback);
        inPreview=false;
      }
    }

    return(super.onOptionsItemSelected(item));
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

  private Camera.Size getSmallestPictureSize(Camera.Parameters parameters) {
    Camera.Size result=null;

    for (Camera.Size size : parameters.getSupportedPictureSizes()) {
      if (result == null) {
        result=size;
      }
      else {
        int resultArea=result.width * result.height;
        int newArea=size.width * size.height;

        if (newArea < resultArea) {
          result=size;
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
        Toast.makeText(PictureDemo.this, t.getMessage(),
                       Toast.LENGTH_LONG).show();
      }

      if (!cameraConfigured) {
        Camera.Parameters parameters=camera.getParameters();
        Camera.Size size=getBestPreviewSize(width, height, parameters);
        Camera.Size pictureSize=getSmallestPictureSize(parameters);

        if (size != null && pictureSize != null) {
          parameters.setPreviewSize(size.width, size.height);
          parameters.setPictureSize(pictureSize.width,
                                    pictureSize.height);
          parameters.setPictureFormat(ImageFormat.JPEG);
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

  Camera.PictureCallback photoCallback=new Camera.PictureCallback() {
    public void onPictureTaken(byte[] data, Camera camera) {
      new SavePhotoTask().execute(data);
      camera.startPreview();
      inPreview=true;
    }
  };

  class SavePhotoTask extends AsyncTask<byte[], String, String> {
    @Override
    protected String doInBackground(byte[]... jpeg) {
      File photo=
          new File(Environment.getExternalStorageDirectory(),
                   "photo.jpg");

      if (photo.exists()) {
        photo.delete();
      }

      try {
        FileOutputStream fos=new FileOutputStream(photo.getPath());

        fos.write(jpeg[0]);
        fos.close();
      }
      catch (java.io.IOException e) {
        Log.e("PictureDemo", "Exception in photoCallback", e);
      }

      return(null);
    }
  }
}