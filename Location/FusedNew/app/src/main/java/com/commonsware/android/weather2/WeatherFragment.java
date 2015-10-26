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

package com.commonsware.android.weather2;

import android.app.ListFragment;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
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
    Runnable {
  private String template=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);

    template=getActivity().getString(R.string.url);
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

  @Override
  public void run() {
    Location loc=LocationServices.FusedLocationApi
                    .getLastLocation(getPlayServices());

    if (loc == null) {
      getListView().postDelayed(this, 1000);
    }
    else {
      FetchForecastTask task=new FetchForecastTask();

      task.execute(loc);
    }
  }

  private GoogleApiClient getPlayServices() {
    return(((WeatherDemo)getActivity()).getPlayServices());
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

      if (!TextUtils.isEmpty(item.getIcon())) {
        ImageView icon=(ImageView)row.findViewById(R.id.icon);

        Picasso.with(getActivity()).load(item.getIcon())
          .resize(size, size).centerCrop().into(icon);
      }

      TextView title=(TextView)row.findViewById(R.id.date);

      title.setText(item.getTime());

      TextView temp=(TextView)row.findViewById(R.id.temp);

      temp.setText("Temperature: "+String.valueOf(item.getTemp())+"F");

      return(row);
    }
  }
}
