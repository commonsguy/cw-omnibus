/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
	
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
*/
   
package com.commonsware.android.maps;

import java.util.ArrayList;
import java.util.List;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

public class NooYawk extends MapActivity {
  private MapView map=null;
  private MyLocationOverlay me=null;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    
    map=(MapView)findViewById(R.id.map);
    
    map.getController().setCenter(getPoint(40.76793169992044,
                                            -73.98180484771729));
    map.getController().setZoom(17);
    map.setBuiltInZoomControls(true);
    
    Drawable marker=getResources().getDrawable(R.drawable.marker);
    
    marker.setBounds(0, 0, marker.getIntrinsicWidth(),
                            marker.getIntrinsicHeight());
    
    map.getOverlays().add(new SitesOverlay(marker));
    
    me=new MyLocationOverlay(this, map);
    map.getOverlays().add(me);
  }
  
  @Override
  public void onResume() {
    super.onResume();
    
    me.enableCompass();
  }   
  
  @Override
  public void onPause() {
    super.onPause();
    
    me.disableCompass();
  }   
  
  @Override
  protected boolean isRouteDisplayed() {
    return(false);
  }
  
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_S) {
      map.setSatellite(!map.isSatellite());
      return(true);
    }
    else if (keyCode == KeyEvent.KEYCODE_Z) {
      map.displayZoomControls(true);
      return(true);
    }
    
    return(super.onKeyDown(keyCode, event));
  }

  private GeoPoint getPoint(double lat, double lon) {
    return(new GeoPoint((int)(lat*1000000.0),
                          (int)(lon*1000000.0)));
  }
    
  private class SitesOverlay extends ItemizedOverlay<OverlayItem> {
    private List<OverlayItem> items=new ArrayList<OverlayItem>();
    
    public SitesOverlay(Drawable marker) {
      super(marker);
      
      boundCenterBottom(marker);
      
      items.add(new OverlayItem(getPoint(40.748963847316034,
                                          -73.96807193756104),
                                "UN", "United Nations"));
      items.add(new OverlayItem(getPoint(40.76866299974387,
                                          -73.98268461227417),
                                "Lincoln Center",
                                "Home of Jazz at Lincoln Center"));
      items.add(new OverlayItem(getPoint(40.765136435316755,
                                          -73.97989511489868),
                                "Carnegie Hall",
              "Where you go with practice, practice, practice"));
      items.add(new OverlayItem(getPoint(40.70686417491799,
                                          -74.01572942733765),
                                "The Downtown Club",
                        "Original home of the Heisman Trophy"));

      populate();
    }
    
    @Override
    protected OverlayItem createItem(int i) {
      return(items.get(i));
    }
    
    @Override
    protected boolean onTap(int i) {
      Toast.makeText(NooYawk.this,
                      items.get(i).getSnippet(),
                      Toast.LENGTH_SHORT).show();
      
      return(true);
    }
    
    @Override
    public int size() {
      return(items.size());
    }
  }
}
