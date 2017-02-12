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

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import java.util.ArrayList;

class PageAdapter extends RecyclerView.Adapter<PageController> {
  private static final String STATE_BUFFERS="buffers";
  private final RecyclerView pager;
  private final LayoutInflater inflater;
  private ArrayList<EditBuffer> buffers=new ArrayList<>();
  private int nextTabNumber=1;

  PageAdapter(RecyclerView pager, LayoutInflater inflater) {
    this.pager=pager;
    this.inflater=inflater;

    for (int i=0;i<10;i++) {
      buffers.add(new EditBuffer(getNextTitle()));
    }
  }

  @Override
  public PageController onCreateViewHolder(ViewGroup parent, int viewType) {
    return(new PageController(inflater.inflate(R.layout.editor, parent, false)));
  }

  @Override
  public void onBindViewHolder(PageController holder, int position) {
    holder.setText(buffers.get(position).getProse());
    holder.setTitle(buffers.get(position).toString());
  }

  @Override
  public int getItemCount() {
    return(buffers.size());
  }

  String getTabText(int position) {
    return(buffers.get(position).toString());
  }

  @Override
  public void onViewDetachedFromWindow(PageController holder) {
    super.onViewDetachedFromWindow(holder);

    if (holder.getAdapterPosition()>=0) {
      buffers.get(holder.getAdapterPosition()).setProse(holder.getText());
    }
  }

  private String getNextTitle() {
    return(pager.getContext().getString(R.string.hint, nextTabNumber++));
  }

  void onSaveInstanceState(Bundle state) {
    for (int i=0;i<getItemCount();i++) {
      updateProse(i);
    }

    state.putParcelableArrayList(STATE_BUFFERS, buffers);
  }

  void onRestoreInstanceState(Bundle state) {
    buffers=state.getParcelableArrayList(STATE_BUFFERS);
  }

  void insert(int position) {
    buffers.add(position, new EditBuffer(getNextTitle()));
    notifyItemInserted(position);
  }

  void clone(int position) {
    updateProse(position);

    EditBuffer newBuffer=new EditBuffer(getNextTitle(),
      buffers.get(position).getProse());

    buffers.add(position+1, newBuffer);
    notifyItemInserted(position+1);
  }

  void swap(int first, int second) {
    EditBuffer firstBuffer=buffers.get(first);
    EditBuffer secondBuffer=buffers.get(second);

    buffers.set(first, secondBuffer);
    buffers.set(second, firstBuffer);

    PageController holder=
      (PageController)pager.findViewHolderForAdapterPosition(first);

    if (holder!=null) {
      holder.setText(secondBuffer.getProse());
    }

    holder=(PageController)pager.findViewHolderForAdapterPosition(second);

    if (holder!=null) {
      holder.setText(firstBuffer.getProse());
    }

    notifyItemChanged(first);
    notifyItemChanged(second);
  }

  void remove(int position) {
    buffers.remove(position);
    notifyItemRemoved(position);
  }

  private void updateProse(int position) {
    PageController holder=
      (PageController)pager.findViewHolderForAdapterPosition(position);

    if (holder!=null) {
      buffers.get(position).setProse(holder.getText());
    }
  }
}
