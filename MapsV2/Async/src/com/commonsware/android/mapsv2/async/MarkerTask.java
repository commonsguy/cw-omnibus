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

package com.commonsware.android.mapsv2.async;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONObject;

class MarkerTask extends AsyncTask<String, Void, Void> {
  private AssetManager assets=null;
  private Exception e=null;
  private GoogleMap map;
  private ArrayList<MarkerOptions> markers=
      new ArrayList<MarkerOptions>();

  MarkerTask(Context ctxt, GoogleMap map) {
    assets=ctxt.getAssets();
    this.map=map;
  }

  @Override
  protected Void doInBackground(String... params) {
    try {
      InputStream raw=assets.open(params[0]);
      BufferedReader in=
          new BufferedReader(new InputStreamReader(raw));
      String str;
      StringBuilder buf=new StringBuilder();

      while ((str=in.readLine()) != null) {
        buf.append(str);
      }

      in.close();

      JSONArray jsonArray=new JSONArray(buf.toString());

      for (int i=0; i < jsonArray.length(); i++) {
        JSONObject json=jsonArray.getJSONObject(i);
        LatLng location=
            new LatLng(json.getDouble("lat"), json.getDouble("lon"));
        
        markers.add(new MarkerOptions().position(location)
                                       .title(json.getString("title"))
                                       .snippet(json.getString("snippet")));
      }
    }
    catch (Exception e) {
      this.e=e;
    }

    return(null);
  }

  @Override
  public void onPostExecute(Void unused) {
    for (MarkerOptions marker : markers) {
      map.addMarker(marker);
    }

    if (e != null) {
      Log.e(getClass().getSimpleName(), "Exception parsing JSON", e);
    }
  }
}