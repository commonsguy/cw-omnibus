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

public class Thingy implements Parcelable {
  final String something;
  final int anotherThing;

  public Thingy(String something, int anotherThing) {
    this.something=something;
    this.anotherThing=anotherThing;
  }

  protected Thingy(Parcel in) {
    something=in.readString();
    anotherThing=in.readInt();
  }

  @Override
  public int describeContents() {
    return(0);
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(something);
    dest.writeInt(anotherThing);
  }

  @SuppressWarnings("unused")
  public static final Parcelable.Creator<Thingy> CREATOR=
    new Parcelable.Creator<Thingy>() {
    @Override
    public Thingy createFromParcel(Parcel in) {
      return(new Thingy(in));
    }

    @Override
    public Thingy[] newArray(int size) {
      return(new Thingy[size]);
    }
  };
}