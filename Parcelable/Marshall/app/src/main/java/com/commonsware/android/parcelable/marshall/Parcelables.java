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

package com.commonsware.android.parcelable.marshall;

import android.os.Parcel;
import android.os.Parcelable;

// inspired by http://stackoverflow.com/a/18000094/115145

public class Parcelables {
  public static byte[] toByteArray(Parcelable parcelable) {
    Parcel parcel=Parcel.obtain();

    parcelable.writeToParcel(parcel, 0);

    byte[] result=parcel.marshall();

    parcel.recycle();

    return(result);
  }

  public static <T> T toParcelable(byte[] bytes,
                                   Parcelable.Creator<T> creator) {
    Parcel parcel=Parcel.obtain();

    parcel.unmarshall(bytes, 0, bytes.length);
    parcel.setDataPosition(0);

    T result=creator.createFromParcel(parcel);

    parcel.recycle();

    return(result);
  }
}
