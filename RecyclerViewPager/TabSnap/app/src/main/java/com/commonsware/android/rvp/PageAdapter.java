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

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import java.util.ArrayList;

class PageAdapter extends RecyclerView.Adapter<PageController> {
  private static final String STATE_BUFFERS="buffers";
  private static final int PAGE_COUNT=10;
  private final RecyclerView pager;
  private final LayoutInflater inflater;
  private ArrayList<String> buffers=new ArrayList<>();

  PageAdapter(RecyclerView pager, LayoutInflater inflater) {
    this.pager=pager;
    this.inflater=inflater;

    for (int i=0;i<10;i++) {
      buffers.add("");
    }
  }

  @Override
  public PageController onCreateViewHolder(ViewGroup parent, int viewType) {
    return(new PageController(inflater.inflate(R.layout.editor, parent, false)));
  }

  @Override
  public void onBindViewHolder(PageController holder, int position) {
    holder.setText(buffers.get(position));
  }

  @Override
  public int getItemCount() {
    return(PAGE_COUNT);
  }

  String getTabText(Context ctxt, int position) {
    return(PageController.getTitle(ctxt, position));
  }

  @Override
  public void onViewDetachedFromWindow(PageController holder) {
    super.onViewDetachedFromWindow(holder);

    buffers.set(holder.getAdapterPosition(), holder.getText());
  }

  void onSaveInstanceState(Bundle state) {
    for (int i=0;i<PAGE_COUNT;i++) {
      PageController holder=
        (PageController)pager.findViewHolderForAdapterPosition(i);

      if (holder!=null) {
        buffers.set(i, holder.getText());
      }
    }

    state.putStringArrayList(STATE_BUFFERS, buffers);
  }

  void onRestoreInstanceState(Bundle state) {
    buffers=state.getStringArrayList(STATE_BUFFERS);
  }
}
