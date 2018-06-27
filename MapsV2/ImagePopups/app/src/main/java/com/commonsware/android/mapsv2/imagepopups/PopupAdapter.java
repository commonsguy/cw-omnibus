/***
  Copyright (c) 2013-2014 CommonsWare, LLC
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

package com.commonsware.android.mapsv2.imagepopups;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.HashMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

class PopupAdapter implements InfoWindowAdapter {
  private View popup=null;
  private LayoutInflater inflater=null;
  private HashMap<String, Uri> images=null;
  private Context ctxt=null;
  private int iconWidth=-1;
  private int iconHeight=-1;
  private Marker lastMarker=null;

  PopupAdapter(Context ctxt, LayoutInflater inflater,
               HashMap<String, Uri> images) {
    this.ctxt=ctxt;
    this.inflater=inflater;
    this.images=images;

    iconWidth=
        ctxt.getResources().getDimensionPixelSize(R.dimen.icon_width);
    iconHeight=
        ctxt.getResources().getDimensionPixelSize(R.dimen.icon_height);
  }

  @Override
  public View getInfoWindow(Marker marker) {
    return(null);
  }

  @SuppressLint("InflateParams")
  @Override
  public View getInfoContents(Marker marker) {
    if (popup == null) {
      popup=inflater.inflate(R.layout.popup, null);
    }

    if (lastMarker == null
        || !lastMarker.getId().equals(marker.getId())) {
      lastMarker=marker;

      TextView tv=(TextView)popup.findViewById(R.id.title);

      tv.setText(marker.getTitle());
      tv=(TextView)popup.findViewById(R.id.snippet);
      tv.setText(marker.getSnippet());

      Uri image=images.get(marker.getId());
      ImageView icon=(ImageView)popup.findViewById(R.id.icon);

      if (image == null) {
        icon.setVisibility(View.GONE);
      }
      else {
        icon.setVisibility(View.VISIBLE);
        Picasso.with(ctxt).load(image).resize(iconWidth, iconHeight)
               .centerCrop().noFade()
               .placeholder(R.drawable.placeholder)
               .into(icon, new MarkerCallback(marker));
      }
    }

    return(popup);
  }

  static class MarkerCallback implements Callback {
    Marker marker=null;

    MarkerCallback(Marker marker) {
      this.marker=marker;
    }

    @Override
    public void onError() {
      Log.e(getClass().getSimpleName(), "Error loading thumbnail!");
    }

    @Override
    public void onSuccess() {
      if (marker != null && marker.isInfoWindowShown()) {
        marker.showInfoWindow();
      }
    }
  }
}