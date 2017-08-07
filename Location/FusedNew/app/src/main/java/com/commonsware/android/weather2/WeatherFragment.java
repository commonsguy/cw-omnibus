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

package com.commonsware.android.weather2;

import android.annotation.SuppressLint;
import android.app.ListFragment;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherFragment extends ListFragment implements
    Runnable {
  @SuppressLint("SimpleDateFormat")
  private static final SimpleDateFormat ISO8601=
    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
  private ForecastAdapter adapter;
  private NWSInterface nws;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);

    Retrofit retrofit=
      new Retrofit.Builder()
        .baseUrl("https://api.weather.gov")
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    nws=retrofit.create(NWSInterface.class);
  }

  @Override
  public void onViewCreated(View view,
                            Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    run();
  }

  @Override
  public void onPause() {
    getListView().removeCallbacks(this);

    super.onPause();
  }

  @SuppressWarnings("MissingPermission")
  @Override
  public void run() {
    Location location=
      LocationServices.FusedLocationApi.getLastLocation(getPlayServices());

    if (location==null) {
      getListView().postDelayed(this, 1000);
    }
    else {
      fetchForecast(location);
    }
  }

  private GoogleApiClient getPlayServices() {
    return(((WeatherDemo)getActivity()).getPlayServices());
  }

  private void fetchForecast(Location location) {
    double roundedLat=(double)Math.round(location.getLatitude()*10000d)/10000d;
    double roundedLon=(double)Math.round(location.getLongitude()*10000d)/10000d;

    nws.getForecast(roundedLat, roundedLon)
      .enqueue(new Callback<WeatherResponse>() {
        @Override
        public void onResponse(Call<WeatherResponse> call,
                               Response<WeatherResponse> response) {
          if (response.code()==200) {
            adapter=new ForecastAdapter(response.body().properties.periods);
            setListAdapter(adapter);
          }
          else {
            Toast.makeText(getActivity(), R.string.msg_nws,
              Toast.LENGTH_LONG).show();
          }
        }

        @Override
        public void onFailure(Call<WeatherResponse> call, Throwable t) {
          Toast.makeText(getActivity(), t.getMessage(),
            Toast.LENGTH_LONG).show();
          Log.e(getClass().getSimpleName(),
            "Exception from Retrofit request to National Weather Service", t);
        }
      });
  }

  private class ForecastAdapter extends ArrayAdapter<WeatherResponse.Period> {
    private int size;
    private java.text.DateFormat dateFormat;
    private java.text.DateFormat timeFormat;

    ForecastAdapter(List<WeatherResponse.Period> items) {
      super(getActivity(), R.layout.row, R.id.date, items);

      size=getActivity()
        .getResources()
        .getDimensionPixelSize(R.dimen.icon);
      dateFormat=DateFormat.getDateFormat(getActivity());
      timeFormat=DateFormat.getTimeFormat(getActivity());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View row=super.getView(position, convertView, parent);
      WeatherResponse.Period item=getItem(position);

      if (!TextUtils.isEmpty(item.icon)) {
        ImageView icon=row.findViewById(R.id.icon);

        Picasso.with(getActivity()).load(item.icon)
          .resize(size, size).centerCrop().into(icon);
      }

      TextView title=row.findViewById(R.id.date);

      try {
        Date parsedStartTime=ISO8601.parse(item.startTime);
        String date=dateFormat.format(parsedStartTime);
        String time=timeFormat.format(parsedStartTime);

        title.setText(date+" "+time);
      }
      catch (ParseException e) {
        title.setText(item.startTime);
      }

      TextView temp=row.findViewById(R.id.temp);

      temp.setText(getString(R.string.temp, item.temperature,
        item.temperatureUnit));

      return(row);
    }
  }
}
