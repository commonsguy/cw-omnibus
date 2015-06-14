/***
  Copyright (c) 2012-2014 CommonsWare, LLC
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

package com.commonsware.android.bitmap.iss;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.IOException;

public class BitmapFragment extends Fragment {
  private static final String KEY_SAMPLE_SIZE="inSampleSize";
  private AssetManager assets=null;

  static BitmapFragment newInstance(int inSampleSize) {
    BitmapFragment frag=new BitmapFragment();
    Bundle args=new Bundle();

    args.putInt(KEY_SAMPLE_SIZE, inSampleSize);
    frag.setArguments(args);

    return(frag);
  }

  static String getTitle(Context ctxt, int inSampleSize) {
    return(String.format(ctxt.getString(R.string.title), inSampleSize));
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    View result=inflater.inflate(R.layout.sample, container, false);
    int inSampleSize=getArguments().getInt(KEY_SAMPLE_SIZE, 1);

    try {
      Bitmap flower=
          load("Tibouchina_urvilleana_flower_ja.jpg", inSampleSize);
      Bitmap logo=load("square.png", inSampleSize);

      ImageView iv=(ImageView)result.findViewById(R.id.flower_large);

      iv.setImageBitmap(flower);
      iv=(ImageView)result.findViewById(R.id.flower_small);
      iv.setImageBitmap(flower);
      iv=(ImageView)result.findViewById(R.id.logo_large);
      iv.setImageBitmap(logo);
      iv=(ImageView)result.findViewById(R.id.logo_small);
      iv.setImageBitmap(logo);

      TextView tv=(TextView)result.findViewById(R.id.byte_count);

      tv.setText(String.valueOf(byteCount(flower)));
    }
    catch (IOException e) {
      Log.e(getClass().getSimpleName(), "Exception loading bitmap", e);
    }

    return(result);
  }

  private Bitmap load(String path, int inSampleSize) throws IOException {
    BitmapFactory.Options opts=new BitmapFactory.Options();

    opts.inSampleSize=inSampleSize;

    return(BitmapFactory.decodeStream(assets().open(path), null, opts));
  }

  private AssetManager assets() {
    if (assets == null) {
      assets=getActivity().getResources().getAssets();
    }

    return(assets);
  }

  @TargetApi(Build.VERSION_CODES.KITKAT)
  private int byteCount(Bitmap b) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      return(b.getAllocationByteCount());
    }

    return(b.getByteCount());
  }
}