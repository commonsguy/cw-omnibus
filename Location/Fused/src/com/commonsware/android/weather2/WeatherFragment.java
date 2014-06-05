/***
  Copyright (c) 2013 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
 */

package com.commonsware.android.weather2;

import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebViewFragment;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class WeatherFragment extends WebViewFragment implements
    GooglePlayServicesClient.ConnectionCallbacks,
    GooglePlayServicesClient.OnConnectionFailedListener, Runnable {
  private static final int REQUEST_ID=1337;
  private String template=null;
  private LocationClient client=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);

    template=getActivity().getString(R.string.url);
    client=new LocationClient(getActivity(), this, this);
  }

  @Override
  public void onResume() {
    super.onResume();

    client.connect();
  }

  @Override
  public void onPause() {
    getWebView().removeCallbacks(this);
    client.disconnect();

    super.onPause();
  }

  @Override
  public void onConnected(Bundle undocumented) {
    run();
  }

  @Override
  public void onDisconnected() {
    // unused
  }

  @Override
  public void onConnectionFailed(ConnectionResult result) {
    boolean anyLuck=false;

    if (result.hasResolution()) {
      try {
        result.startResolutionForResult(getActivity(), REQUEST_ID);
        anyLuck=true;
      }
      catch (IntentSender.SendIntentException e) {
        Log.e(getClass().getSimpleName(),
              "Exception trying to startResolutionForResult()", e);
      }
    }

    if (!anyLuck) {
      Toast.makeText(getActivity(), R.string.no_fused,
                     Toast.LENGTH_LONG).show();
      getActivity().finish();
    }
  }

  @Override
  public void run() {
    Location loc=client.getLastLocation();

    if (loc == null) {
      getWebView().postDelayed(this, 1000);
    }
    else {
      FetchForecastTask task=new FetchForecastTask();

      task.execute(loc);
    }
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
        buf.append(line + "\n");
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

  String generatePage(ArrayList<Forecast> forecasts) {
    StringBuilder bufResult=new StringBuilder("<html><body><table>");

    bufResult.append("<tr><th width=\"50%\">Time</th>"
        + "<th>Temperature</th><th>Forecast</th></tr>");

    for (Forecast forecast : forecasts) {
      bufResult.append("<tr><td align=\"center\">");
      bufResult.append(forecast.getTime());
      bufResult.append("</td><td align=\"center\">");
      bufResult.append(forecast.getTemp());
      bufResult.append("</td><td><img src=\"");
      bufResult.append(forecast.getIcon());
      bufResult.append("\"></td></tr>");
    }

    bufResult.append("</table></body></html>");

    return(bufResult.toString());
  }

  class FetchForecastTask extends AsyncTask<Location, Void, String> {
    Exception e=null;

    @Override
    protected String doInBackground(Location... locs) {
      String page=null;

      try {
        Location loc=locs[0];
        String url=
            String.format(template, loc.getLatitude(),
                          loc.getLongitude());

        page=generatePage(buildForecasts(getForecastXML(url)));
      }
      catch (Exception e) {
        this.e=e;
      }

      return(page);
    }

    @Override
    protected void onPostExecute(String page) {
      if (e == null) {
        getWebView().loadDataWithBaseURL(null, page, "text/html",
                                         "UTF-8", null);
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
}
