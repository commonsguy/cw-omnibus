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

package com.commonsware.android.dragdrop;

import android.os.Build;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewParent;
import java.util.ArrayList;

public class DropTarget implements View.OnDragListener {
  private ArrayList<View> views=new ArrayList<>();
  private View.OnDragListener listener;

  public DropTarget on(View... views) {
    for (View v : views) {
      this.views.add(v);
      v.setOnDragListener(this);
    }

    return(this);
  }

  public void to(View.OnDragListener listener) {
    this.listener=listener;
  }

  @Override
  public boolean onDrag(View view, DragEvent dragEvent) {
    if (Build.VERSION.SDK_INT<Build.VERSION_CODES.N) {
      return(listener.onDrag(view, dragEvent));
    }

    boolean result=listener.onDrag(view, dragEvent);
    ViewParent parent=view.getParent();

    while (parent!=null && parent instanceof View) {
      View parentView=(View)parent;

      if (views.contains(parentView)) {
        listener.onDrag(parentView, dragEvent);
      }

      parent=parentView.getParent();
    }

    return(result);
  }
}
