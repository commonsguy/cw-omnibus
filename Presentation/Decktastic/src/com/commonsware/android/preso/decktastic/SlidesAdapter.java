/***
  Copyright (c) 2013-14 CommonsWare, LLC
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

package com.commonsware.android.preso.decktastic;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

class SlidesAdapter extends PagerAdapter {
  private PresoContents preso;
  private Context ctxt;

  SlidesAdapter(Context ctxt, PresoContents preso) {
    this.ctxt=ctxt;
    this.preso=preso;
  }

  @Override
  public Object instantiateItem(ViewGroup container, int position) {
    ImageView page=new ImageView(ctxt);

    container.addView(page,
                      new ViewGroup.LayoutParams(
                                                 ViewGroup.LayoutParams.MATCH_PARENT,
                                                 ViewGroup.LayoutParams.MATCH_PARENT));

    Picasso.with(ctxt).load(getSlideImageUri(position)).into(page);

    return(page);
  }

  @Override
  public void destroyItem(ViewGroup container, int position,
                          Object object) {
    container.removeView((View)object);
  }

  @Override
  public int getCount() {
    return(preso.slides.size());
  }

  @Override
  public boolean isViewFromObject(View view, Object object) {
    return(view == object);
  }

  @Override
  public String getPageTitle(int position) {
    return(preso.getSlideTitle(position));
  }
  
  Uri getSlideImageUri(int position) {
    return(Uri.fromFile(preso.getSlideImage(position)));
  }
}
