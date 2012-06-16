/***
	Copyright (c) 2008-2011 CommonsWare, LLC
	
	Licensed under the Apache License, Version 2.0 (the "License"); you may
	not use this file except in compliance with the License. You may obtain
	a copy of the License at
		http://www.apache.org/licenses/LICENSE-2.0
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */

package com.commonsware.android.weather;

import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.webkit.WebView;

public class WeatherDemo extends Activity {
	private WebView browser;
	private LocationManager mgr=null;
	private State state=null;
	private boolean isConfigurationChanging=false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		browser=(WebView)findViewById(R.id.webkit);
		state=(State)getLastNonConfigurationInstance();

		if (state==null) {
			state=new State();

			Intent i=new Intent(this, WeatherService.class);

			getApplicationContext().bindService(i, state.svcConn,
																					BIND_AUTO_CREATE);
		}
		else if (state.lastForecast!=null) {
			showForecast();
		}

		state.attach(this);

		mgr=(LocationManager)getSystemService(LOCATION_SERVICE);
		mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3600000,
															 1000, onLocationChange);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// BUG: we should be removing location updates
		// somewhere...

		if (!isConfigurationChanging) {
			getApplicationContext().unbindService(state.svcConn);
		}
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		isConfigurationChanging=true;

		return(state);
	}

	private void goBlooey(Throwable t) {
		AlertDialog.Builder builder=new AlertDialog.Builder(this);

		builder.setTitle("Exception!").setMessage(t.toString())
					 .setPositiveButton("OK", null).show();
	}

	static String generatePage(ArrayList<Forecast> forecasts) {
		StringBuilder bufResult=new StringBuilder("<html><body><table>");

		bufResult.append("<tr><th width=\"50%\">Time</th>"
				+"<th>Temperature</th><th>Forecast</th></tr>");

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

	void showForecast() {
		browser.loadDataWithBaseURL(null, state.lastForecast, "text/html",
																"UTF-8", null);
	}

	LocationListener onLocationChange=new LocationListener() {
		public void onLocationChanged(Location location) {
			if (state.weather!=null) {
				state.weather.getForecast(location, state);
			}
			else {
				Log.w(getClass().getName(),
							"Unable to fetch forecast -- no WeatherBinder");
			}
		}

		public void onProviderDisabled(String provider) {
			// required for interface, not used
		}

		public void onProviderEnabled(String provider) {
			// required for interface, not used
		}

		public void onStatusChanged(String provider, int status,
																Bundle extras) {
			// required for interface, not used
		}
	};

	// BUG: not using a static inner class for state

	class State implements WeatherListener {
		WeatherBinder weather=null;
		WeatherDemo activity=null;
		String lastForecast=null;

		void attach(WeatherDemo activity) {
			this.activity=activity;
		}

		public void updateForecast(ArrayList<Forecast> forecast) {
			lastForecast=generatePage(forecast);
			activity.showForecast();
		}

		public void handleError(Exception e) {
			activity.goBlooey(e);
		}

		ServiceConnection svcConn=new ServiceConnection() {
			public void onServiceConnected(ComponentName className,
																		 IBinder rawBinder) {
				state.weather=(WeatherBinder)rawBinder;
			}

			public void onServiceDisconnected(ComponentName className) {
				state.weather=null;
			}
		};
	}
}