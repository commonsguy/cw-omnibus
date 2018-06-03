/***
  Copyright (c) 2008-2012 CommonsWare, LLC
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

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Forecaster extends JobIntentService {
  static WeatherResponse LATEST=null;
  static final int COUNT=4;
  private static final double NYC_LATITUDE=40.730610;
  private static final double NYC_LONGITUDE=-73.935242;
  private static final int UNIQUE_JOB_ID=1337;

  static void enqueueWork(Context ctxt) {
    enqueueWork(ctxt, Forecaster.class, UNIQUE_JOB_ID,
      new Intent(ctxt, Forecaster.class));
  }

  @Override
  public void onHandleWork(@NonNull Intent intent) {
    OkHttpClient ok=new OkHttpClient.Builder().build();
    Retrofit retrofit=
      new Retrofit.Builder()
        .client(ok)
        .baseUrl("https://api.weather.gov")
        .addConverterFactory(GsonConverterFactory.create())
        .build();
    NWSInterface nws=retrofit.create(NWSInterface.class);
    double roundedLat=(double)Math.round(NYC_LATITUDE*10000d)/10000d;
    double roundedLon=(double)Math.round(NYC_LONGITUDE*10000d)/10000d;

    try {
      WeatherResponse response=
        nws.getForecast(roundedLat, roundedLon).execute().body();

      for (int i=0;i<COUNT;i++) {
        WeatherResponse.Period period=response.properties.periods.get(i);
        ResponseBody body=ok
          .newCall(new Request.Builder().url(period.icon).build())
          .execute()
          .body();

        period.iconBitmap=BitmapFactory.decodeStream(body.byteStream());
      }

      LATEST=response;
      getContentResolver().notifyChange(WeatherSliceProvider.ME, null);
    }
    catch (Throwable t) {
      Log.e(getClass().getSimpleName(),
        "Exception from Retrofit request to National Weather Service", t);
    }
  }
}
