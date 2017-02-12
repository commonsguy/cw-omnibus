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

package com.commonsware.android.pdfrenderer;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

class PageController extends RecyclerView.ViewHolder {
  private final SubsamplingScaleImageView iv;
  private Bitmap bitmap;

  PageController(View itemView) {
    super(itemView);

    iv=(SubsamplingScaleImageView)itemView.findViewById(R.id.page);
  }

  void setPage(PdfRenderer.Page page) {
    if (bitmap==null) {
      int height=2000;
      int width=height * page.getWidth() / page.getHeight();

      bitmap=Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }

    bitmap.eraseColor(0xFFFFFFFF);
    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
    iv.resetScaleAndCenter();
    iv.setImage(ImageSource.cachedBitmap(bitmap));
  }
}
