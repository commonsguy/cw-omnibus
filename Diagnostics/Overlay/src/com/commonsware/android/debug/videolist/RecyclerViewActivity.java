/***
 Copyright (c) 2008-2015 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 From _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.debug.videolist;

import android.app.Activity;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;

public class RecyclerViewActivity extends Activity {
  private RecyclerView rv=null;

  public void setAdapter(RecyclerView.Adapter adapter) {
    boolean canDrawOverlays=
        (Build.VERSION.SDK_INT<=Build.VERSION_CODES.LOLLIPOP_MR1);

    if (!canDrawOverlays) {
      canDrawOverlays=Settings.canDrawOverlays(this);
    }

    if (BuildConfig.DEBUG && canDrawOverlays) {
      adapter=new TimingWrapper(adapter, this);
    }

    getRecyclerView().setAdapter(adapter);
  }

  public RecyclerView.Adapter getAdapter() {
    RecyclerView.Adapter result=getRecyclerView().getAdapter();

    if (result instanceof RVAdapterWrapper) {
      result=((RVAdapterWrapper)result).getWrappedAdapter();
    }

    return(result);
  }

  public void setLayoutManager(RecyclerView.LayoutManager mgr) {
    getRecyclerView().setLayoutManager(mgr);
  }

  public RecyclerView getRecyclerView() {
    if (rv==null) {
      rv=new RecyclerView(this);
      setContentView(rv);
    }

    return(rv);
  }
}
