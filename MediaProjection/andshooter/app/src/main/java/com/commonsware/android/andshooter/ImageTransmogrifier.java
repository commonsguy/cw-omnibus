/***
 Copyright (c) 2015 CommonsWare, LLC
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

package com.commonsware.android.andshooter;

import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.Image;
import android.media.ImageReader;
import android.view.Display;
import android.view.Surface;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class ImageTransmogrifier implements ImageReader.OnImageAvailableListener {
  private final int width;
  private final int height;
  private final ImageReader imageReader;
  private final ScreenshotService svc;
  private Bitmap latestBitmap=null;

  ImageTransmogrifier(ScreenshotService svc) {
    this.svc=svc;

    Display display=svc.getWindowManager().getDefaultDisplay();
    Point size=new Point();

    display.getRealSize(size);

    int width=size.x;
    int height=size.y;

    while (width*height > (2<<19)) {
      width=width>>1;
      height=height>>1;
    }

    this.width=width;
    this.height=height;

    imageReader=ImageReader.newInstance(width, height,
        PixelFormat.RGBA_8888, 2);
    imageReader.setOnImageAvailableListener(this, svc.getHandler());
  }

  @Override
  public void onImageAvailable(ImageReader reader) {
    final Image image=imageReader.acquireLatestImage();

    if (image!=null) {
      Image.Plane[] planes=image.getPlanes();
      ByteBuffer buffer=planes[0].getBuffer();
      int pixelStride=planes[0].getPixelStride();
      int rowStride=planes[0].getRowStride();
      int rowPadding=rowStride - pixelStride * width;
      int bitmapWidth=width + rowPadding / pixelStride;

      if (latestBitmap == null ||
          latestBitmap.getWidth() != bitmapWidth ||
          latestBitmap.getHeight() != height) {
        if (latestBitmap != null) {
          latestBitmap.recycle();
        }

        latestBitmap=Bitmap.createBitmap(bitmapWidth,
            height, Bitmap.Config.ARGB_8888);
      }

      latestBitmap.copyPixelsFromBuffer(buffer);
      image.close();

      ByteArrayOutputStream baos=new ByteArrayOutputStream();
      Bitmap cropped=Bitmap.createBitmap(latestBitmap, 0, 0,
        width, height);

      cropped.compress(Bitmap.CompressFormat.PNG, 100, baos);

      byte[] newPng=baos.toByteArray();

      svc.processImage(newPng);
    }
  }

  Surface getSurface() {
    return(imageReader.getSurface());
  }

  int getWidth() {
    return(width);
  }

  int getHeight() {
    return(height);
  }

  void close() {
    imageReader.close();
  }
}
