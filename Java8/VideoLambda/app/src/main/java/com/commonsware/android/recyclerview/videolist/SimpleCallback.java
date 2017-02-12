/***
 Copyright (c) 2016 CommonsWare, LLC
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

package com.commonsware.android.recyclerview.videolist;

import android.support.v7.util.DiffUtil;
import java.util.ArrayList;

class SimpleCallback<T extends Comparable> extends DiffUtil.Callback {
  private final ArrayList<T> oldItems;
  private final ArrayList<T> newItems;

  public SimpleCallback(ArrayList<T> oldItems,
                        ArrayList<T> newItems) {
    this.oldItems=oldItems;
    this.newItems=newItems;
  }

  @Override
  public int getOldListSize() {
    return(oldItems.size());
  }

  @Override
  public int getNewListSize() {
    return(newItems.size());
  }

  @Override
  public boolean areItemsTheSame(int oldItemPosition,
                                 int newItemPosition) {
    return(oldItems.get(oldItemPosition)
      .equals(newItems.get(newItemPosition)));
  }

  @Override
  public boolean areContentsTheSame(int oldItemPosition,
                                    int newItemPosition) {
    return(oldItems.get(oldItemPosition)
      .compareTo(newItems.get(newItemPosition))==0);
  }
}
