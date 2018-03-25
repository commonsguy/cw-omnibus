/***
 Copyright (c) 2008-2018 CommonsWare, LLC
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

package com.commonsware.android.imagedecoder;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ImageViewDemo extends Activity {
  private static final String ASSET_PATH="FreedomTower-Morning.jpg";
  private ImageView photo;

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);
    setContentView(R.layout.main);

    photo=findViewById(R.id.photo);

    Observable.just(getAssets())
      .subscribeOn(Schedulers.io())
      .map((assets) -> assets.open(ASSET_PATH))
      .map(this::copyToCache)
      .map((cache) -> ImageDecoder.decodeBitmap(ImageDecoder.createSource(cache)))
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(
        (bitmap) -> photo.setImageBitmap(bitmap),
        (t) -> {
          Toast.makeText(this, t.getMessage(), Toast.LENGTH_LONG).show();
          Log.e(getClass().getSimpleName(), "Exception loading photo", t);
        });
  }

  private File copyToCache(InputStream in) throws IOException {
    File result=new File(getCacheDir(), "photo.jpg");
    FileOutputStream out=new FileOutputStream(result);
    byte[] buf=new byte[8192];
    int len;

    while ((len=in.read(buf)) > 0) {
      out.write(buf, 0, len);
    }

    in.close();
    out.close();

    return result;
  }
}
