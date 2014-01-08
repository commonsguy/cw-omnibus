/***
  Copyright (c) 2012 CommonsWare, LLC
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

package com.commonsware.android.weather;

import info.juanmendez.android.utils.AsyncUtils;
import info.juanmendez.android.utils.Trace;
import info.juanmendez.android.views.WebViewFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.commonsware.android.weather.tasks.FetchForecastTask;

public class WeatherFragment extends WebViewFragment implements LocationListener
{
	
	private LocationManager mgr = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		
		mgr = (LocationManager) getActivity().getSystemService( Context.LOCATION_SERVICE);
	}

	@Override
	public void onResume()
	{
		super.onResume();

		Trace.warn( "onResume", this );
		
		mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3600000, 1000, this);
	}

	@Override
	public void onPause()
	{
		super.onPause();

		Trace.warn( "onPause", this );
		mgr.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location)
	{
		Trace.warn( "onLocationChanged", this );
		FetchForecastTask task = new FetchForecastTask( this );
		AsyncUtils.execute(task, location );
		//task.execute(location);
	}

	@Override
	public void onProviderDisabled(String provider)
	{
		// required for interface, not used
	}

	@Override
	public void onProviderEnabled(String provider)
	{
		// required for interface, not used
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		// required for interface, not used
	}
	
	public String getTemplate()
	{
		return getActivity().getString(R.string.url);
	}

	private String getForecastXML(String path) throws IOException
	{
		BufferedReader reader = null;

		try
		{
			URL url = new URL(path);
			HttpURLConnection c = (HttpURLConnection) url.openConnection();

			c.setRequestMethod("GET");
			c.setReadTimeout(15000);
			c.connect();

			reader = new BufferedReader(new InputStreamReader(
					c.getInputStream()));

			StringBuilder buf = new StringBuilder();
			String line = null;

			while ((line = reader.readLine()) != null)
			{
				buf.append(line + "\n");
			}

			return (buf.toString());
		}
		finally
		{
			if (reader != null)
			{
				reader.close();
			}
		}
	}

	private ArrayList<Forecast> buildForecasts(String raw) throws Exception
	{
		ArrayList<Forecast> forecasts = new ArrayList<Forecast>();
		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(raw)));
		NodeList times = doc.getElementsByTagName("start-valid-time");

		for (int i = 0; i < times.getLength(); i++)
		{
			Element time = (Element) times.item(i);
			Forecast forecast = new Forecast();

			forecasts.add(forecast);
			forecast.setTime(time.getFirstChild().getNodeValue());
		}

		NodeList temps = doc.getElementsByTagName("value");

		for (int i = 0; i < temps.getLength(); i++)
		{
			Element temp = (Element) temps.item(i);
			Forecast forecast = forecasts.get(i);

			forecast.setTemp(new Integer(temp.getFirstChild().getNodeValue()));
		}

		NodeList icons = doc.getElementsByTagName("icon-link");

		for (int i = 0; i < icons.getLength(); i++)
		{
			Element icon = (Element) icons.item(i);
			Forecast forecast = forecasts.get(i);

			forecast.setIcon(icon.getFirstChild().getNodeValue());
		}

		return (forecasts);
	}
	
	public String generatePage( String url ) throws IOException, Exception
	{
		return generatePage( buildForecasts( getForecastXML(url)));
	}

	private String generatePage(ArrayList<Forecast> forecasts)
	{
		StringBuilder bufResult = new StringBuilder("<html><body><table>");

		bufResult.append("<tr><th width=\"50%\">Time</th>"
				+ "<th>Temperature</th><th>Forecast</th></tr>");

		for (Forecast forecast : forecasts)
		{
			bufResult.append("<tr><td align=\"center\">");
			bufResult.append(forecast.getTime());
			bufResult.append("</td><td align=\"center\">");
			bufResult.append(forecast.getTemp());
			bufResult.append("</td><td><img src=\"");
			bufResult.append(forecast.getIcon());
			bufResult.append("\"></td></tr>");
		}

		bufResult.append("</table></body></html>");

		return (bufResult.toString());
	}	
}
