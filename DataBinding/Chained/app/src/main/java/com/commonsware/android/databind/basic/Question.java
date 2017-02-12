/***
  Copyright (c) 2013-2015 CommonsWare, LLC
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

package com.commonsware.android.databind.basic;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

public class Question {
  public final ObservableField<String> title=
    new ObservableField<String>();
  public final Owner owner;
  public final String link;
  public final ObservableInt score=new ObservableInt();
  public final String id;
  public ObservableBoolean expanded=new ObservableBoolean(true);

  Question(Item item) {
    updateFromItem(item);
    owner=item.owner;
    link=item.link;
    id=item.id;
  }

  void updateFromItem(Item item) {
    title.set(item.title);
    score.set(item.score);
  }
}
