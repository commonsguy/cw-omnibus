/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Advanced Android Development_
    http://commonsware.com/AdvAndroid
*/

package com.commonsware.android.maptouch;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
    private Drawable marker=null;
    private OverlayItem inDrag=null;
    private ImageView dragImage=null;
    private int xDragImageOffset=0;
    private int yDragImageOffset=0;
    private int xDragTouchOffset=0;
    private int yDragTouchOffset=0;
    
    public SitesOverlay(Drawable marker) {
      super(marker);
      this.marker=marker;
      
      dragImage=(ImageView)findViewById(R.id.drag);
      xDragImageOffset=dragImage.getDrawable().getIntrinsicWidth()/2;
      yDragImageOffset=dragImage.getDrawable().getIntrinsicHeight();
      
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
    public void draw(Canvas canvas, MapView mapView,
                      boolean shadow) {
      super.draw(canvas, mapView, shadow);
      
      boundCenterBottom(marker);
    }
    
    @Override
    public int size() {
      return(items.size());
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event, MapView mapView) {
      final int action=event.getAction();
      final int x=(int)event.getX();
      final int y=(int)event.getY();
      boolean result=false;
      
      if (action==MotionEvent.ACTION_DOWN) {
        for (OverlayItem item : items) {
          Point p=new Point(0,0);
          
          map.getProjection().toPixels(item.getPoint(), p);
          
          if (hitTest(item, marker, x-p.x, y-p.y)) {
            result=true;
            inDrag=item;
            items.remove(inDrag);
            populate();

            xDragTouchOffset=0;
            yDragTouchOffset=0;
            
            setDragImagePosition(p.x, p.y);
            dragImage.setVisibility(View.VISIBLE);

            xDragTouchOffset=x-p.x;
            yDragTouchOffset=y-p.y;
            
            break;
          }
        }
      }
      else if (action==MotionEvent.ACTION_MOVE && inDrag!=null) {
        setDragImagePosition(x, y);
        result=true;
      }
      else if (action==MotionEvent.ACTION_UP && inDrag!=null) {
        dragImage.setVisibility(View.GONE);
        
        GeoPoint pt=map.getProjection().fromPixels(x-xDragTouchOffset,
                                                   y-yDragTouchOffset);
        OverlayItem toDrop=new OverlayItem(pt, inDrag.getTitle(),
                                           inDrag.getSnippet());
        
        items.add(toDrop);
        populate();
        
        inDrag=null;
        result=true;
      }
      
      return(result || super.onTouchEvent(event, mapView));
    }
    
    private void setDragImagePosition(int x, int y) {
      RelativeLayout.LayoutParams lp=
        (RelativeLayout.LayoutParams)dragImage.getLayoutParams();
            
      lp.setMargins(x-xDragImageOffset-xDragTouchOffset,
                      y-yDragImageOffset-yDragTouchOffset, 0, 0);
      dragImage.setLayoutParams(lp);
    }
  }
}