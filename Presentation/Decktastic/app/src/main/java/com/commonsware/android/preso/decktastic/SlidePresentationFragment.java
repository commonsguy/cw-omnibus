/***
  Copyright (c) 2013-14 CommonsWare, LLC
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

package com.commonsware.android.preso.decktastic;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

public class SlidePresentationFragment extends PresentationFragment {
  private static final String KEY_URI="u";
  private ImageView slide=null;

  public static SlidePresentationFragment newInstance(Context ctxt,
                                                      Display display,
                                                      Uri slideUri) {
    SlidePresentationFragment frag=new SlidePresentationFragment();

    frag.setDisplay(ctxt, display);

    Bundle b=new Bundle();

    b.putParcelable(KEY_URI, slideUri);
    frag.setArguments(b);

    return(frag);
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    slide=new ImageView(getContext());

    setSlideContent((Uri)getArguments().getParcelable(KEY_URI));

    return(slide);
  }

  void setSlideContent(Uri slideUri) {
    Picasso.with(getContext()).load(slideUri).into(slide);
  }
}
