/***
 Copyright (c) 2015 CommonsWare, LLC
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

package com.commonsware.android.recyclerview.actionmodelist2;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseBooleanArray;

public class ParcelableSparseBooleanArray extends SparseBooleanArray
    implements Parcelable {
  public static Parcelable.Creator<ParcelableSparseBooleanArray> CREATOR
    =new Parcelable.Creator<ParcelableSparseBooleanArray>() {
    @Override
    public ParcelableSparseBooleanArray createFromParcel(Parcel source) {
      return(new ParcelableSparseBooleanArray(source));
    }

    @Override
    public ParcelableSparseBooleanArray[] newArray(int size) {
      return(new ParcelableSparseBooleanArray[size]);
    }
  };

  public ParcelableSparseBooleanArray() {
    super();
  }

  private ParcelableSparseBooleanArray(Parcel source) {
    int size=source.readInt();

    for (int i=0; i < size; i++) {
      put(source.readInt(), (Boolean)source.readValue(null));
    }
  }

  @Override
  public int describeContents() {
    return(0);
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(size());

    for (int i=0;i<size();i++) {
      dest.writeInt(keyAt(i));
      dest.writeValue(valueAt(i));
    }
  }
}
