package com.commonsware.android.weather.tasks;

import info.juanmendez.android.utils.Trace;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.commonsware.android.weather.R;
import com.commonsware.android.weather.WeatherFragment;

public class FetchForecastTask extends AsyncTask<Location, Void, String>
{
	Exception e = null;
	private WeatherFragment f;

	public FetchForecastTask( WeatherFragment w )
	{
		f = w;
	}

	@Override
	protected String doInBackground(Location... locs)
	{
		String page = null;
		
		try
		{
			Location loc = locs[0];
			Trace.warn( String.format("locationChange", loc.getLatitude(), loc.getLongitude()), this );
			String url = String.format( f.getTemplate(), loc.getLatitude(), loc.getLongitude());

			page = f.generatePage( url );
		}
		catch (Exception e)
		{
			this.e = e;
		}

		return (page);
	}

	@Override
	protected void onPostExecute(String page)
	{
		if (e == null)
		{
			f.getWebView().loadDataWithBaseURL(null, page, "text/html",
					"UTF-8", null);
		}
		else
		{
			Log.e(getClass().getSimpleName(), "Exception fetching data", e);
			Toast.makeText( f.getActivity(),
					String.format( f.getString(R.string.error), e.toString()),
					Toast.LENGTH_LONG).show();
		}
	}
}
