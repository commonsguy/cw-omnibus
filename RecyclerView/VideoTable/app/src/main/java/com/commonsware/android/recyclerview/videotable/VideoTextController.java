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

package com.commonsware.android.recyclerview.videotable;

import android.database.Cursor;
import android.view.View;
import android.widget.TextView;

class VideoTextController extends BaseVideoController {
  private TextView label=null;
  private int cursorColumn;

  VideoTextController(View cell, int labelId, int cursorColumn) {
    super(cell);
    this.cursorColumn=cursorColumn;

    label=(TextView)cell.findViewById(labelId);
  }

  @Override
  void bindModel(Cursor row) {
    super.bindModel(row);

    label.setText(row.getString(cursorColumn));
  }
}
