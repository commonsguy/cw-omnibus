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

package com.commonsware.android.rvp;

import android.os.Parcel;
import android.os.Parcelable;

class EditBuffer implements Parcelable {
  private String prose;
  final private String title;

  EditBuffer(String title) {
    this(title, "");
  }

  EditBuffer(String title, String prose) {
    this.prose=prose;
    this.title=title;
  }

  protected EditBuffer(Parcel in) {
    prose=in.readString();
    title=in.readString();
  }

  @Override
  public String toString() {
    return(title);
  }

  String getProse() {
    return(prose);
  }

  void setProse(String prose) {
    this.prose=prose;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(prose);
    dest.writeString(title);
  }

  @SuppressWarnings("unused")
  public static final Parcelable.Creator<EditBuffer> CREATOR=
    new Parcelable.Creator<EditBuffer>() {
    @Override
    public EditBuffer createFromParcel(Parcel in) {
      return(new EditBuffer(in));
    }

    @Override
    public EditBuffer[] newArray(int size) {
      return(new EditBuffer[size]);
    }
  };
}
