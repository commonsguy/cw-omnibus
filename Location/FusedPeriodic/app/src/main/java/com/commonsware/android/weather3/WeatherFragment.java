/***
  Copyright (c) 2013-2015 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.weather3;

import android.app.ListFragment;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.squareup.picasso.Picasso;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class WeatherFragment extends ListFragment implements
    ResultCallback<LocationSettingsResult>,
    LocationListener {
  static final int SETTINGS_REQUEST_ID=1338;
  private String template=null;
  private LocationRequest request=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);

    template=getActivity().getString(R.string.url);
    request=new LocationRequest()
              .setNumUpdates(1)
              .setExpirationDuration(60000)
              .setInterval(1000)
              .setPriority(LocationRequest.PRIORITY_LOW_POWER);
  }

  @Override
  public void onPause() {
    LocationServices.FusedLocationApi.removeLocationUpdates(
      getPlayServices(), this);

    super.onPause();
  }

  @Override
  public void onViewCreated(View view,
                            Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    requestSettings();
  }

  @Override
  public void onResult(LocationSettingsResult result) {
    boolean thingsPlumbBusted=true;

    switch(result.getStatus().getStatusCode()) {
      case LocationSettingsStatusCodes.SUCCESS:
        requestLocations();
        thingsPlumbBusted=false;
        break;

      case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
        try {
          result
            .getStatus()
            .startResolutionForResult(getActivity(),
                                      SETTINGS_REQUEST_ID);
          thingsPlumbBusted=false;
        }
        catch (IntentSender.SendIntentException e) {
          // oops
        }
        break;

      case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
        // more oops
        break;
    }

    if (thingsPlumbBusted) {
      Toast
        .makeText(getActivity(),
                  R.string.settings_resolution_fail_msg,
                  Toast.LENGTH_LONG)
        .show();
      getActivity().finish();
    }
  }

  @Override
  public void onLocationChanged(Location location) {
    new FetchForecastTask()
        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, location);
  }

  private GoogleApiClient getPlayServices() {
    return(((WeatherDemo)getActivity()).getPlayServices());
  }

  private void requestSettings() {
    LocationSettingsRequest.Builder b=
        new LocationSettingsRequest.Builder()
              .addLocationRequest(request);
    PendingResult<LocationSettingsResult> result=
        LocationServices.SettingsApi.checkLocationSettings(getPlayServices(),
                                                            b.build());

    result.setResultCallback(this);
  }

  void requestLocations() {
    PendingResult<Status> result=
        LocationServices.FusedLocationApi
          .requestLocationUpdates(getPlayServices(), request, this);

    result.setResultCallback(new ResultCallback<Status>() {
      @Override
      public void onResult(Status status) {
        if (status.isSuccess()) {
          Toast
              .makeText(getActivity(),
                  R.string.location_req_success_msg,
                  Toast.LENGTH_LONG)
              .show();
        } else {
          Toast
              .makeText(getActivity(), status.getStatusMessage(),
                  Toast.LENGTH_LONG)
              .show();
          getActivity().finish();
        }
      }
    });
  }

  private String getForecastXML(String path) throws IOException {
    BufferedReader reader=null;
    URL url=new URL(path);
    HttpURLConnection c=(HttpURLConnection)url.openConnection();

    try {
      reader=
          new BufferedReader(new InputStreamReader(c.getInputStream()));

      StringBuilder buf=new StringBuilder();
      String line=null;

      while ((line=reader.readLine()) != null) {
        buf.append(line);
        buf.append('\n');
      }

      return(buf.toString());
    }
    finally {
      if (reader != null) {
        reader.close();
      }
      
      c.disconnect();
    }
  }

  private ArrayList<Forecast> buildForecasts(String raw)
                                                        throws Exception {
    ArrayList<Forecast> forecasts=new ArrayList<Forecast>();
    DocumentBuilder builder=
        DocumentBuilderFactory.newInstance().newDocumentBuilder();
    Document doc=builder.parse(new InputSource(new StringReader(raw)));
    NodeList times=doc.getElementsByTagName("start-valid-time");

    for (int i=0; i < times.getLength(); i++) {
      Element time=(Element)times.item(i);
      Forecast forecast=new Forecast();

      forecasts.add(forecast);
      forecast.setTime(time.getFirstChild().getNodeValue());
    }

    NodeList temps=doc.getElementsByTagName("value");

    for (int i=0; i < temps.getLength(); i++) {
      Element temp=(Element)temps.item(i);
      Forecast forecast=forecasts.get(i);

      forecast.setTemp(Integer.valueOf(temp.getFirstChild()
                                           .getNodeValue()));
    }

    NodeList icons=doc.getElementsByTagName("icon-link");

    for (int i=0; i < icons.getLength(); i++) {
      Element icon=(Element)icons.item(i);
      Forecast forecast=forecasts.get(i);

      forecast.setIcon(icon.getFirstChild().getNodeValue());
    }

    return(forecasts);
  }

  private class FetchForecastTask extends AsyncTask<Location, Void, List<Forecast>> {
    private Exception e=null;

    @Override
    protected List<Forecast> doInBackground(Location... locs) {
      try {
        Location loc=locs[0];
        String url=
            String.format(template, loc.getLatitude(),
                          loc.getLongitude());

        return(buildForecasts(getForecastXML(url)));
      }
      catch (Exception e) {
        this.e=e;
      }

      return(null);
    }

    @Override
    protected void onPostExecute(List<Forecast> forecasts) {
      if (e == null) {
        setListAdapter(new ForecastAdapter(forecasts));
      }
      else {
        Log.e(getClass().getSimpleName(), "Exception fetching data", e);
        Toast.makeText(getActivity(),
                       String.format(getString(R.string.error),
                                     e.toString()), Toast.LENGTH_LONG)
             .show();
      }
    }
  }

  private class ForecastAdapter extends ArrayAdapter<Forecast> {
    int size;

    ForecastAdapter(List<Forecast> items) {
      super(getActivity(), R.layout.row, R.id.date, items);

      size=
          getActivity().getResources()
              .getDimensionPixelSize(R.dimen.icon);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View row=super.getView(position, convertView, parent);
      Forecast item=getItem(position);
      ImageView icon=(ImageView)row.findViewById(R.id.icon);

      Picasso.with(getActivity()).load(item.getIcon())
          .resize(size, size).centerCrop().into(icon);

      TextView title=(TextView)row.findViewById(R.id.date);

      title.setText(item.getTime());

      TextView temp=(TextView)row.findViewById(R.id.temp);

      temp.setText("Temperature: "+String.valueOf(item.getTemp())+"F");

      return(row);
    }
  }
}
