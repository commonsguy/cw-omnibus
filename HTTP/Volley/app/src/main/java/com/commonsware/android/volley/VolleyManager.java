/***
 Copyright (c) 2013-2016 CommonsWare, LLC
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

package com.commonsware.android.volley;

import android.content.Context;
import android.widget.ImageView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class VolleyManager {
  private static volatile VolleyManager INSTANCE;
  private final RequestQueue queue;
  private final ImageLoader imageLoader;

  synchronized static VolleyManager get(Context ctxt) {
    if (INSTANCE==null) {
      INSTANCE=new VolleyManager(ctxt.getApplicationContext());
    }

    return(INSTANCE);
  }

  private VolleyManager(Context ctxt) {
    queue=Volley.newRequestQueue(ctxt);
    imageLoader=new ImageLoader(queue, new LruBitmapCache(ctxt));
  }

  void enqueue(Request<?> request) {
    queue.add(request);
  }

  void loadImage(String url, ImageView iv,
                 int placeholderDrawable, int errorDrawable) {
    imageLoader.get(url,
      ImageLoader.getImageListener(iv, placeholderDrawable,
        errorDrawable));
  }
}
