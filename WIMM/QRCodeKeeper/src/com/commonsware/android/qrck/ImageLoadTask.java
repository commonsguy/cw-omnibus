/***
  Copyright (c) 2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Advanced Android Development_
    http://commonsware.com/AdvAndroid
*/

package com.commonsware.android.qrck;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoadTask extends AsyncTask<String, Void, Bitmap> {
  private ImageView image=null;
  private Exception ex=null;
  private String path=null;
  
  public ImageLoadTask(ImageView image) {
    this.image=image;
  }
  
  /** 
   * Runs on a worker thread, loading in our data.  
   */
  @Override
  public Bitmap doInBackground(String... paths) {
    Bitmap result=null;
    
    path=paths[0];
    
    try {
      image.setTag(path);
      result=BitmapFactory.decodeFile(path);
    }
    catch (Exception ex) {
      this.ex=ex;
    }
    
    return(result);
  }

  @Override
  protected void onPostExecute(Bitmap bitmap) {
      if (path.equals(image.getTag())) {
        image.setImageBitmap(bitmap);
        image.invalidate();
      }
      
      if (ex!=null) {
        Log.e("ImageLoadTask", "Exception loading image", ex);
      }
  }
}