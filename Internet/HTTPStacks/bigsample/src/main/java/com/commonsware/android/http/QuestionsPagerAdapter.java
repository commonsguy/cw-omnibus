/***
  Copyright (c) 2012-15 CommonsWare, LLC
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

package com.commonsware.android.http;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;
import com.commonsware.android.http.impl.HURLQuestionsFragment;
import com.commonsware.android.http.impl.HURLTorQuestionsFragment;
import com.commonsware.android.http.impl.HttpClientQuestionsFragment;
import com.commonsware.android.http.impl.HttpClientTorQuestionsFragment;
import com.commonsware.android.http.impl.OkHttp3QuestionsFragment;
import com.commonsware.android.http.impl.OkHttp3TorQuestionsFragment;
import com.commonsware.android.http.impl.RetrofitQuestionsFragment;
import com.commonsware.android.http.impl.RetrofitTorQuestionsFragment;
import com.commonsware.android.http.impl.VolleyQuestionsFragment;
import com.commonsware.android.http.impl.VolleyTorQuestionsFragment;

public class QuestionsPagerAdapter extends FragmentPagerAdapter {
  private static final int[] TITLES={
    R.string.title_hurl,
    R.string.title_hurl_tor,
    R.string.title_httpclient,
    R.string.title_httpclient_tor,
    R.string.title_volley,
    R.string.title_volley_tor,
    R.string.title_okhttp3,
    R.string.title_okhttp3_tor,
    R.string.title_retrofit,
    R.string.title_retrofit_tor
  };
  private final Context ctxt;

  public QuestionsPagerAdapter(Context ctxt, FragmentManager mgr) {
    super(mgr);

    this.ctxt=ctxt;
  }

  @Override
  public int getCount() {
    return(10);
  }

  @Override
  public Fragment getItem(int position) {
    Fragment result=null;

    switch(position) {
      case 0:
        result=new HURLQuestionsFragment();
        break;

      case 1:
        result=new HURLTorQuestionsFragment();
        break;

      case 2:
        result=new HttpClientQuestionsFragment();
        break;

      case 3:
        result=new HttpClientTorQuestionsFragment();
        break;

      case 4:
        result=new VolleyQuestionsFragment();
        break;

      case 5:
        result=new VolleyTorQuestionsFragment();
        break;

      case 6:
        result=new OkHttp3QuestionsFragment();
        break;

      case 7:
        result=new OkHttp3TorQuestionsFragment();
        break;

      case 8:
        result=new RetrofitQuestionsFragment();
        break;

      case 9:
        result=new RetrofitTorQuestionsFragment();
        break;
    }

    return(result);
  }

  @Override
  public String getPageTitle(int position) {
    return(ctxt.getString(TITLES[position]));
  }
}