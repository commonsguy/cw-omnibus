/***
 Copyright (c) 2018 CommonsWare, LLC
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

package com.commonsware.android.slice.sampler;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.DrawableRes;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.SliceProvider;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.ListBuilder.RowBuilder;
import androidx.slice.builders.SliceAction;

public class SamplerSliceProvider extends SliceProvider {
  @Override
  public boolean onCreateSliceProvider() {
    return true;
  }

  @Override
  public Slice onBindSlice(Uri sliceUri) {
    Context ctxt=getContext();

    if (ctxt==null) {
      return null;
    }

    ListBuilder builder=new ListBuilder(ctxt, sliceUri, ListBuilder.INFINITY)
      .setAccentColor(ctxt.getResources().getColor(R.color.colorAccent))
      .addAction(buildIconAction(ctxt, "Top-level Action",
        R.drawable.ic_looks_two_black_24dp));

    builder
      .setHeader(buildHeader(ctxt))
      .addRow(buildSimpleRow(ctxt))
      .addInputRange(buildRangeRow(ctxt));

    return builder.build();
  }

  ListBuilder.HeaderBuilder buildHeader(Context ctxt) {
    return new ListBuilder.HeaderBuilder()
      .setTitle("Header Title")
      .setSubtitle("This is the subtitle")
      .setSummary("This is the summary", false)
      .setPrimaryAction(buildIconAction(ctxt, "Header Primary Action",
        R.drawable.ic_looks_one_black_24dp));
  }

  RowBuilder buildSimpleRow(Context ctxt) {
    return new RowBuilder()
      .setTitle("Simple Row Title")
      .setSubtitle("This is the subtitle")
      .setPrimaryAction(buildIconAction(ctxt, "Simple Row Primary Action",
        R.drawable.ic_looks_4_black_24dp))
      .addEndItem(buildToggleAction(ctxt, "Simple Row End Item", R.id.toggle));
  }

  ListBuilder.InputRangeBuilder buildRangeRow(Context ctxt) {
    return new ListBuilder.InputRangeBuilder()
      .setTitle("Range Title")
      .setSubtitle("This is the subtitle")
      .setMax(10)
      .setValue(5)
      .setPrimaryAction(buildIconAction(ctxt, "Range Primary Action",
        R.drawable.ic_looks_5_black_24dp))
      .setInputAction(buildActionPI(ctxt, "Range Selection Changed", R.id.range));
  }

  SliceAction buildIconAction(Context ctxt, String msg, @DrawableRes int iconRes) {
    return SliceAction.create(buildActionPI(ctxt, msg, iconRes),
      buildIcon(ctxt, iconRes), ListBuilder.ICON_IMAGE, msg);
  }

  SliceAction buildToggleAction(Context ctxt, String msg, int id) {
    return SliceAction.createToggle(buildActionPI(ctxt, msg, id), msg, false);
  }

  PendingIntent buildActionPI(Context ctxt, String msg, int id) {
    Intent i=new Intent(ctxt, SliceActionReceiver.class)
      .putExtra(SliceActionReceiver.EXTRA_MSG, msg);

    return PendingIntent.getBroadcast(ctxt, id, i, PendingIntent.FLAG_UPDATE_CURRENT);
  }

  IconCompat buildIcon(Context ctxt, @DrawableRes int iconRes) {
    return IconCompat.createWithResource(ctxt, iconRes);
  }
}
