/***
 Copyright (c) 2013-2014 CommonsWare, LLC
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

import android.annotation.TargetApi;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import com.commonsware.android.databind.basic.databinding.RowBinding;
import org.greenrobot.eventbus.EventBus;

public class QuestionController extends RecyclerView.ViewHolder
  implements View.OnTouchListener {
  private final RowBinding rowBinding;
  private final QuestionsFragment.QuestionsAdapter adapter;

  public QuestionController(RowBinding rowBinding,
                            QuestionsFragment.QuestionsAdapter adapter) {
    super(rowBinding.getRoot());

    this.rowBinding=rowBinding;
    this.adapter=adapter;
  }

  public void showQuestion(Question question) {
    EventBus.getDefault().post(new QuestionClickedEvent(question));
  }

  @Override
  public boolean onTouch(View v, MotionEvent event){
    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
      v
        .findViewById(R.id.row_content)
        .getBackground()
        .setHotspot(event.getX(),event.getY());
    }

    return(false);
  }

  void bindModel(Question question) {
    rowBinding.setQuestion(question);
    rowBinding.setController(this);
    rowBinding.executePendingBindings();
  }
}
