/***
 Copyright (c) 2015 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 From _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.debug.videolist;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public class RVAdapterWrapper<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
  private final RecyclerView.Adapter<T> wrapped;

  public RVAdapterWrapper(RecyclerView.Adapter<T> wrapped) {
    super();

    this.wrapped=wrapped;
  }

  public RecyclerView.Adapter<T> getWrappedAdapter() {
    return(wrapped);
  }

  @Override
  public T onCreateViewHolder(final ViewGroup parent, final int viewType) {
   return(wrapped.onCreateViewHolder(parent, viewType));
  }

  @Override
  public void onBindViewHolder(final T holder, final int position) {
    wrapped.onBindViewHolder(holder, position);
  }

  @Override
  public long getItemId(int position) {
    return(wrapped.getItemId(position));
  }

  @Override
  public int getItemViewType(int position) {
    return(wrapped.getItemViewType(position));
  }

  @Override
  public void onAttachedToRecyclerView(RecyclerView recyclerView) {
    wrapped.onAttachedToRecyclerView(recyclerView);
  }

  @Override
  public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
    wrapped.onDetachedFromRecyclerView(recyclerView);
  }

  @Override
  public void onViewAttachedToWindow(T holder) {
    wrapped.onViewAttachedToWindow(holder);
  }

  @Override
  public void onViewDetachedFromWindow(T holder) {
    wrapped.onViewDetachedFromWindow(holder);
  }

  @Override
  public void onViewRecycled(T holder) {
    wrapped.onViewRecycled(holder);
  }

  @Override
  public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
    wrapped.registerAdapterDataObserver(observer);
  }

  @Override
  public void setHasStableIds(boolean hasStableIds) {
    wrapped.setHasStableIds(hasStableIds);
  }

  @Override
  public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
    wrapped.unregisterAdapterDataObserver(observer);
  }

  @Override
  public int getItemCount() {
    return(wrapped.getItemCount());
  }
}
