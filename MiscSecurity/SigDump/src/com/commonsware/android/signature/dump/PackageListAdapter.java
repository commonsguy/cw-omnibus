/***
  Copyright (c) 2013 CommonsWare, LLC
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

package com.commonsware.android.signature.dump;

import android.annotation.TargetApi;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

class PackageListAdapter extends ArrayAdapter<PackageInfo> {
  PackageListAdapter(PackagesFragment packagesFragment) {
    super(packagesFragment.getActivity(), getRowResourceId(),
          packagesFragment.getContract().getPackageList());
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View result=super.getView(position, convertView, parent);

    ((TextView)result).setText(getItem(position).packageName);

    return(result);
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  private static int getRowResourceId() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      return(android.R.layout.simple_list_item_activated_1);
    }

    return(android.R.layout.simple_list_item_1);
  }
}