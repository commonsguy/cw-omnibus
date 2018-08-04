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

package com.commonsware.android.slice.weather;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;
import androidx.annotation.DrawableRes;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.SliceProvider;
import androidx.slice.builders.GridRowBuilder;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import static androidx.slice.builders.ListBuilder.LARGE_IMAGE;

public class WeatherSliceProvider extends SliceProvider {
  static final Uri NYC_FORECAST=
    Uri.parse(
      "https://forecast.weather.gov/MapClick.php?lat=40.7146&lon=-74.0071");
  static final DateTimeFormatter FORMATTER=
    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
  static final Uri ME=new Uri.Builder()
    .scheme("content")
    .authority(BuildConfig.APPLICATION_ID+".provider")
    .build();

  @Override
  public boolean onCreateSliceProvider() {
    return true;
  }

  public Slice onBindSlice(Uri sliceUri) {
    Context ctxt=getContext();

    if (ctxt==null) {
      return null;
    }

    ListBuilder builder=new ListBuilder(ctxt, sliceUri, ListBuilder.INFINITY)
      .setAccentColor(ctxt.getResources().getColor(R.color.colorAccent));

    builder.setHeader(buildHeader(ctxt));

    WeatherResponse weather=Forecaster.LATEST;

    if (weather==null) {
      Forecaster.enqueueWork(getContext());

      builder.addRange(new ListBuilder.RangeBuilder()
        .setTitle(ctxt.getString(R.string.downloading)));
    }
    else {
      builder.addGridRow(buildGridRow());
    }

    return builder.build();
  }

  ListBuilder.HeaderBuilder buildHeader(Context ctxt) {
    return new ListBuilder.HeaderBuilder()
      .setTitle(ctxt.getString(R.string.header_title))
      .setPrimaryAction(
        buildIconAction(ctxt, ctxt.getString(R.string.msg_forecast),
          R.drawable.ic_wb_sunny_black_24dp));
  }

  GridRowBuilder buildGridRow() {
    GridRowBuilder row=new GridRowBuilder();

    for (int i=0; i<Forecaster.COUNT; i++) {
      WeatherResponse.Period period=Forecaster.LATEST.properties.periods.get(i);
      OffsetDateTime odt=OffsetDateTime.parse(period.startTime);

      row.addCell(new GridRowBuilder.CellBuilder()
        .addTitleText(odt.format(FORMATTER))
        .addImage(IconCompat.createWithBitmap(period.iconBitmap), LARGE_IMAGE)
        .addText(
          String.format("%d%s", period.temperature, period.temperatureUnit)));
    }

    return row;
  }

  SliceAction buildIconAction(Context ctxt, String msg,
                              @DrawableRes int iconRes) {
    return SliceAction.create(buildActionPI(ctxt, iconRes),
      buildIcon(ctxt, iconRes), ListBuilder.ICON_IMAGE, msg);
  }

  PendingIntent buildActionPI(Context ctxt, int id) {
    Intent i=new Intent(Intent.ACTION_VIEW, NYC_FORECAST);

    return PendingIntent.getActivity(ctxt, id, i,
      PendingIntent.FLAG_UPDATE_CURRENT);
  }

  IconCompat buildIcon(Context ctxt, @DrawableRes int iconRes) {
    return IconCompat.createWithResource(ctxt, iconRes);
  }
}
