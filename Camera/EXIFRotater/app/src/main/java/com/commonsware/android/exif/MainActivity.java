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

package com.commonsware.android.exif;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.io.File;
import java.io.InputStream;
import it.sephiroth.android.library.exif2.ExifInterface;
import it.sephiroth.android.library.exif2.ExifTag;

public class MainActivity extends Activity {
  private static final String ASSET_NAME="Landscape_8.jpg";
  private ImageView original;
  private ImageView oriented;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    original=(ImageView)findViewById(R.id.original_image);
    oriented=(ImageView)findViewById(R.id.oriented_image);

    new ImageLoadThread(this).start();
  }

  @Override
  protected void onStart() {
    super.onStart();

    EventBus.getDefault().register(this);
  }

  @Override
  protected void onStop() {
    EventBus.getDefault().unregister(this);

    super.onStop();
  }

  @Subscribe(sticky=true, threadMode=ThreadMode.MAIN)
  public void onImageLoaded(ImageLoadedEvent event) {
    original.setImageBitmap(event.original);

    if (BuildConfig.ROTATE_BITMAP) {
      oriented.setImageBitmap(event.rotated);
    }
    else {
      oriented.setImageBitmap(event.original);
      oriented.setRotation(degreesForRotation(event.orientation));
    }
  }

  private static class ImageLoadThread extends Thread {
    private final Context ctxt;

    ImageLoadThread(Context ctxt) {
      this.ctxt=ctxt.getApplicationContext();
    }

    @Override
    public void run() {
      AssetManager assets=ctxt.getAssets();

      try {
        InputStream is=assets.open(ASSET_NAME);
        ExifInterface exif=new ExifInterface();

        exif.readExif(is, ExifInterface.Options.OPTION_ALL);

        ExifTag tag=exif.getTag(ExifInterface.TAG_ORIENTATION);
        int orientation=(tag==null ? -1 : tag.getValueAsInt(-1));

        if (orientation==8 || orientation==3 || orientation==6) {
          is=assets.open(ASSET_NAME);

          Bitmap original=BitmapFactory.decodeStream(is);
          Bitmap rotated=null;

          if (BuildConfig.ROTATE_BITMAP) {
            rotated=rotateViaMatrix(original, orientation);

            exif.setTagValue(ExifInterface.TAG_ORIENTATION, 1);
            exif.removeCompressedThumbnail();

            File output=
              new File(ctxt.getExternalFilesDir(null), "rotated.jpg");

            exif.writeExif(rotated, output.getAbsolutePath(), 100);

            MediaScannerConnection.scanFile(ctxt,
              new String[]{output.getAbsolutePath()}, null, null);
          }

          EventBus
            .getDefault()
            .postSticky(new ImageLoadedEvent(original, rotated, orientation));
        }
      }
      catch (Exception e) {
        Log.e(getClass().getSimpleName(), "Exception processing image", e);
      }
    }
  }

  static private Bitmap rotateViaMatrix(Bitmap original, int orientation) {
    Matrix matrix=new Matrix();

    matrix.setRotate(degreesForRotation(orientation));

    return(Bitmap.createBitmap(original, 0, 0, original.getWidth(),
      original.getHeight(), matrix, true));
  }

  static private int degreesForRotation(int orientation) {
    int result;

    switch (orientation) {
      case 8:
        result=270;
        break;

      case 3:
        result=180;
        break;

      default:
        result=90;
    }

    return(result);
  }

  private static class ImageLoadedEvent {
    final Bitmap original;
    final Bitmap rotated;
    final int orientation;

    ImageLoadedEvent(Bitmap original, Bitmap rotated, int orientation) {
      this.original=original;
      this.rotated=rotated;
      this.orientation=orientation;
    }
  }
}
